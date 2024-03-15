package cn.bootx.platform.daxpay.service.core.payment.sync.service;

import cn.bootx.platform.common.core.exception.BizException;
import cn.bootx.platform.common.core.exception.RepetitiveOperationException;
import cn.bootx.platform.daxpay.code.RefundStatusEnum;
import cn.bootx.platform.daxpay.code.RefundSyncStatusEnum;
import cn.bootx.platform.daxpay.exception.pay.PayFailureException;
import cn.bootx.platform.daxpay.param.pay.RefundSyncParam;
import cn.bootx.platform.daxpay.result.pay.SyncResult;
import cn.bootx.platform.daxpay.service.code.PayRepairSourceEnum;
import cn.bootx.platform.daxpay.service.code.PaymentTypeEnum;
import cn.bootx.platform.daxpay.service.code.RefundRepairWayEnum;
import cn.bootx.platform.daxpay.service.common.context.RepairLocal;
import cn.bootx.platform.daxpay.service.common.local.PaymentContextLocal;
import cn.bootx.platform.daxpay.service.core.order.refund.dao.RefundOrderManager;
import cn.bootx.platform.daxpay.service.core.order.refund.entity.RefundOrder;
import cn.bootx.platform.daxpay.service.core.payment.repair.result.RefundRepairResult;
import cn.bootx.platform.daxpay.service.core.payment.repair.service.RefundRepairService;
import cn.bootx.platform.daxpay.service.core.payment.sync.factory.RefundSyncStrategyFactory;
import cn.bootx.platform.daxpay.service.core.payment.sync.result.RefundGatewaySyncResult;
import cn.bootx.platform.daxpay.service.core.record.sync.entity.PaySyncRecord;
import cn.bootx.platform.daxpay.service.core.record.sync.service.PaySyncRecordService;
import cn.bootx.platform.daxpay.service.func.AbsRefundSyncStrategy;
import com.baomidou.lock.LockInfo;
import com.baomidou.lock.LockTemplate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

/**
 * 支付退款同步服务类
 * @author xxm
 * @since 2024/1/29
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RefundSyncService {
    private final RefundOrderManager refundOrderManager;

    private final PaySyncRecordService paySyncRecordService;

    private final RefundRepairService repairService;

    private final LockTemplate lockTemplate;

    /**
     * 退款同步, 开启一个新的事务, 不受外部抛出异常的影响
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public SyncResult sync(RefundSyncParam param){
        // 先获取退款单
        RefundOrder refundOrder;
        if (Objects.nonNull(param.getRefundId())){
            refundOrder = refundOrderManager.findById(param.getRefundId())
                    .orElseThrow(() -> new PayFailureException("未查询到退款订单"));
        } else {
            refundOrder = refundOrderManager.findByRefundNo(param.getRefundNo())
                    .orElseThrow(() -> new PayFailureException("未查询到退款订单"));
        }
        // 如果不是异步支付, 直接返回返回
        if (!refundOrder.isAsyncPay()){
            throw new PayFailureException("订单没有异步通道的退款，不需要同步");
        }
        // 如果订单已经关闭, 直接返回失败
        if (Objects.equals(refundOrder.getStatus(), RefundStatusEnum.CLOSE.getCode())){
            throw new PayFailureException("订单已经关闭，不需要同步");
        }
        return this.syncRefundOrder(refundOrder);
    }

    /**
     * 退款订单信息同步
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public SyncResult syncRefundOrder(RefundOrder refundOrder) {
        // 加锁
        LockInfo lock = lockTemplate.lock("sync:refund:" + refundOrder.getId(),10000,200);
        if (Objects.isNull(lock)) {
            throw new RepetitiveOperationException("退款同步处理中，请勿重复操作");
        }
        try {
            // 获取支付同步策略类
            AbsRefundSyncStrategy syncPayStrategy = RefundSyncStrategyFactory.create(refundOrder.getAsyncChannel());
            syncPayStrategy.initRefundParam(refundOrder);
            // 同步前处理, 主要预防请求过于迅速
            syncPayStrategy.doBeforeHandler();
            // 执行操作, 获取支付网关同步的结果
            RefundGatewaySyncResult syncResult = syncPayStrategy.doSyncStatus();

            // 判断是否同步成功
            if (Objects.equals(syncResult.getSyncStatus(), RefundSyncStatusEnum.FAIL)) {
                // 同步失败, 返回失败响应, 同时记录失败的日志
                this.saveRecord(refundOrder, syncResult, false, null, syncResult.getErrorMsg());
                throw new PayFailureException(syncResult.getErrorMsg());
            }
            // 支付订单的网关订单号是否一致, 不一致进行更新
            if (Objects.nonNull(syncResult.getGatewayOrderNo()) && !Objects.equals(syncResult.getGatewayOrderNo(), refundOrder.getGatewayOrderNo())){
                refundOrder.setGatewayOrderNo(syncResult.getGatewayOrderNo());
                refundOrderManager.updateById(refundOrder);
            }
            // 判断网关状态是否和支付单一致
            boolean statusSync = this.checkSyncStatus(syncResult, refundOrder);
            RefundRepairResult repairResult = new RefundRepairResult();
            try {
                // 状态不一致，执行退款单修复逻辑
                if (!statusSync) {
                    // 如果没有支付来源, 设置支付来源为同步
                    RepairLocal repairInfo = PaymentContextLocal.get().getRepairInfo();
                    if (Objects.isNull(repairInfo.getSource())){
                        repairInfo.setSource(PayRepairSourceEnum.SYNC);
                    }
                    repairInfo.setFinishTime(syncResult.getRefundTime());
                    repairResult = this.repairHandler(syncResult, refundOrder);
                }
            } catch (PayFailureException e) {
                // 同步失败, 返回失败响应, 同时记录失败的日志
                syncResult.setSyncStatus(RefundSyncStatusEnum.FAIL);
                this.saveRecord(refundOrder, syncResult, false, null, e.getMessage());
                throw e;
            }
            // 同步成功记录日志
            this.saveRecord(refundOrder, syncResult, !statusSync, repairResult.getRepairNo(), null);
            return new SyncResult()
                    .setGatewayStatus(syncResult.getSyncStatus().getCode())
                    .setRepair(!statusSync)
                    .setRepairOrderNo(repairResult.getRepairNo());
        } finally {
            lockTemplate.releaseLock(lock);
        }
    }


    /**
     * 检查状态是否一致
     * @see RefundSyncStatusEnum 同步返回类型
     * @see RefundStatusEnum 退款单状态
     */
    private boolean checkSyncStatus(RefundGatewaySyncResult syncResult, RefundOrder order){
        RefundSyncStatusEnum syncStatus = syncResult.getSyncStatus();
        String orderStatus = order.getStatus();
        // 退款完成
        if (Objects.equals(syncStatus, RefundSyncStatusEnum.SUCCESS)&&
                Objects.equals(orderStatus, RefundStatusEnum.SUCCESS.getCode())) {
            return true;
        }

        // 退款失败
        if (Objects.equals(syncStatus, RefundSyncStatusEnum.FAIL)&&
                Objects.equals(orderStatus, RefundStatusEnum.FAIL.getCode())) {
            return true;
        }
        // 退款中
        if (Objects.equals(syncStatus, RefundSyncStatusEnum.PROGRESS)&&
                Objects.equals(orderStatus, RefundStatusEnum.PROGRESS.getCode())) {
            return true;
        }
        return false;
    }

    /**
     * 进行退款订单和支付订单的补偿
     */
    private RefundRepairResult repairHandler(RefundGatewaySyncResult syncResult, RefundOrder order){
        RefundSyncStatusEnum syncStatusEnum = syncResult.getSyncStatus();
        RefundRepairResult repair = new RefundRepairResult();
        // 对支付网关同步的结果进行处理
        switch (syncStatusEnum) {
            case SUCCESS:
                repair = repairService.repair(order, RefundRepairWayEnum.REFUND_SUCCESS);
                break;
            case PROGRESS:
                // 不进行处理
                break;
            case FAIL: {
                repair = repairService.repair(order, RefundRepairWayEnum.REFUND_FAIL);
                break;
            }
            case NOT_FOUND:
                repair = repairService.repair(order, RefundRepairWayEnum.REFUND_FAIL);
                break;
            default: {
                throw new BizException("代码有问题");
            }
        }
        return repair;
    }

    /**
     * 保存同步记录
     * @param refundOrder 支付单
     * @param syncResult 同步结果
     * @param repair 是否修复
     * @param repairOrderNo 修复号
     * @param errorMsg 错误信息
     */
    private void saveRecord(RefundOrder refundOrder, RefundGatewaySyncResult syncResult, boolean repair, String repairOrderNo, String errorMsg){
        PaySyncRecord paySyncRecord = new PaySyncRecord()
                .setOrderId(refundOrder.getId())
                .setOrderNo(refundOrder.getRefundNo())
                .setSyncType(PaymentTypeEnum.REFUND.getCode())
                .setAsyncChannel(refundOrder.getAsyncChannel())
                .setGatewayOrderNo(syncResult.getGatewayOrderNo())
                .setSyncInfo(syncResult.getSyncInfo())
                .setGatewayStatus(syncResult.getSyncStatus().getCode())
                .setRepairOrder(repair)
                .setRepairOrderNo(repairOrderNo)
                .setErrorMsg(errorMsg)
                .setClientIp(PaymentContextLocal.get().getRequestInfo().getClientIp())
                .setReqId(PaymentContextLocal.get().getRequestInfo().getReqId());
        paySyncRecordService.saveRecord(paySyncRecord);
    }
}

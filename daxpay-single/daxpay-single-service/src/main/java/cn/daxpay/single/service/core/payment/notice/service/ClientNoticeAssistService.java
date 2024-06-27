package cn.daxpay.single.service.core.payment.notice.service;

import cn.bootx.platform.common.jackson.util.JacksonUtil;
import cn.daxpay.single.service.code.ClientNoticeTypeEnum;
import cn.daxpay.single.service.core.notice.entity.ClientNoticeTask;
import cn.daxpay.single.service.core.order.allocation.convert.AllocOrderConvert;
import cn.daxpay.single.service.core.order.allocation.entity.AllocOrder;
import cn.daxpay.single.service.core.order.allocation.entity.AllocOrderDetail;
import cn.daxpay.single.service.core.order.pay.convert.PayOrderConvert;
import cn.daxpay.single.service.core.order.pay.entity.PayOrder;
import cn.daxpay.single.service.core.order.refund.convert.RefundOrderConvert;
import cn.daxpay.single.service.core.order.refund.entity.RefundOrder;
import cn.daxpay.single.service.core.payment.common.service.PaymentAssistService;
import cn.daxpay.single.service.core.payment.notice.result.AllocDetailNoticeResult;
import cn.daxpay.single.service.core.payment.notice.result.AllocNoticeResult;
import cn.daxpay.single.service.core.payment.notice.result.PayNoticeResult;
import cn.daxpay.single.service.core.payment.notice.result.RefundNoticeResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 客户系统消息通知任务支撑服务
 * @author xxm
 * @since 2024/2/25
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ClientNoticeAssistService {

    private final PaymentAssistService paymentAssistService;

    /**
     * 构建出支付通知任务对象
     */
    public ClientNoticeTask buildPayTask(PayOrder order){
        PayNoticeResult payNoticeResult = PayOrderConvert.CONVERT.convertNotice(order);
        payNoticeResult.setAttach(order.getAttach());
        paymentAssistService.sign(payNoticeResult);
        return new ClientNoticeTask()
                .setUrl(order.getNotifyUrl())
                // 时间序列化进行了重写, 所以使用Jackson的序列化工具类
                .setContent(JacksonUtil.toJson(payNoticeResult))
                .setNoticeType(ClientNoticeTypeEnum.PAY.getType())
                .setSendCount(0)
                .setTradeId(order.getId())
                .setTradeNo(order.getOrderNo())
                .setTradeStatus(order.getStatus());
    }

    /**
     * 构建出退款通知任务对象
     */
    public ClientNoticeTask buildRefundTask(RefundOrder order){
        // 创建退款通知内容
        RefundNoticeResult refundNoticeResult = RefundOrderConvert.CONVERT.convertNotice(order);
        refundNoticeResult.setAttach(order.getAttach());
        // 签名
        paymentAssistService.sign(refundNoticeResult);
        return new ClientNoticeTask()
                .setUrl(order.getNotifyUrl())
                // 时间序列化进行了重写
                .setContent(JacksonUtil.toJson(refundNoticeResult))
                .setNoticeType(ClientNoticeTypeEnum.REFUND.getType())
                .setSendCount(0)
                .setTradeId(order.getId())
                .setTradeNo(order.getRefundNo())
                .setTradeStatus(order.getStatus());
    }

    /**
     * 构建分账通知
     */
    public ClientNoticeTask buildAllocTask(AllocOrder order, List<AllocOrderDetail> list){
        // 分账
        AllocNoticeResult allocOrderResult = AllocOrderConvert.CONVERT.toNotice(order);
        // 分账详情
        List<AllocDetailNoticeResult> details = list.stream()
                .map(AllocOrderConvert.CONVERT::toNotice)
                .collect(Collectors.toList());
        // 分账扩展和明细
        allocOrderResult.setAttach(order.getAttach())
                .setDetails(details);
        // 签名
        paymentAssistService.sign(allocOrderResult);
        return new ClientNoticeTask()
                .setUrl(order.getNotifyUrl())
                // 时间序列化进行了重写
                .setContent(JacksonUtil.toJson(allocOrderResult))
                .setNoticeType(ClientNoticeTypeEnum.ALLOCATION.getType())
                .setSendCount(0)
                .setTradeId(order.getId())
                .setTradeNo(order.getAllocNo())
                .setTradeStatus(order.getStatus());
    }

}

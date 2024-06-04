package cn.daxpay.single.gateway.controller;

import cn.bootx.platform.common.core.annotation.IgnoreAuth;
import cn.daxpay.single.code.PaymentApiCode;
import cn.daxpay.single.exception.pay.PayFailureException;
import cn.daxpay.single.param.payment.allocation.QueryAllocOrderParam;
import cn.daxpay.single.param.payment.allocation.QueryAllocReceiverParam;
import cn.daxpay.single.param.payment.pay.QueryPayParam;
import cn.daxpay.single.param.payment.refund.QueryRefundParam;
import cn.daxpay.single.param.payment.transfer.QueryTransferParam;
import cn.daxpay.single.result.DaxResult;
import cn.daxpay.single.result.order.AllocOrderResult;
import cn.daxpay.single.result.allocation.AllocReceiversResult;
import cn.daxpay.single.result.order.PayOrderResult;
import cn.daxpay.single.result.order.RefundOrderResult;
import cn.daxpay.single.service.annotation.PaymentSign;
import cn.daxpay.single.service.annotation.InitPaymentContext;
import cn.daxpay.single.service.core.order.pay.service.PayOrderQueryService;
import cn.daxpay.single.service.core.order.refund.service.RefundOrderQueryService;
import cn.daxpay.single.service.core.payment.allocation.service.AllocationReceiverService;
import cn.daxpay.single.service.core.payment.allocation.service.AllocationService;
import cn.daxpay.single.util.DaxRes;
import cn.hutool.core.util.StrUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 统一查询接口
 * @author xxm
 * @since 2024/2/7
 */
@IgnoreAuth
@Tag(name = "统一查询接口")
@RestController
@RequestMapping("/unipay/query")
@RequiredArgsConstructor
public class UniQueryController {

    private final PayOrderQueryService payOrderQueryService;
    private final RefundOrderQueryService refundOrderQueryService;
    private final AllocationReceiverService allocationReceiverService;
    private final AllocationService allocationService;

    @PaymentSign
    @InitPaymentContext(PaymentApiCode.QUERY_PAY_ORDER)
    @Operation(summary = "支付订单查询接口")
    @PostMapping("/payOrder")
    public DaxResult<PayOrderResult> queryPayOrder(@RequestBody QueryPayParam param){
        return DaxRes.ok(payOrderQueryService.queryPayOrder(param));
    }

    @PaymentSign
    @InitPaymentContext(PaymentApiCode.QUERY_REFUND_ORDER)
    @Operation(summary = "退款订单查询接口")
    @PostMapping("/refundOrder")
     public DaxResult<RefundOrderResult> queryRefundOrder(@RequestBody QueryRefundParam param){
        return DaxRes.ok(refundOrderQueryService.queryRefundOrder(param));
    }

    @PaymentSign
    @InitPaymentContext(PaymentApiCode.QUERY_ALLOCATION_ORDER)
    @Operation(summary = "分账订单查询接口")
    @PostMapping("/allocationOrder")
    public DaxResult<AllocOrderResult> queryAllocationOrder(@RequestBody QueryAllocOrderParam param){
        return DaxRes.ok(allocationService.queryAllocationOrder(param));
    }

    @PaymentSign
    @InitPaymentContext(PaymentApiCode.QUERY_TRANSFER_ORDER)
    @Operation(summary = "转账订单查询接口")
    @PostMapping("/transferOrder")
    public DaxResult<Void> transferOrder(@RequestBody QueryTransferParam param){
        return DaxRes.ok();
    }

    @PaymentSign
    @InitPaymentContext(PaymentApiCode.QUERY_ALLOCATION_RECEIVER)
    @Operation(summary = "分账接收方查询接口")
    @PostMapping("/allocationReceiver")
    public DaxResult<AllocReceiversResult> queryAllocReceive(@RequestBody QueryAllocReceiverParam param){
        if (StrUtil.isAllBlank(param.getChannel(), param.getReceiverNo())){
            throw new PayFailureException("所属通道和接收方编号不可同时为空");
        }
        return DaxRes.ok(allocationReceiverService.queryAllocReceive(param));
    }

}

package cn.daxpay.single.service.core.payment.refund.strategy;

import cn.daxpay.single.core.code.PayChannelEnum;
import cn.daxpay.single.service.core.channel.alipay.entity.AliPayConfig;
import cn.daxpay.single.service.core.channel.alipay.service.AliPayConfigService;
import cn.daxpay.single.service.core.channel.alipay.service.AliPayRefundService;
import cn.daxpay.single.service.func.AbsRefundStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import static org.springframework.beans.factory.config.BeanDefinition.SCOPE_PROTOTYPE;

/**
 * 支付宝退款
 * @author xxm
 * @since 2023/7/4
 */
@Scope(SCOPE_PROTOTYPE)
@Component
@RequiredArgsConstructor
public class AliRefundStrategy extends AbsRefundStrategy {

    private final AliPayConfigService alipayConfigService;

    private final AliPayRefundService aliRefundService;

    private AliPayConfig config;

    /**
     * 策略标识
     * @see PayChannelEnum
     */
     @Override
    public String getChannel() {
        return PayChannelEnum.ALI.getCode();
    }


    /**
     * 退款前前操作
     */
    @Override
    public void doBeforeRefundHandler() {
        this.config = alipayConfigService.getAndCheckConfig();
    }

    /**
     * 退款
     */
    @Override
    public void doRefundHandler() {
        aliRefundService.refund(this.getRefundOrder(),this.config);
    }
}

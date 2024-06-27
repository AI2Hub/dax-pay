package cn.daxpay.single.core.result.pay;

import cn.daxpay.single.core.result.PaymentCommonResult;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 支付关闭响应参数
 * @author xxm
 * @since 2024/4/23
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
@Schema(title = "支付关闭响应参数")
public class PayCloseResult extends PaymentCommonResult {
}

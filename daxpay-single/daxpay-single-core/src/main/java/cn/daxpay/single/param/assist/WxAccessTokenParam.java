package cn.daxpay.single.param.assist;

import cn.daxpay.single.param.PaymentCommonParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 获取微信AccessToken参数
 * @author xxm
 * @since 2024/2/10
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
@Schema(title = "获取微信AccessToken参数")
public class WxAccessTokenParam extends PaymentCommonParam {

    @Schema(description = "微信code")
    private String code;
}

package cn.daxpay.single.service.dto.order.allocation;

import cn.bootx.platform.common.core.rest.dto.BaseDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
* 分账订单扩展
*/
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
@Schema(title = "分账订单扩展")
public class AllocationOrderExtraDto extends BaseDto {

    /** 异步通知地址 */
    @Schema(description = "异步通知地址")
    private String notifyUrl;

    /** 商户扩展参数,回调时会原样返回, 以最后一次为准 */
    @Schema(description = "商户扩展参数")
    private String attach;

    /** 请求时间，时间戳转时间 */
    @Schema(description = "请求时间，传输时间戳")
    private LocalDateTime reqTime;

    /** 终端ip */
    @Schema(description = "支付终端ip")
    private String clientIp;
}

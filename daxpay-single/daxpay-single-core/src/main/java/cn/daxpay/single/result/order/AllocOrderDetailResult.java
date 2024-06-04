package cn.daxpay.single.result.order;

import cn.daxpay.single.code.AllocDetailResultEnum;
import cn.daxpay.single.code.AllocReceiverTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * 分账订单明细
 * @author xxm
 * @since 2024/5/21
 */
@Data
@Accessors(chain = true)
@Schema(title = "分账订单明细")
public class AllocOrderDetailResult {

    @Schema(description = "分账接收方编号")
    private String receiverNo;

    /** 分账金额 */
    @Schema(description = "分账金额")
    private Integer amount;

    /** 分账比例 */
    @Schema(description = "分账比例(万分之多少)")
    private Integer rate;

    /**
     * 分账接收方类型
     * @see AllocReceiverTypeEnum
     */
    @Schema(description = "分账接收方类型")
    private String receiverType;

    /** 接收方账号 */
    @Schema(description = "接收方账号")
    private String receiverAccount;

    /** 接收方姓名 */
    @Schema(description = "接收方姓名")
    private String receiverName;

    /**
     * 分账结果
     * @see AllocDetailResultEnum
     */
    @Schema(description = "分账结果")
    private String result;

    /** 错误代码 */
    @Schema(description = "错误代码")
    private String errorCode;

    /** 错误原因 */
    @Schema(description = "错误原因")
    private String errorMsg;

    /** 分账完成时间 */
    @Schema(description = "分账完成时间")
    private LocalDateTime finishTime;
}

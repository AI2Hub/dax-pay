package cn.daxpay.single.service.core.order.reconcile.entity;

import cn.bootx.platform.common.core.function.EntityBaseFunction;
import cn.bootx.platform.common.mybatisplus.base.MpCreateEntity;
import cn.daxpay.single.service.code.PaymentTypeEnum;
import cn.daxpay.single.service.core.order.reconcile.conver.ReconcileConvert;
import cn.daxpay.single.service.dto.order.reconcile.ReconcileOutTradeDto;
import cn.bootx.table.modify.annotation.DbColumn;
import cn.bootx.table.modify.annotation.DbTable;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * 对账通道交易明细, 从三方系统下载的交易记录
 * @author xxm
 * @since 2024/1/18
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
@DbTable(comment = "对账-通道交易明细")
@TableName("pay_reconcile_out_trade")
public class ReconcileOutTrade extends MpCreateEntity implements EntityBaseFunction<ReconcileOutTradeDto> {

    /** 关联对账订单ID */
    @DbColumn(comment = "关联对账订单ID")
    private Long reconcileId;

    /** 商品名称 */
    @DbColumn(comment = "商品名称")
    private String title;

    /** 交易金额 */
    @DbColumn(comment = "交易金额")
    private Integer amount;

    /**
     * 交易类型
     * @see PaymentTypeEnum
     */
    @DbColumn(comment = "交易类型")
    private String type;

    /** 本地交易号 */
    @DbColumn(comment = "本地交易号")
    private String tradeNo;

    /** 通道交易号 - 支付宝/微信的订单号 */
    @DbColumn(comment = "通道交易号")
    private String outTradeNo;

    /** 交易时间 */
    @DbColumn(comment = "交易时间")
    private LocalDateTime tradeTime;


    @Override
    public ReconcileOutTradeDto toDto() {
        return ReconcileConvert.CONVERT.convert(this);
    }
}

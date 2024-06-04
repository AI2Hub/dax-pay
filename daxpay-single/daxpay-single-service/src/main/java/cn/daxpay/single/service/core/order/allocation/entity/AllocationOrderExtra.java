package cn.daxpay.single.service.core.order.allocation.entity;

import cn.bootx.platform.common.core.function.EntityBaseFunction;
import cn.bootx.platform.common.mybatisplus.base.MpBaseEntity;
import cn.bootx.table.modify.annotation.DbColumn;
import cn.bootx.table.modify.annotation.DbTable;
import cn.daxpay.single.service.core.order.allocation.convert.AllocationConvert;
import cn.daxpay.single.service.dto.order.allocation.AllocationOrderExtraDto;
import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * 分账订单扩展
 * @author xxm
 * @since 2024/5/22
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
@DbTable(comment = "分账订单扩展")
@TableName("pay_allocation_order_extra")
public class AllocationOrderExtra extends MpBaseEntity implements EntityBaseFunction<AllocationOrderExtraDto> {

    /** 异步通知地址 */
    @DbColumn(comment = "异步通知地址")
    @TableField(updateStrategy = FieldStrategy.ALWAYS)
    private String notifyUrl;

    /** 商户扩展参数,回调时会原样返回, 以最后一次为准 */
    @DbColumn(comment = "商户扩展参数")
    private String attach;

    /** 请求时间，时间戳转时间 */
    @DbColumn(comment = "请求时间，传输时间戳")
    private LocalDateTime reqTime;

    /** 终端ip */
    @DbColumn(comment = "支付终端ip")
    private String clientIp;

    @Override
    public AllocationOrderExtraDto toDto() {
        return AllocationConvert.CONVERT.convert(this);
    }
}

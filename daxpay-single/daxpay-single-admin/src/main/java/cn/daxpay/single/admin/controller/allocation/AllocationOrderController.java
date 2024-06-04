package cn.daxpay.single.admin.controller.allocation;

import cn.bootx.platform.common.core.rest.PageResult;
import cn.bootx.platform.common.core.rest.Res;
import cn.bootx.platform.common.core.rest.ResResult;
import cn.bootx.platform.common.core.rest.dto.LabelValue;
import cn.bootx.platform.common.core.rest.param.PageParam;
import cn.daxpay.single.code.PaymentApiCode;
import cn.daxpay.single.param.payment.allocation.AllocFinishParam;
import cn.daxpay.single.param.payment.allocation.AllocSyncParam;
import cn.daxpay.single.param.payment.allocation.AllocationParam;
import cn.daxpay.single.service.annotation.InitPaymentContext;
import cn.daxpay.single.service.core.order.allocation.service.AllocationOrderQueryService;
import cn.daxpay.single.service.core.payment.allocation.service.AllocationService;
import cn.daxpay.single.service.core.payment.allocation.service.AllocationSyncService;
import cn.daxpay.single.service.dto.order.allocation.AllocationOrderAndExtraDto;
import cn.daxpay.single.service.dto.order.allocation.AllocationOrderDetailDto;
import cn.daxpay.single.service.dto.order.allocation.AllocationOrderDto;
import cn.daxpay.single.service.dto.order.allocation.AllocationOrderExtraDto;
import cn.daxpay.single.service.param.order.AllocationOrderQuery;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 分账订单控制器
 * @author xxm
 * @since 2024/4/7
 */
@Tag(name = "分账订单控制器")
@RestController
@RequestMapping("/order/allocation")
@RequiredArgsConstructor
public class AllocationOrderController {

    private final AllocationOrderQueryService queryService;

    private final AllocationService allocationService;

    private final AllocationSyncService allocationSyncService;

    @Operation(summary = "分页")
    @GetMapping("/page")
    public ResResult<PageResult<AllocationOrderDto>> page(PageParam pageParam, AllocationOrderQuery param){
        return Res.ok(queryService.page(pageParam,param));
    }

    @Operation(summary = "分账明细列表")
    @GetMapping("/detail/findAll")
    public ResResult<List<AllocationOrderDetailDto>> findDetailsByOrderId(Long orderId){
        return Res.ok(queryService.findDetailsByOrderId(orderId));
    }

    @Operation(summary = "查询详情")
    @GetMapping("/findById")
    public ResResult<AllocationOrderDto> findById(Long id){
        return Res.ok(queryService.findById(id));
    }

    @Operation(summary = "查询明细详情")
    @GetMapping("/detail/findById")
    public ResResult<AllocationOrderDetailDto> findDetailById(Long id){
        return Res.ok(queryService.findDetailById(id));
    }

    @Operation(summary = "查询扩展信息")
    @GetMapping("/findByAllocNo")
    public ResResult<AllocationOrderAndExtraDto> findByAllocNo(String allocNo){
        AllocationOrderAndExtraDto result = new AllocationOrderAndExtraDto();
        AllocationOrderDto order = queryService.findByAllocNo(allocNo);
        AllocationOrderExtraDto extra = queryService.findExtraById(order.getId());
        result.setOrder(order).setExtra(extra);
        return Res.ok(result);
    }

    @Operation(summary = "获取可以分账的通道")
    @GetMapping("/findChannels")
    public ResResult<List<LabelValue>> findChannels(){
        return Res.ok(queryService.findChannels());
    }

    @InitPaymentContext(PaymentApiCode.SYNC_ALLOCATION)
    @Operation(summary = "同步分账结果")
    @PostMapping("/sync")
    public ResResult<Void> sync(String allocationNo){
        AllocSyncParam param = new AllocSyncParam();
        param.setAllocationNo(allocationNo);
        allocationSyncService.sync(param);
        return Res.ok();
    }

    @InitPaymentContext(PaymentApiCode.ALLOCATION_FINISH)
    @Operation(summary = "分账完结")
    @PostMapping("/finish")
    public ResResult<Void> finish(String allocationNo){
        AllocFinishParam param = new AllocFinishParam();
        param.setAllocationNo(allocationNo);
        allocationService.finish(param);
        return Res.ok();
    }

    @InitPaymentContext(PaymentApiCode.ALLOCATION)
    @Operation(summary = "重新发起分账")
    @PostMapping("/retry")
    public ResResult<Void> retryAllocation(String bizAllocationNo){
        AllocationParam param = new AllocationParam();
        param.setBizAllocationNo(bizAllocationNo);
        allocationService.allocation(param);
        return Res.ok();
    }
}

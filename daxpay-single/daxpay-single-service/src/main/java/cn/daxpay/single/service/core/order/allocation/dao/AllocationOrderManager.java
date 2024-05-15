package cn.daxpay.single.service.core.order.allocation.dao;

import cn.bootx.platform.common.core.rest.param.PageParam;
import cn.bootx.platform.common.mybatisplus.impl.BaseManager;
import cn.bootx.platform.common.mybatisplus.util.MpUtil;
import cn.bootx.platform.common.query.generator.QueryGenerator;
import cn.daxpay.single.code.AllocOrderStatusEnum;
import cn.daxpay.single.service.core.order.allocation.entity.AllocationOrder;
import cn.daxpay.single.service.param.order.AllocationOrderQuery;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 *
 * @author xxm
 * @since 2024/4/7
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class AllocationOrderManager extends BaseManager<AllocationOrderMapper, AllocationOrder> {

    /**
     * 根据分账单号查询
     */
    public Optional<AllocationOrder> findByAllocationNo(String allocationNo){
        return findByField(AllocationOrder::getAllocationNo, allocationNo);
    }

    /**
     * 根据商户分账号查询
     */
    public Optional<AllocationOrder> findByBizAllocationNo(String bizAllocationNo){
        return findByField(AllocationOrder::getBizAllocationNo, bizAllocationNo);
    }

    /**
     * 分页
     */
    public Page<AllocationOrder> page(PageParam pageParam, AllocationOrderQuery param){
            Page<AllocationOrder> mpPage = MpUtil.getMpPage(pageParam, AllocationOrder.class);
            QueryWrapper<AllocationOrder> generator = QueryGenerator.generator(param);
            return this.page(mpPage, generator);
    }

    /**
     * 查询待同步的分账单
     */
    public List<AllocationOrder> findSyncOrder(){
        List<String> statusList = Arrays.asList(AllocOrderStatusEnum.ALLOCATION_PROCESSING.getCode(), AllocOrderStatusEnum.ALLOCATION_END.getCode());
        return lambdaQuery()
                .in(AllocationOrder::getStatus, statusList)
                .list();
    }
}

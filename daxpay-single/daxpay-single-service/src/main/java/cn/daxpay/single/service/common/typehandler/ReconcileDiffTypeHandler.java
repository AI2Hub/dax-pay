package cn.daxpay.single.service.common.typehandler;

import cn.bootx.platform.common.mybatisplus.handler.JacksonTypeReferenceHandler;
import cn.daxpay.single.service.core.payment.reconcile.domain.ReconcileDiffDetail;
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.List;

/**
 * 对账差异内容类型处理器
 * @author xxm
 * @since 2024/3/4
 */
public class ReconcileDiffTypeHandler extends JacksonTypeReferenceHandler<List<ReconcileDiffDetail>> {
    /**
     * 返回要反序列化的类型对象
     */
    @Override
    public TypeReference<List<ReconcileDiffDetail>> getTypeReference() {
        return new TypeReference<List<ReconcileDiffDetail>>() {};
    }
}

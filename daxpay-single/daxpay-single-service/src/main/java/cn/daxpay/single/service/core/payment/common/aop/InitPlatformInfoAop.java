package cn.daxpay.single.service.core.payment.common.aop;

import cn.bootx.platform.common.core.exception.DataNotExistException;
import cn.daxpay.single.exception.pay.PayFailureException;
import cn.daxpay.single.service.annotation.InitPaymentContext;
import cn.daxpay.single.service.core.system.config.dao.PayApiConfigManager;
import cn.daxpay.single.service.core.system.config.entity.PayApiConfig;
import cn.daxpay.single.service.core.system.config.service.PayApiConfigService;
import cn.daxpay.single.service.core.system.config.service.PlatformConfigService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 *
 * 执行顺序: 过滤器 -> 拦截器 -> 切面 -> 方法
 * @author xxm
 * @since 2023/12/24
 */
@Aspect
@Slf4j
@Component
@RequiredArgsConstructor
public class InitPlatformInfoAop {

    private final PayApiConfigService payApiConfigService;

    private final PayApiConfigManager payApiConfigManager;

    private final PlatformConfigService platformConfigService;

    /**
     * 拦截注解
     */
    @Around("@annotation(platformContext)")
    public Object beforeMethod(ProceedingJoinPoint pjp, InitPaymentContext platformContext) throws Throwable {
        String code = platformContext.value();
        // 接口信息
        PayApiConfig api = payApiConfigManager.findByCode(code)
                .orElseThrow(() -> new DataNotExistException("未找到接口信息"));
        if (!api.isEnable()){
            throw new PayFailureException("该接口权限未开放");
        }
        // 设置接口信息
        payApiConfigService.initApiInfo(api);
        // 初始化平台信息
        platformConfigService.initPlatform();
        return pjp.proceed();
    }
}

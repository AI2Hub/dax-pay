package cn.daxpay.single.gateway.controller;

import cn.bootx.platform.common.core.annotation.IgnoreAuth;
import cn.bootx.platform.common.core.rest.Res;
import cn.bootx.platform.common.core.rest.ResResult;
import cn.daxpay.single.core.code.PaymentApiCode;
import cn.daxpay.single.core.param.assist.WxAccessTokenParam;
import cn.daxpay.single.core.param.assist.WxAuthUrlParam;
import cn.daxpay.single.core.result.DaxResult;
import cn.daxpay.single.core.result.assist.WxAccessTokenResult;
import cn.daxpay.single.core.result.assist.WxAuthUrlResult;
import cn.daxpay.single.service.annotation.InitPaymentContext;
import cn.daxpay.single.service.annotation.PaymentVerify;
import cn.daxpay.single.service.core.extra.WeChatAuthService;
import cn.daxpay.single.core.util.DaxRes;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 支付支撑接口
 * @author xxm
 * @since 2024/2/10
 */
@IgnoreAuth
@Tag(name = "支付支撑接口")
@RestController
@RequestMapping("/unipay/assist")
@RequiredArgsConstructor
public class UniPayAssistController {
    private final WeChatAuthService wechatAuthService;

    @PaymentVerify
    @InitPaymentContext(PaymentApiCode.GET_WX_AUTH_URL)
    @Operation(summary = "获取微信OAuth2授权链接")
    @PostMapping("/getWxAuthUrl")
    public DaxResult<WxAuthUrlResult> getWxAuthUrl(@RequestBody WxAuthUrlParam param){
        return DaxRes.ok(wechatAuthService.getWxAuthUrl(param));
    }

    @PaymentVerify
    @InitPaymentContext(PaymentApiCode.GET_WX_ACCESS_TOKEN)
    @Operation(summary = "获取微信AccessToken")
    @PostMapping("/getWxAccessToken")
    public ResResult<WxAccessTokenResult> getWxAccessToken(@RequestBody WxAccessTokenParam param){
        return Res.ok(wechatAuthService.getWxAccessToken(param));
    }

}

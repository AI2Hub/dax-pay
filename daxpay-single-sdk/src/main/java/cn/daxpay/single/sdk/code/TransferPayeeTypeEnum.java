package cn.daxpay.single.sdk.code;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 转账接收方类型
 * @author xxm
 * @since 2024/4/1
 */
@Getter
@AllArgsConstructor
public enum TransferPayeeTypeEnum {
    /** 微信 个人 */
    WX_PERSONAL("wx_personal", "OpenId"),
    /** 支付宝 userId 以2088开头的纯16位数字 */
    ALI_USER_ID("ali_user_id", "用户ID"),
    /** 支付宝 openId  */
    ALI_OPEN_ID("ali_open_id", "OpenId"),
    /** 支付宝 账号 支持邮箱和手机号格式 */
    ALI_LOGIN_NAME("ali_login_name", "账号");

    /** 编码 */
    private final String code;
    /** 名称 */
    private final String name;

    /**
     * 根据编码查找
     */
    public static TransferPayeeTypeEnum findByCode(String code) {
        return Arrays.stream(TransferPayeeTypeEnum.values())
                .filter(e -> e.getCode().equals(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("未找到对应的分账接收方类型"));
    }

    /** 微信支持类型 */
    public static final List<TransferPayeeTypeEnum> WECHAT_LIST = Collections.singletonList(WX_PERSONAL);
    /** 支付宝支持类型 */
    public static final List<TransferPayeeTypeEnum> ALI_LIST = Collections.unmodifiableList(Arrays.asList(ALI_OPEN_ID, ALI_USER_ID, ALI_LOGIN_NAME));

}

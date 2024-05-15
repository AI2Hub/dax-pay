package cn.daxpay.single.exception.waller;

import cn.bootx.platform.common.core.exception.BizException;
import cn.daxpay.single.code.DaxPayErrorCode;

/**
 * 钱包不存在
 *
 * @author xxm
 * @since 2020/12/8
 */
public class WalletNotExistsException extends BizException {

    public WalletNotExistsException() {
        super(DaxPayErrorCode.WALLET_NOT_EXISTS, "钱包不存在");
    }

}

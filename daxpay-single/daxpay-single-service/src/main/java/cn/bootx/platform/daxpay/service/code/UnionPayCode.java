package cn.bootx.platform.daxpay.service.code;

import com.egzosn.pay.union.bean.SDKConstants;

/**
 * 云闪付常量
 * @author xxm
 * @since 2024/3/7
 */
public interface UnionPayCode {


    /** 应答码 00表示成功 */
    String RESP_CODE = SDKConstants.param_respCode;

    /** 原交易应答码 00表示成功 */
    String RESP_ORIG_CODE = SDKConstants.param_origRespCode;

    /** 应答码信息 */
    String RESP_MSG = SDKConstants.param_respMsg;

    /** 业务结果 00表示成功 */
    String RESP_SUCCESS = SDKConstants.OK_RESP_CODE;

    /** 交易类型 */
    String TXN_TYPE = "txnType";

    /** 网关订单号 */
    String QUERY_ID = "queryId";

    /** 第三方订单号(本地订单号) */
    String ORDER_ID = "orderId";

    /** APP支付 银联订单号 */
    String PAY_APP_TN = SDKConstants.param_tn;

    /** 交易类型 支付 */
    String TXN_TYPE_PAY = "01";

    /** 交易类型 退款 */
    String TXN_TYPE_REFUND = "04";

    /**
     * 订单发送时间
     * 格式: yyyyMMddHHmmss
     */
    String TXN_TIME = "txnTime";


    /** 退款金额 */
    String TXN_AMT = "txnAmt";

    /** 总金额 */
    String TOTAL_FEE = "settleAmt";



    /** 对账单下载类型编码 */
    String RECONCILE_BILL_TYPE = "00";

}

package com.miniw.external.client.minicoin.params;


/**
 * 迷你玩账号服返回code枚举
 *
 * @author luoquan
 * @date 2021/08/05
 */
public enum CoinPayResultCode {

    /* 账号服充值相关 */
    COIN_PAY_SUCCESS         ("SUCCESS" , "充值成功"),
    COIN_PAY_FAIL           ("FAIL","充值失败TIMEOUT"),
    COIN_DUP_BILL      ("DUP_BILL","重复的订单"),
    COIN_UNKNOWN_ERROR      ("ERROR","未知错误"),

    ;

    private final String code;
    private final String msg;

    CoinPayResultCode(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public String getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}

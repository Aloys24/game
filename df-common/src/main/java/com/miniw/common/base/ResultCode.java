package com.miniw.common.base;

/**
 * 消息状态枚举
 */
public enum ResultCode {

    /*系统相关*/
    SYS_SUCCESS         (0 , "操作成功"),
    SYS_ERROR           (400,"操作失败"),
    SYS_NEED_LOGIN      (401,"请登录"),
    SYS_NOT_OPEN        (402,"活动未开启"),
    SYS_IS_END          (403,"活动已结束"),
    SYS_LIMIT           (404,"超过次数限制"),
    SYS_UNKNOWN         (405,"未知错误"),
    SYS_PARAM_ERROR     (406,"请求参数异常"),
    SYS_ILLEGAL_PARAM   (407,"API请求失败"),
    SYS_MISMATCH        (408,"数据不匹配"),
    SYS_NON_EXIST       (409,"数据不存在"),
    SYS_DISCONTENT      (410,"未达到领取条件"),
    SYS_BAN             (411,"禁止操作"),
    SYS_BUSY            (444,"系统繁忙"),
    SYS_ACCOUNT_ERROR   (445,"用户名密码不匹配"),


    /* 免流相关 */
    DF_BIND_LIMITED (446, "当月已达到绑定次数限制"),
    DF_LIMITED_MONTH(447, "每月仅能限购一次"),

    /* 运营商回调相关 */
    DF_CALLBACK_SUCCESS(200, "订购成功"),
    DF_CALLBACK_FAILED(500, "订购失败"),
    DF_CALLBACK_BAD_BEQUEST(501, "非法请求"),
    DF_CALLBACK_UNKNOW_SIGN(502, "未携带正确签名"),


    /* 充值服相关 */
    MINI_COIN_PAY_FAILED(503, "迷你币充值失败")

    ;

    private final Integer code;
    private final String  msg;

    ResultCode(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public static ResultCode valueOf(Integer code) {
        if (code == null || code < 0)
            return SYS_UNKNOWN;
        for (ResultCode resultCode : values()) {
            if (resultCode.code.equals(code))
                return resultCode;
        }
        return SYS_UNKNOWN;
    }
}

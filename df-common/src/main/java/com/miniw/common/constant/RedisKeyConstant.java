package com.miniw.common.constant;

/**
 * redis常用key常量类
 *
 * @author luoquan
 * @date 2021/07/28
 */
public class RedisKeyConstant {

    /**
     * BIND_PHONES_UIN: data free迷你号绑定手机号key
     * LIMIT_KEY_MONTHS: 手机号当月购买次数限制key
     * PAY_FAILED_LIST: 迷你币下发失败list
     * CALLBACK_REQ_TEMP：回调信息备份
     * DF_ORDER_NO_TEMP: 生成订单号备份
     */
    public static final String BIND_PHONES_UIN ="DF_BIND_PHONES:";
    public static final String LIMIT_KEY_MONTHS ="DF_LIMIT_FLAG:";
    public static final String PAY_FAILED_LIST ="DF_FAILED_LIST:";
    public static final String CALLBACK_REQ_TEMP ="CALLBACK_REQ_TEMP:";
    public static final String DF_ORDER_NO_TEMP ="DF_ORDER_NO_TEMP:";

    /**
     * DF_UNICOM_CALLBACK_KEY: 联通运营商回调锁key
     */
    public static final String DF_UNICOM_CALLBACK_KEY = "DF_UNICOM_CALLBACK:";

    /**
     * USER_LOGIN_DATA: 用户登录态
     */
    public static final String USER_LOGIN_DATA = "USER_LOGIN_DATA:";

}

package com.miniw.web.application;


import com.miniw.common.base.ResultCode;
import com.miniw.common.base.ResultMsg;
import com.miniw.common.base.UserLoginData;
import com.miniw.external.client.minicoin.response.CoinPayResponse;
import com.miniw.web.param.req.GenerateDfOrderRequest;

import java.io.IOException;
import java.text.ParseException;

/**
 *
 * @author luoquan
 * @date 2021/08/03
 */
public interface TDfOrderLogApplication {

    /**
     * 绑定手机验证
     *
     * @param uin 迷你号
     * @return {@link ResultCode}
     */
    ResultMsg bindPhoneNumVerify(String uin);


    /**
     * 生成免流订单号
     *
     * @param request 请求
     * @return {@link ResultMsg}
     * @throws InterruptedException 中断异常
     */
    ResultMsg generateDfOrder(GenerateDfOrderRequest request) throws InterruptedException;


    /**
     * 回调函数（用于接收合作方订购结果
     *
     * @param data 加密字符串
     * @param sign 签名
     * @return {@link ResultMsg}
     * @throws Exception 异常
     */
    ResultMsg callBack(String data, String sign) throws Exception;

    /**
     * 续订回调函数（续订合作方仅提供电话号码
     *
     * @param data 加密字符串
     * @param sign 签名
     * @return {@link ResultMsg}
     * @throws Exception 异常
     */
    ResultMsg renewFunction(String data, String sign) throws Exception;


    /**
     * 迷你币充值
     *
     * @param payCoinNo 充值服订单号
     * @param phone     电话
     * @param isp       运营商类型（1-中国移动，2-中国联通，3-中国电信（预留）
     * @param uin       迷你号
     * @param country   国家
     * @param ip        ip
     * @param loginData 登录数据
     * @return {@link CoinPayResponse}
     */

    CoinPayResponse payCoin(UserLoginData loginData, String payCoinNo, String phone, Integer isp, String uin, String country, String ip);

    /**
     * 下发邮件
     *
     * @param uin 迷你号
     */
    void sendEmail(String uin);
}

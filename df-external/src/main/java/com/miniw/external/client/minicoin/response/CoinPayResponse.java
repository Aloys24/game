package com.miniw.external.client.minicoin.response;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 迷你币充值服返回结果集
 *
 * @author luoquan
 * @date 2021/08/05
 */
@NoArgsConstructor
@Data
public class CoinPayResponse {

    /**
     * return_code: 返回状态码
     * return_msg：返回信息
     *
     * eg：
     * {"return_code":"SUCCESS","return_msg":"充值成功","data":{"Uin":1000030452,"MiniCoin":150}}
     * {"return_code":"DUP_BILL","return_msg":"重复的订单","data":{"Uin":100002,"MiniCoin":150}}
     * {"return_code":"FAIL","return_msg":"充值失败TIMEOUT"}
     */
    private String return_code;
    private String return_msg;
    private ResultData data;

    /**
     * data：返回结果 无则为NULL
     *
     * eg：
     * "data":{"Uin":1000030452,"MiniCoin":150}
     */
    @NoArgsConstructor
    @Data
    public static class ResultData{
        /**
         * Uin：当前迷你号
         * MiniCoin：充值的迷你币数量
         */
        private String Uin;
        private Integer MiniCoin;

    }


}

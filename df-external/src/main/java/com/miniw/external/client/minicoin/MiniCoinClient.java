package com.miniw.external.client.minicoin;


import com.miniw.external.client.minicoin.config.MiniCoinPayConfig;
import com.miniw.external.client.minicoin.response.CoinPayResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 迷你币充值服
 *
 * @author luoquan
 * @date 2021/07/30
 */
@FeignClient(name = "miniCoinClient", configuration = MiniCoinPayConfig.class, url = "****")
public interface MiniCoinClient {

    /**
     * 充值
     *
     * @param miniCoin      币
     * @param uin           号
     * @param transactionId 订单id
     * @param productId     产品id
     * @param productName   产品名称
     * @param why           充值理由
     * @param reason        充值原因
     * @param money         充值金额（单位元
     * @return {@link Object}
     */
    @GetMapping("/***.php")
    CoinPayResponse coinPay(@RequestParam("minicoin") Integer miniCoin,
                 @RequestParam("uin") String uin,
                 @RequestParam("transaction_id") String transactionId,
                 @RequestParam("product_id") String productId,
                 @RequestParam("product_name") String productName,
                 @RequestParam("why") String why,
                 @RequestParam("reason") String reason,
                 @RequestParam("money") String money
                   );


}

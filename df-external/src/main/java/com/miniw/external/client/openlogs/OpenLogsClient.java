package com.miniw.external.client.openlogs;


import com.miniw.external.client.minicoin.config.MiniCoinPayConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 迷你玩大数据打点服务
 *
 * @author luoquan
 * @date 2021/07/30
 */
@FeignClient(name = "openLogsClient", configuration = MiniCoinPayConfig.class, url = "https://tj2.mini1.cn")
public interface OpenLogsClient {

    /**
     * 迷你币充值
     * @param k           打点id
     * @param uin         迷你号
     * @param apiId       渠道
     * @param version     版本
     * @param country     国家
     * @param lang        语言
     * @param ts          时间戳
     * @param phone       绑定手机号
     * @param isp         运营商 1-中国移动，2-中国联通，3-中国电信（预留）
     * @param addMiniCoin 迷你币增加的数量
     */
    @GetMapping("/miniworld")
    void openLog(@RequestParam("k") String k,
                            @RequestParam("v1") String uin,
                            @RequestParam("v2") String apiId,
                            @RequestParam("v3") String version,
                            @RequestParam("v4") String country,
                            @RequestParam("v5") String lang,
                            @RequestParam("v6") long ts,
                            @RequestParam("v7") String phone,
                            @RequestParam("v8") Integer isp,
                            @RequestParam("v9") String addMiniCoin
                            );


}

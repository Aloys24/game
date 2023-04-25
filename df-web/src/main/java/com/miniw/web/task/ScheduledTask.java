package com.miniw.web.task;

import cn.hutool.core.date.DateTime;
import com.alibaba.fastjson.JSONObject;
import com.miniw.common.base.UserLoginData;
import com.miniw.common.constant.MiniCoinConstant;
import com.miniw.common.constant.RedisKeyConstant;
import com.miniw.common.utils.RedisUtil;
import com.miniw.external.client.minicoin.MiniCoinClient;
import com.miniw.external.client.minicoin.params.CoinPayResultCode;
import com.miniw.external.client.minicoin.response.CoinPayResponse;
import com.miniw.external.client.openlogs.OpenLogsClient;
import com.miniw.external.client.openlogs.params.OpenLogsConstant;
import com.miniw.persistence.service.TDfOrderLogService;
import com.miniw.web.application.TDfOrderLogApplication;
import com.miniw.web.param.dto.PayFailedDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 计划任务
 *
 * @author luoquan
 * @date 2021/08/10
 */
@Component
@Slf4j
public class ScheduledTask {

    @Resource
    private RedisUtil redisUtil;
    @Resource
    private MiniCoinClient miniCoinClient;
    @Resource
    private TDfOrderLogApplication tDfOrderLogApplication;
    @Resource
    private TDfOrderLogService tDfOrderLogService;
    @Resource
    private OpenLogsClient openLogsClient;

    /**
     * （每1分钟执行一次
     */
    @Async // 多任务下可开启
    @Scheduled(cron = "0 0/1 * * * ? ")
    public void payCoinTask() {

        AtomicInteger count = new AtomicInteger(0);

        do {
            if(!redisUtil.hasKey(RedisKeyConstant.PAY_FAILED_LIST)){
                break;
            }

            log.info("补偿任务执行：{}", LocalDateTime.now());
            PayFailedDto failedDto = JSONObject.parseObject(
                    String.valueOf(redisUtil.lRightPop(RedisKeyConstant.PAY_FAILED_LIST)), PayFailedDto.class);
            final String uin = failedDto.getUin();
            log.info("正在对迷你号：{}进行迷你币补发：{}", uin, LocalDateTime.now());

            // 获取缓存中的登录态
            final Object o = redisUtil.hGet(RedisKeyConstant.USER_LOGIN_DATA, uin);
            if(!redisUtil.hHasKey(RedisKeyConstant.USER_LOGIN_DATA, uin) || null == o){
                log.error("无法获取该迷你号的登录态：{}", uin);
                continue;
            }
            UserLoginData loginData = JSONObject.parseObject(String.valueOf(o), UserLoginData.class);
            final String apiId = loginData.getApiId();
            String version = loginData.getVer();

            // 下发迷你币
            CoinPayResponse coinPayResponse = miniCoinClient.coinPay(MiniCoinConstant.DF_MINICOIN, uin, failedDto.getPayCoinNo(),
                    MiniCoinConstant.DF_PRODUCT_ID,
                    MiniCoinConstant.DF_PRODUCT_NAME,
                    MiniCoinConstant.DF_WHY,
                    MiniCoinConstant.DF_REASON,
                    MiniCoinConstant.DF_MONEY
            );

            final String country = loginData.getCountry();
            final String langId = loginData.getLangId();
            final long ts = loginData.getTs();
            final String phone = failedDto.getPhone();
            final Integer isp = failedDto.getIsp();

            log.info("账号：{} 补发迷你币成功， 开始打点：url: {}， 参数：【k={}, v1={}, v2={}, v3={}, v4={}, v5={}, v6={}, v7={}, v8={}, v9={}】",
                    uin, "https://tj2.mini1.cn/miniworld", OpenLogsConstant.OPEN_LOGS_K, uin, apiId, version, country, langId, ts, phone, isp, MiniCoinConstant.DF_MINICOIN);

            // 数据打点
            openLogsClient.openLog(OpenLogsConstant.OPEN_LOGS_K,
                    uin, apiId, version, country,
                    langId, ts==0?System.currentTimeMillis():ts,
                    phone, isp, String.valueOf(MiniCoinConstant.DF_MINICOIN)
            );

            log.info("账号:{}打点成功", uin);

            // 如果成功，记录数据，删除该元素
            if (coinPayResponse.getReturn_code().equals(CoinPayResultCode.COIN_PAY_SUCCESS.getCode())) {
                tDfOrderLogService.saveByCallBack(uin,
                        phone,
                        failedDto.getPayCoinNo(),
                        failedDto.getDfOrderNo(),
                        isp,
                        new DateTime(failedDto.getCreated()),
                        new DateTime(failedDto.getEnded()), new Date(),
                        MiniCoinConstant.DF_LOGIN_CHANNEL,
                        MiniCoinConstant.DF_VERSION,
                        failedDto.getCountry(),
                        failedDto.getIp(),
                        MiniCoinConstant.DF_MINICOIN);

                // 更新手机号限制次数key（套餐当月最后一天
                final String limitKey = String.format("%s%s", RedisKeyConstant.LIMIT_KEY_MONTHS, phone);
//                val betweenDay = DateUtil.betweenDay(failedDto.getEnded(), DateUtil.offsetDay(failedDto.getEnded(), 30), true);
//                redisUtil.sPut(limitKey, "true", failedDto.getEnded().getTime(), TimeUnit.MILLISECONDS);
                redisUtil.sPut(limitKey, "true", failedDto.getEnded());
                // 下发邮件
                tDfOrderLogApplication.sendEmail(uin);
                log.info("迷你号：{}补发成功：{}", uin, LocalDateTime.now());
                count.incrementAndGet();
                log.info("当前任务执行条数：{}", count);
            }
            log.error("迷你号：{}补发失败：{}, 五分钟后再次重试", uin, coinPayResponse.getReturn_msg());
        }while (count.get() <= 50);
    }


    /**
     * 清除迷你号绑定手机次数限制
     * （每个月5号凌晨
     */
    @Async
    @Scheduled(cron = "0 0 0 5 * ? ")
    public void delBindPhonesKeyMonth(){
        log.info("迷你号绑定手机次数缓存清除任务开始执行：{}", LocalDateTime.now());
        // 删除整个命名空间（ 用以处理可能存在的未过期的值
        redisUtil.delKey(RedisKeyConstant.BIND_PHONES_UIN);
    }


    /**
     * 清除手机号当月限制
     * （每个月1号凌晨
     */
    @Async
    @Scheduled(cron = "0 0 0 1 * ? ")
    public void delLimitMonthKeyMonth(){
//        log.info("迷你号清除手机号当月限制开始执行：{}", LocalDateTime.now());
        // 删除整个命名空间（ 用以处理可能存在的未过期的值
        redisUtil.delKeyByPrefix(null, RedisKeyConstant.LIMIT_KEY_MONTHS);
    }


}

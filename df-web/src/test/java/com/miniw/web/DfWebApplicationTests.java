package com.miniw.web;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSONObject;
import com.miniw.common.constant.MiniCoinConstant;
import com.miniw.common.constant.RedisKeyConstant;
import com.miniw.common.constant.SendEmailConstant;
import com.miniw.common.utils.RedisUtil;
import com.miniw.external.client.email.config.EmailParamsConfig;
import com.miniw.external.client.minicoin.MiniCoinClient;
import com.miniw.external.client.minicoin.response.CoinPayResponse;
import com.miniw.external.client.openlogs.OpenLogsClient;
import com.miniw.gameapi.api.EmailApi;
import com.miniw.gameapi.exception.GameApiException;
import com.miniw.persistence.service.TDfOrderLogService;
import com.miniw.web.application.TDfOrderLogApplication;
import com.miniw.web.param.dto.PayFailedDto;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

@SpringBootTest
public class DfWebApplicationTests {

    @Resource
    MiniCoinClient miniCoinClient;
    @Resource
    RedisUtil redisUtil;
    @Resource
    private TDfOrderLogApplication tDfOrderLogApplication;
    @Resource
    private EmailApi emailApi;
    @Resource
    private OpenLogsClient openLogsClient;
    @Resource
    private TDfOrderLogService tDfOrderLogService;

//    @Test
//    void contextLoads() {
//    }


//    @Test
//    public void gerOrderNo(){
//        System.out.println(dfOrderUtil.generateDfOrder("18870064467", "1"));
//    }

//    @Test
//    public void testRedis(){
//        val key = StrUtil.format("{}{}", RedisKeyConstant.BIND_PHONES_UIN, "10001");
//        System.out.println(redisTemplate.opsForList().size(key));
//    }

    @Test
    public void testCoinPay(){
        CoinPayResponse coinPayResponse = miniCoinClient.coinPay(MiniCoinConstant.DF_MINICOIN, "1000030452", "DF100002UNICOM20210804379532",
                MiniCoinConstant.DF_PRODUCT_ID,
                MiniCoinConstant.DF_PRODUCT_NAME,
                MiniCoinConstant.DF_WHY,
                MiniCoinConstant.DF_REASON,
                MiniCoinConstant.DF_MONEY
        );

        System.out.println(coinPayResponse);
    }


    @Test
    public void testHashIncr(){
        final String bindKey = String.format("%s%s", RedisKeyConstant.BIND_PHONES_UIN, 10003);
        System.out.println(redisUtil.hIncrement(bindKey, String.valueOf(10003), 1));

    }

    @Test
    public void testListGet(){
        final List<String> strings = redisUtil.lGet(RedisKeyConstant.PAY_FAILED_LIST, 0, 49);
        strings.forEach(System.out::println);
        strings.forEach(s -> {
            PayFailedDto failedDto = JSONObject.parseObject(s, PayFailedDto.class);
            System.out.println(failedDto);
        });
    }

    @Test
    public void testSendEmail() throws GameApiException {
        emailApi.sendEmail(Long.valueOf(734674607), SendEmailConstant.EMAIL_TITLE, SendEmailConstant.EMAIL_BODY, "", SendEmailConstant.EMAIL_FROM);

    }

    @Test
    public void testLimitKeyMonth(){
        // 当月限制次数校验（一个手机号仅限一次
        final String limitKey = String.format("%s%s", RedisKeyConstant.LIMIT_KEY_MONTHS, "18870064467");
        if (StringUtils.isNotBlank(redisUtil.sGet(limitKey))) {
            System.out.println(limitKey + "当月已绑定");
        }
        // 更新限制次数key（到期时间往后推30天
//        redisUtil.sPut(limitKey, "true", 20, TimeUnit.DAYS);
    }


    @Test
    public void testOpenLog(){
        openLogsClient.openLog(
                "15386",
                "734676044",
                "999",
                "0.43.0",
                "CN",
                "0",
                1587204294,
                "18870064467",
        2,
        "150"
        );
    }

    @Test
    public void testMonth(){
        final DateTime nextMonth = DateUtil.nextMonth();
        System.out.println(nextMonth);
        final DateTime beginOfMonth = DateUtil.beginOfMonth(nextMonth);
        System.out.println(beginOfMonth);
        final DateTime offsetDay = DateUtil.offsetDay(beginOfMonth, 4);
        System.out.println(offsetDay);
        System.out.println(DateUtil.endOfDay(offsetDay));

    }

    @Test
    public void testEmailConfig(){
        System.out.println(EmailParamsConfig.SEND_URL);
    }

    @Test
    public void testURL() throws UnknownHostException {
        System.out.println(InetAddress.getByName("notice.pay.mini1.cn").getHostAddress());
    }

    @Test
    public void testPushFailedList(){
        redisUtil.lRightPush(RedisKeyConstant.PAY_FAILED_LIST,
                PayFailedDto.covertTo("734676044", "11111111111", "DF734676044UNICOM20210811379538", "DFPAY734676044UNICOM20210816241560", 2,
                        new Date(), new Date(),
                        "CN", "127.0.0.1"));
    }

    @Test
    public void testHputAndEx() throws ParseException {
//        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        Date END_TIME = dateFormat.parse("2022-04-30 23:59:59");
//
//        final String limitKey = String.format("%s%s", RedisKeyConstant.LIMIT_KEY_MONTHS, "18870066467");
//        redisUtil.sPut(limitKey, true, END_TIME);
//
//        System.out.println(StringUtils.isNotBlank(redisUtil.sGet(limitKey)));
        System.out.println(redisUtil.delKey(RedisKeyConstant.LIMIT_KEY_MONTHS));
    }

    @Test
    public void testExpireKey(){
    }


    @Test
    public void testDelBindPhone(){
        System.out.println(redisUtil.delKeyByPrefix(null, RedisKeyConstant.LIMIT_KEY_MONTHS));

    }



}

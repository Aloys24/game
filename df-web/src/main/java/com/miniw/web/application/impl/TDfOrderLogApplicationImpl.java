package com.miniw.web.application.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.miniw.common.base.ResultCode;
import com.miniw.common.base.ResultMsg;
import com.miniw.common.base.UserLoginData;
import com.miniw.common.constant.MiniCoinConstant;
import com.miniw.common.constant.RedisKeyConstant;
import com.miniw.common.constant.SendEmailConstant;
import com.miniw.common.constant.SignConstant;
import com.miniw.common.exception.ResultException;
import com.miniw.common.utils.DfOrderUtil;
import com.miniw.common.utils.RSAUtil;
import com.miniw.common.utils.RedisUtil;
import com.miniw.common.utils.ThreadPoolUtil;
import com.miniw.external.client.minicoin.MiniCoinClient;
import com.miniw.external.client.minicoin.params.CoinPayResultCode;
import com.miniw.external.client.minicoin.response.CoinPayResponse;
import com.miniw.external.client.openlogs.OpenLogsClient;
import com.miniw.external.client.openlogs.params.OpenLogsConstant;
import com.miniw.gameapi.api.EmailApi;
import com.miniw.gameapi.exception.GameApiException;
import com.miniw.persistence.model.TDfOrderLog;
import com.miniw.persistence.service.TDfOrderLogService;
import com.miniw.web.application.TDfOrderLogApplication;
import com.miniw.web.param.dto.CallBackDto;
import com.miniw.web.param.dto.PayFailedDto;
import com.miniw.web.param.req.GenerateDfOrderRequest;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.net.InetAddress;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * @author luoquan
 * @date 2021/08/03
 */

@Slf4j
@Service
public class TDfOrderLogApplicationImpl implements TDfOrderLogApplication {

    @Resource
    private RedisUtil redisUtil;
    @Resource
    private MiniCoinClient miniCoinClient;
    @Resource
    private OpenLogsClient openLogsClient;
    @Resource
    private TDfOrderLogService tDfOrderLogService;
    @Resource
    private Redisson redisson;
    @Resource
    private EmailApi emailApi;


    /**
     * 绑定手机验证
     *
     * @param uin 迷你号
     * @return {@link ResultCode}
     */
    @Override
    public ResultMsg bindPhoneNumVerify(String uin) {
        val key = StrUtil.format("{}{}", RedisKeyConstant.BIND_PHONES_UIN, uin);
//        if (redisUtil.hGet(key, uin) == null) {
//            final DateTime offsetDay = DateUtil.offsetDay(DateUtil.beginOfMonth(DateUtil.nextMonth()), 4);
//            final DateTime expireTime = DateUtil.endOfDay(offsetDay);
//            redisUtil.hIncrementAndEx(key, uin, 1, expireTime);
//        }
        if (redisUtil.hGetInt(key, uin) >= 3) {
            return new ResultMsg(ResultCode.DF_BIND_LIMITED.getCode(), ResultCode.DF_BIND_LIMITED.getMsg());
        } else {
            return new ResultMsg(ResultCode.SYS_SUCCESS.getCode(), ResultCode.SYS_SUCCESS.getMsg());
        }

    }

    /**
     * 生成免流订单号
     *
     * @param request 请求
     * @return {@link ResultMsg}
     */
    @Override
    public ResultMsg generateDfOrder(GenerateDfOrderRequest request) throws InterruptedException {
        log.debug("接口：{} 请求参数：{}", "generateDfOrder", JSON.toJSONString(request));
        ResultMsg result = new ResultMsg();

        val lockKey = String.format("%s%s", RedisKeyConstant.DF_UNICOM_CALLBACK_KEY, request.getUin());
        final RLock lock = redisson.getLock(lockKey);
        if (!lock.tryLock(0, 5, TimeUnit.SECONDS)) {
            log.error("迷你号{}生成订单号正在处理", request.getUin());
            return new ResultMsg( ResultCode.SYS_BUSY.getCode(), "生成订单号正在处理,请稍后再试");
        }

        final String orderNo = Optional.of(DfOrderUtil.generateDfOrder(request.getUin(), request.getIsp())).orElseThrow(() -> new ResultException(ResultCode.SYS_ERROR));

        result.setMsg(ResultCode.SYS_SUCCESS.getMsg());
        result.setCode(ResultCode.SYS_SUCCESS.getCode());
        result.addData("DfOrderNo", orderNo);
        log.debug("当前迷你号：{}生成的订单号为：{}", request.getUin(), orderNo);
        redisUtil.hPutAndEx(RedisKeyConstant.DF_ORDER_NO_TEMP, request.getUin(), orderNo, DateUtil.endOfDay(DateUtil.endOfMonth(new Date())));
        return result;
    }

    /**
     * 回调函数（用于接收合作方订购结果
     * <p>
     * 1. 签名合法性校验
     * 2. 确认是否接收到核心参数phone、dfOrderNo、<br/>
     * 3. 当月限制次数校验（一个手机号仅限一次）<br/>
     * 4. 持久化至Redis，确保成功<br/>
     * 5. 调用充值服下发迷你币，确认下发成功<br/>
     * 如果步骤5出现异常，提供重试策略；<br/>
     * 3.1: 记录充值失败的迷你号<br/>
     * 3.2: 定时补偿失败数据<br/>
     * 6. 调用发送邮件接口异步发送邮件<br/>
     * <p/>
     *
     * @param data 请求集
     * @return {@link ResultMsg}
     */
    @Override
    public ResultMsg callBack(String data, String sign) throws Exception {
        log.info("接收到请求：data：{} ------- sign：{}", data, sign);
        // 验证签名合法性,解析参数
        CallBackDto callBackDto = verifyAndParsing(data, sign);
        log.info("当前请求签名校验通过:{}", callBackDto);

        if (!callBackDto.getCode().equals(ResultCode.DF_CALLBACK_SUCCESS.getCode())) {
            log.error("回调的code错误：{}", callBackDto.getCode());
            new ResultMsg(ResultCode.SYS_BAN.getCode(), ResultCode.SYS_BAN.getMsg());
        }

        final DateTime created = DateUtil.parse(callBackDto.getCreated(), "yyyy-MM-dd HH:mm");
        final DateTime ended = CallBackDto.convertTo(callBackDto.getCreated());
        final String phone = callBackDto.getPhone();
        final Integer isp = callBackDto.getIsp();
        final String country = callBackDto.getCountry();
        final String dfOrderNo = callBackDto.getDfOrderNo();
        if(StringUtils.isBlank(dfOrderNo)){
            log.error("回调的订单号为空：{}", dfOrderNo);
            return new ResultMsg(ResultCode.SYS_PARAM_ERROR.getCode(), "请检查传入的订单号！");
        }

        if(!dfOrderNo.contains("DF") || !dfOrderNo.contains("UNICOM")){
            log.error("回调的订单号格式有误：{}", dfOrderNo);
            return new ResultMsg(ResultCode.SYS_PARAM_ERROR.getCode(), "请检查传入的订单号！");
        }

        final String uin = dfOrderNo.split("UNICOM")[0].replaceAll("DF", "");
        final String ip = InetAddress.getByName("notice.pay.mini1.cn").getHostAddress();

        // 获取缓存中的登录态
        final Object o = redisUtil.hGet(RedisKeyConstant.USER_LOGIN_DATA, uin);
        if(!redisUtil.hHasKey(RedisKeyConstant.USER_LOGIN_DATA, uin) || null == o){
            log.error("无法获取该迷你号的登录态：{}", uin);
            return new ResultMsg( ResultCode.SYS_NEED_LOGIN.getCode(), "无法获取该迷你号的登录信息");
        }
        UserLoginData loginData = JSONObject.parseObject(String.valueOf(o), UserLoginData.class);
        String channel = loginData.getApiId();
        String version = loginData.getVer();

        val lockKey = String.format("%s%s", RedisKeyConstant.DF_UNICOM_CALLBACK_KEY, phone);
        final RLock lock = redisson.getLock(lockKey);
        if (!lock.tryLock(0, 15, TimeUnit.SECONDS)) {
            log.error("迷你号{}订购成功回调正在处理", uin);
            return new ResultMsg( ResultCode.SYS_BUSY.getCode(), "回调正在处理,请稍后再试");
        }

        try {
            // 回调信息备份
            redisUtil.hPut(RedisKeyConstant.CALLBACK_REQ_TEMP, phone, JSON.toJSONString(callBackDto));

            // 手机号当月限购次数校验（一个手机号仅限一次
            final String limitKey = String.format("%s%s", RedisKeyConstant.LIMIT_KEY_MONTHS, phone);
            if (StringUtils.isNotBlank(redisUtil.sGet(limitKey))) {
                log.error("手机号：{} 已达当月限购", phone);
                return new ResultMsg(ResultCode.DF_LIMITED_MONTH.getCode(), ResultCode.DF_LIMITED_MONTH.getMsg());
            }

            log.info("回调业务处理中dfOrderNo:{}", dfOrderNo);
            // 设置迷你号绑定手机次数缓存（过期时间为下个月5号凌晨
            final DateTime offsetDay = DateUtil.offsetDay(DateUtil.beginOfMonth(DateUtil.nextMonth()), 4);
            final DateTime expireTime = DateUtil.endOfDay(offsetDay);
            redisUtil.hIncrementAndEx(RedisKeyConstant.BIND_PHONES_UIN, uin, 1, expireTime);

            // 获取本次充值订单号 充值迷你币
            final String payCoinOrderNo = DfOrderUtil.generatePayCoinOrderNo(uin, "UNICOM");
            CoinPayResponse coinPayResponse = this.payCoin(loginData, payCoinOrderNo, phone, isp, uin, country, ip);
            // 重复订单 直接返回 不予充值
            if(coinPayResponse.getReturn_code().equals(CoinPayResultCode.COIN_DUP_BILL.getCode())){
                // 清除回调备份
                redisUtil.hDelKey(RedisKeyConstant.CALLBACK_REQ_TEMP, phone);
                return new ResultMsg(ResultCode.DF_CALLBACK_FAILED.getCode(), CoinPayResultCode.COIN_DUP_BILL.getMsg());
            }

            // 下发失败
            if (!coinPayResponse.getReturn_code().equals(CoinPayResultCode.COIN_PAY_SUCCESS.getCode())) {
                log.error("迷你号：{}充值失败---resultCode：{}----resultMsg：{}", uin, coinPayResponse.getReturn_code(), coinPayResponse.getReturn_msg());
                redisUtil.lRightPush(RedisKeyConstant.PAY_FAILED_LIST,
                        PayFailedDto.covertTo(uin, phone, payCoinOrderNo, dfOrderNo, isp,
                                created, ended,
                                country, ip));
                // 清除回调备份
                redisUtil.hDelKey(RedisKeyConstant.CALLBACK_REQ_TEMP, phone);
                return new ResultMsg(ResultCode.DF_CALLBACK_FAILED.getCode(), CoinPayResultCode.COIN_PAY_FAIL.getMsg());
            }

            // 充值迷你币成功，记录数据
            tDfOrderLogService.saveByCallBack(uin, phone, payCoinOrderNo, dfOrderNo, isp, created, ended, new Date(), channel, version, country, ip, MiniCoinConstant.DF_MINICOIN);

            // 清除登录态缓存
            redisUtil.hDelKey(RedisKeyConstant.USER_LOGIN_DATA, uin);

            // 设置手机号当月限购次数key（套餐当月最后一天
            redisUtil.sPut(limitKey, true, ended);
            // 下发邮件
            this.sendEmail(uin);
            log.info("账号:{}下发迷你币success,发送邮件完毕", uin);

            // 清除回调备份
            redisUtil.hDelKey(RedisKeyConstant.CALLBACK_REQ_TEMP, phone);
            return new ResultMsg(ResultCode.DF_CALLBACK_SUCCESS.getCode(), ResultCode.DF_CALLBACK_SUCCESS.getMsg());
        } finally {
            if (lock.isLocked()) {
                lock.unlock();
            }
        }
    }

    /**
     * 续订回调函数（续订合作方仅提供电话号码
     * <p>
     * 1. 通过号码获取该uin订单<br/>
     * 2. 更改套餐到期时间，续期limit_key<br/>
     * 3. 发送迷你币，确认发送成功<br/>
     * 如果步骤3出现异常，提供重试策略:<br/>
     * 3.1: 记录充值失败的迷你号<br/>
     * 3.2: 定时补偿失败数据<br/>
     * 4. 下发邮件<br/>
     * <p/>
     *
     * @param data 加密字符串
     * @param sign 签名
     * @return {@link ResultMsg}
     * @throws Exception 异常
     */
    @Override
    public ResultMsg renewFunction(String data, String sign) throws Exception {
        log.info("接收到续订请求：data：{} ------- sign：{}", data, sign);
        // 验证签名合法性,解析参数
        CallBackDto callBackDto = verifyAndParsing(data, sign);
        log.info("当前请求签名校验通过:{}", callBackDto);

        if (!callBackDto.getCode().equals(ResultCode.DF_CALLBACK_SUCCESS.getCode())) {
            new ResultMsg(ResultCode.SYS_BAN.getCode(), ResultCode.SYS_BAN.getMsg());
        }

        final String phone = callBackDto.getPhone();

        // 获取套餐开通时间最近的一条订单
        final List<TDfOrderLog> tDfOrderList = tDfOrderLogService.lambdaQuery().eq(TDfOrderLog::getPhone, phone).orderByDesc(TDfOrderLog::getCreated).list();
        if(CollectionUtil.isEmpty(tDfOrderList)){
            log.error("找不到phone关联的订单：{}", phone);
            throw new ResultException(ResultCode.SYS_NON_EXIST.getMsg(), ResultCode.SYS_NON_EXIST);
        }

        final TDfOrderLog tDfOrderLog = tDfOrderList.get(0);
        final String uin = tDfOrderLog.getUin();
        final String dfOrderNo = tDfOrderLog.getDfOrderNo();
        final Integer isp = 2;
        log.info("获取到uin：{}--历史订单号：{}--套餐到期时间：{}", uin, dfOrderNo, tDfOrderLog.getEnded());

        val lockKey = String.format("%s%s", RedisKeyConstant.DF_UNICOM_CALLBACK_KEY, phone);
        final RLock lock = redisson.getLock(lockKey);
        if (!lock.tryLock(0, 15, TimeUnit.SECONDS)) {
            log.error("迷你号{}续订成功回调正在处理", uin);
            throw new ResultException("回调正在处理,请稍后再试", ResultCode.SYS_BUSY);
        }

        try {

            // 回调信息备份
            redisUtil.hPut(RedisKeyConstant.CALLBACK_REQ_TEMP, phone, JSON.toJSONString(callBackDto));

            // 手机号当月限购次数校验（一个手机号仅限一次
            final String limitKey = String.format("%s%s", RedisKeyConstant.LIMIT_KEY_MONTHS, phone);
            if (StringUtils.isNotBlank(redisUtil.sGet(limitKey))) {
                log.error("该手机号：{}当月达到限购次数", phone);
                return new ResultMsg(ResultCode.DF_LIMITED_MONTH.getCode(), ResultCode.DF_LIMITED_MONTH.getMsg());
            }
            // 设置迷你号绑定手机次数缓存（过期时间为下个月5号凌晨
//            final String bindKey = String.format("%s%s", RedisKeyConstant.BIND_PHONES_UIN, uin);
            final DateTime offsetDay = DateUtil.offsetDay(DateUtil.beginOfMonth(DateUtil.nextMonth()), 4);
            final DateTime expireTime = DateUtil.endOfDay(offsetDay);
            redisUtil.hIncrementAndEx(RedisKeyConstant.BIND_PHONES_UIN, uin, 1, expireTime);

            // 获取新的充值订单号 充值迷你币
            final String payCoinOrderNo = DfOrderUtil.generatePayCoinOrderNo(uin, "UNICOM");
            CoinPayResponse coinPayResponse = this.payCoin(null, payCoinOrderNo, phone, isp, uin, tDfOrderLog.getCountry(), tDfOrderLog.getIp());

            // 重复订单 直接返回 不予充值
            if(coinPayResponse.getReturn_code().equals(CoinPayResultCode.COIN_DUP_BILL.getCode())){
                // 清除回调备份
                redisUtil.hDelKey(RedisKeyConstant.CALLBACK_REQ_TEMP, phone);
                return new ResultMsg(ResultCode.DF_CALLBACK_FAILED.getCode(), CoinPayResultCode.COIN_DUP_BILL.getMsg());
            }

            // 下发失败
            if (!coinPayResponse.getReturn_code().equals(CoinPayResultCode.COIN_PAY_SUCCESS.getCode())) {
                log.error("迷你号：{}充值失败---resultCode：{}----resultMsg：{}", uin, coinPayResponse.getReturn_code(), coinPayResponse.getReturn_msg());
                redisUtil.lRightPush(RedisKeyConstant.PAY_FAILED_LIST,
                        PayFailedDto.covertTo(uin, phone, payCoinOrderNo, dfOrderNo, isp,
                                tDfOrderLog.getCreated(), tDfOrderLog.getEnded(),
                                tDfOrderLog.getCountry(), tDfOrderLog.getIp()));
                // 清除回调备份
                redisUtil.hDelKey(RedisKeyConstant.CALLBACK_REQ_TEMP, phone);
                return new ResultMsg(ResultCode.DF_CALLBACK_FAILED.getCode(), CoinPayResultCode.COIN_PAY_FAIL.getMsg());
            }

            // 充值迷你币成功，更新数据
            // 结束时间续期为下个月最后一天24点
            final DateTime dateTime = DateUtil.offsetMonth(tDfOrderLog.getEnded(), 1);
            final DateTime endOfDay = DateUtil.endOfDay(DateUtil.endOfMonth(dateTime));
            tDfOrderLog.setEnded(endOfDay);
            tDfOrderLog.setPayCoinNo(payCoinOrderNo);
            tDfOrderLog.setCoinCreated(new Date());
//            redisUtil.expireKey(limitKey, 30, TimeUnit.DAYS);
            tDfOrderLogService.saveOrUpdate(tDfOrderLog);

            // 设置手机号当月限购次数key（套餐结束时间当月最后一天
//            val betweenDay = DateUtil.betweenDay(endOfDay, DateUtil.offsetDay(endOfDay, 30), true);
//            redisUtil.sPut(limitKey, "true", endOfDay.getTime(), TimeUnit.MILLISECONDS);
            redisUtil.sPut(limitKey, "true", endOfDay);

            // 发送邮件
            this.sendEmail(uin);
            log.info("账号:{}下发迷你币success,发送邮件完毕", uin);

            // 清除回调备份
            redisUtil.hDelKey(RedisKeyConstant.CALLBACK_REQ_TEMP, phone);
            return new ResultMsg(ResultCode.DF_CALLBACK_SUCCESS.getCode(), ResultCode.DF_CALLBACK_SUCCESS.getMsg());
        } finally {
            if (lock.isLocked()) {
                lock.unlock();
            }
        }
    }

    /**
     * 迷你币充值
     *
     * @param phone     电话
     * @param isp       运营商类型（1-中国移动，2-中国联通，3-中国电信（预留）
     * @param uin       迷你号
     * @param country   国家
     * @param ip        ip
     * @param loginData 登录数据
     * @return {@link CoinPayResponse}
     */
    @Override
    public CoinPayResponse payCoin(UserLoginData loginData, String payCoinNo, String phone, Integer isp, String uin, String country, String ip) {
        log.info("账号:{}正在下发迷你币", uin);
        // 下发迷你币
        CoinPayResponse coinPayResponse = miniCoinClient.coinPay(MiniCoinConstant.DF_MINICOIN, uin, payCoinNo,
                MiniCoinConstant.DF_PRODUCT_ID,
                MiniCoinConstant.DF_PRODUCT_NAME,
                MiniCoinConstant.DF_WHY,
                MiniCoinConstant.DF_REASON,
                MiniCoinConstant.DF_MONEY
        );


        // 无法预知续订请求，此处对于没有登录态的请求 部分字段不予上报
        String apiId = null, version = null, lang = null;
        long ts = 0;
        if(null != loginData){
            apiId = loginData.getApiId();
            version = loginData.getVer();
            lang = loginData.getLangId();
            ts = loginData.getTs();
        }
        log.info("账号：{} 下发迷你币成功， 开始打点：url: {}， 参数：【k={}, v1={}, v2={}, v3={}, v4={}, v5={}, v6={}, v7={}, v8={}, v9={}】",
                uin, "https://tj2.mini1.cn/miniworld", OpenLogsConstant.OPEN_LOGS_K, uin, apiId, version, country, lang, ts, phone, isp, MiniCoinConstant.DF_MINICOIN);

        // 数据打点
        openLogsClient.openLog(OpenLogsConstant.OPEN_LOGS_K,
                uin, apiId, version, country,
                lang, ts==0?System.currentTimeMillis():ts,
                phone, isp, String.valueOf(MiniCoinConstant.DF_MINICOIN)
                );

        log.info("账号:{}打点成功", uin);

        return coinPayResponse;
    }


    /**
     * 验证和解析参数
     *
     * @param data 数据
     * @param sign 标志
     * @return {@link CallBackDto}
     * @throws Exception 异常
     */
    private CallBackDto verifyAndParsing(String data, String sign) throws Exception {

        if (StringUtils.isBlank(data) || StringUtils.isBlank(sign)) {
            log.info("未携带正确签名：data{} sign{}", data, sign);
            throw new ResultException("未携带正确签名", ResultCode.DF_CALLBACK_UNKNOW_SIGN);
        }

        String decrypt = RSAUtil.decrypt(data, RSAUtil.getPrivateKey(SignConstant.MINIW_PRIVATE_KEY_PEM));
        log.info("解密后：{}", decrypt);
        if (!RSAUtil.verify(decrypt, RSAUtil.getPublicKey(SignConstant.UNICOM_PUBLIC_KEY_PEM), sign)) {
            throw new ResultException("非法请求", ResultCode.DF_CALLBACK_BAD_BEQUEST);
        }

        return JSONObject.parseObject(decrypt, CallBackDto.class);
    }

    /**
     * 下发邮件
     *
     * @param uin 迷你号
     */
    @Override
    public void sendEmail(String uin) {
        // 异步发送邮件
        CompletableFuture<Void> sendEmail = CompletableFuture.runAsync(() -> {
            try {
                emailApi.sendEmail(Long.valueOf(uin), SendEmailConstant.EMAIL_TITLE, SendEmailConstant.EMAIL_BODY, "", SendEmailConstant.EMAIL_FROM);
            } catch (GameApiException e) {
                log.error(e.getMessage(), e);
            }
        }, ThreadPoolUtil.getThreadPollProxy().poolExecutor);
        sendEmail.thenRun(() -> log.info("邮件发送任务已完成"));
    }


}

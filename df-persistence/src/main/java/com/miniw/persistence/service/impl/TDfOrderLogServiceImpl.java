package com.miniw.persistence.service.impl;


import cn.hutool.core.date.DateTime;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.miniw.persistence.mapper.TDfOrderLogMapper;
import com.miniw.persistence.model.TDfOrderLog;
import com.miniw.persistence.service.TDfOrderLogService;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 *
 * @author luoquan
 * @date 2021/08/01
 */
@Service
public class TDfOrderLogServiceImpl extends ServiceImpl<TDfOrderLogMapper, TDfOrderLog>
        implements TDfOrderLogService {

    /**
     * 回调接口保存实体.
     *
     * @param uin            迷你号
     * @param phone          电话
     * @param payCoinOrderNo 充值服订单号
     * @param dfOrderNo      免流订单号
     * @param isp            运营商类型
     * @param created        套餐订购日期
     * @param ended          套餐结束日期
     * @param coinCreated    充值迷你币日期
     * @param dfLoginChannel 登录渠道
     * @param dfVersion      登录版本
     * @param country        国家
     * @param ip             IP地址
     * @param addNum         迷你币增量
     */
    @Override
    public void saveByCallBack(String uin, String phone, String payCoinOrderNo, String dfOrderNo, Integer isp, DateTime created, DateTime ended, Date coinCreated, String dfLoginChannel, String dfVersion, String country, String ip, Integer addNum) {
        TDfOrderLog tDfOrderLog = new TDfOrderLog();
        tDfOrderLog.setUin(uin);
        tDfOrderLog.setPhone(phone);
        tDfOrderLog.setDfOrderNo(dfOrderNo);
        tDfOrderLog.setPayCoinNo(payCoinOrderNo);
        tDfOrderLog.setIsp(isp);
        tDfOrderLog.setCreated(created);
        tDfOrderLog.setEnded(ended);
        tDfOrderLog.setCoinCreated(coinCreated);
        tDfOrderLog.setLoginChannel(dfLoginChannel);
        tDfOrderLog.setVersion(dfVersion);
        tDfOrderLog.setCountry(country);
        tDfOrderLog.setIp(ip);
        tDfOrderLog.setAddNum(addNum);
        this.saveOrUpdate(tDfOrderLog);
    }

    /**
     * 根据uin跟phone查询实体
     *
     * @param uin   迷你号
     * @param phone 电话
     * @return {@link TDfOrderLog}
     */
    @Override
    public TDfOrderLog getByUinAndPhone(String uin, String phone) {
        return this.lambdaQuery()
                .eq(TDfOrderLog::getUin, uin)
                .eq(TDfOrderLog::getPhone, phone)
                .one();
    }

    /**
     * 获取免流套餐详情
     *
     * @param uin   迷你号
     * @param phone 手机号
     * @param isp   运营商类型
     * @param start 开始时间（等同于发币时间
     * @param end   结束时间（等同于发币时间
     * @return {@link List <TDfOrderLog>}
     */
    @Override
    public List<TDfOrderLog> getDfStatusDetail(String uin, String phone, Integer isp, DateTime start, DateTime end) {
        QueryWrapper<TDfOrderLog> wrapper = Wrappers.query();
        Map<String, Object> params = new HashMap<>(3);
        params.put("uin", uin);
        params.put("phone", phone);
        params.put("isp", isp);

        final QueryWrapper<TDfOrderLog> order = wrapper.allEq(params, false)
                .ge(null!=start, "coin_created", start)
                .le(null!=end, "coin_created", end)
                .orderByDesc("coin_created")
                ;

        return this.list(order);
    }
}
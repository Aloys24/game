package com.miniw.persistence.service;

import cn.hutool.core.date.DateTime;
import com.baomidou.mybatisplus.extension.service.IService;
import com.miniw.persistence.model.TDfOrderLog;

import java.util.Date;
import java.util.List;

/**
 *
 * @author luoquan
 * @date 2021/08/01
 */
public interface TDfOrderLogService extends IService<TDfOrderLog> {

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
    void saveByCallBack(String uin, String phone, String payCoinOrderNo, String dfOrderNo, Integer isp, DateTime created, DateTime ended, Date coinCreated, String dfLoginChannel, String dfVersion, String country, String ip, Integer addNum);


    /**
     * 根据uin跟phone查询实体
     *
     * @param uin   迷你号
     * @param phone 电话
     * @return {@link TDfOrderLog}
     */
    TDfOrderLog getByUinAndPhone(String uin, String phone);

    /**
     * 获取免流套餐详情
     *
     * @param uin   迷你号
     * @param phone 手机号
     * @param isp   运营商类型
     * @param start 开始时间（等同于发币时间
     * @param end   结束时间（等同于发币时间
     * @return {@link List<TDfOrderLog>}
     */
    List<TDfOrderLog> getDfStatusDetail(String uin, String phone, Integer isp, DateTime start, DateTime end);
}
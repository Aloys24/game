package com.miniw.web.param.dto;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * 回调函数请求集
 *
 * @author luoquan
 * @date 2021/07/30
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CallBackDto {
    /**
     * code: 状态码（ 200 成功
     * isp： 运营商类型（当前仅有联通：1-中国移动，2-中国联通，3-中国电信（预留）
     * dfOrderNo：免流订单号（需保证与订购前一致
     * phone: 电话号码（需保证与当前订购者一致
     * country：国家（暂时默认为CN
     * created：套餐开通时间（格式：2021-08-01 11:00
     * ended: 套餐到期时间（权益结束时间为开通当月的最后一天的24点
     *
     */
    private Integer code;
    private Integer isp = 2;
    private String dfOrderNo;
    private String phone;
    private String country = "CN";
    private String created;
    private DateTime ended;


    public static DateTime convertTo(String created){
        final DateTime time = DateUtil.parse(created, "yyyy-MM-dd HH:mm");
        return DateUtil.endOfDay(DateUtil.endOfMonth(time));
    }

}






package com.miniw.web.param.req;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import lombok.Data;

/**
 * 客服后台免流详细查询请求
 *
 * @author luoquan
 * @date 2021/08/11
 */
@Data
public class DetailQueryRequest {

    /**
     * phone: 手机号
     * uin: 迷你号
     * isp: 运营商类型（移动、联通、电信
     * startTime: 开始时间（yyyy-MM-dd
     * endTime: 结束时间（yyyy-MM-dd
     *
     */
    private String phone;
    private String uin;
    private String isp;
    private String startTime;
    private String endTime;


    public static DateTime convertToCoinCreated(String time){
        return DateUtil.parse(time, "yyyy-MM-dd");
    }

    public static Integer ispConvertTo(String isp){
        switch (isp){
            case "中国移动":
                return 1;
            case "中国联通":
                return 2;
            case "中国电信":
                return 3;
            default:
                return 0;
        }
    }

}

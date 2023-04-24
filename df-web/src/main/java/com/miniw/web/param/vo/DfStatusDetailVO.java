package com.miniw.web.param.vo;

import lombok.Data;

/**
 * 免流套餐查询vo
 *
 * @author luoquan
 * @date 2021/08/11
 */
@Data
public class DfStatusDetailVO {

    /**
     * uin: 迷你号
     * phone: 手机号
     * isp: 运营商类型（中国移动｜中国联通｜中国电信
     * addNum: 增量
     * loginChanel: 登录渠道
     * loginVersion: 登录版本
     * coinCreated: 创建时间（等同于发币时间
     * created: 免流套餐开通时间
     * country: 国家（eg：CN
     */
    private String uin;
    private String phone;
    private String isp;
    private Integer addNum;
    private String loginChanel;
    private String loginVersion;
    private String coinCreated;
    private String created;
    private String country;


    public static String ispConvertTo(Integer isp){
        switch (isp){
            case 1:
                return "中国移动";
            case 2:
                return "中国联通";
            case 3:
                return "中国电信";
            default:
                return "";
        }
    }


}

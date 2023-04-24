package com.miniw.web.param.vo;

import lombok.Data;

import java.util.List;

/**
 * 获取迷你号免流套餐运营商类型vo
 *
 * @author luoquan
 * @date 2021/08/11
 */
@Data
public class DfStatusVO {

    /**
     * uinStatus：迷你号下免流卡
     */
    private List<UinStatus> uinStatus;

    @Data
    public static class UinStatus{

        /**
         * phone：手机号
         * isp： 运营商类型（移动、联通、电信
         */
        private String phone;
        private String isp;
    }


    public static String ispConvertTo(Integer isp){
        switch (isp){
            case 1:
                return "移动";
            case 2:
                return "联通";
            case 3:
                return "电信";
            default:
                return "";
        }
    }
}

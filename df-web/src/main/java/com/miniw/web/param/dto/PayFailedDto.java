package com.miniw.web.param.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Date;

/**
 * 迷你币充值失败DTO
 *
 * @author luoquan
 * @date 2021/08/10
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PayFailedDto {
    /**
     * uin: 迷你号（需保证与当前订购者一致
     * isp： 运营商类型（当前仅有联通：1-中国移动，2-中国联通，3-中国电信（预留）
     * dfOrderNo：免流订单号（需保证与订购前一致
     * payCoinNo: 充值服订单号
     * phone: 电话号码（需保证与当前订购者一致
     * country：国家（暂时默认为CN
     * created：套餐开通时间（格式：2021-08-01 11:00
     * ended: 套餐到期时间（权益结束时间为开通当月的最后一天的24点
     *
     */
    private String uin;
    private String phone;
    private Integer isp = 2;
    private String dfOrderNo;
    private String payCoinNo;
    private Date created;
    private Date ended;
    private String country = "CN";
    private String ip;
    private Date coinCreated;


    public static PayFailedDto covertTo(String uin, String phone, String dfOrderNo, String payCoinNo,Integer isp, Date created, Date ended,  String country, String ip){
        PayFailedDto dto = new PayFailedDto();
        dto.setUin(uin);
        dto.setPhone(phone);
        dto.setDfOrderNo(dfOrderNo);
        dto.setPayCoinNo(payCoinNo);
        dto.setIsp(isp);
        dto.setCreated(created);
        dto.setEnded(ended);
        dto.setCountry(country);
        dto.setIp(ip);
        return dto;
    }


}

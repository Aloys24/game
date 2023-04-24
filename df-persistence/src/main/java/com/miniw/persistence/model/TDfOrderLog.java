package com.miniw.persistence.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;


/**
 * 免流量订单记录
 *
 * @author luoquan
 * @date 2021/07/28
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class TDfOrderLog implements Serializable {
    private static final long serialVersionUID = -82678655075479967L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 订购迷你号
     */
    @TableField("uin")
    private String uin;

    /**
     * 订购手机号
     */
    @TableField("phone")
    private String phone;

    /**
     * 免流订单号
     */
    @TableField("df_order_no")
    private String dfOrderNo;

    /**
     *  充值服订单号
     */
    @TableField("pay_coin_no")
    private String payCoinNo;

    /**
     * 运营商类型（1-中国移动，2-中国联通，3-中国电信（预留
     */
    @TableField("isp")
    private Integer isp;

    /**
     * 免流套餐开通时间
     */
    @TableField("created")
    private Date created;

    /**
     * 免流套到期时间
     */
    @TableField("ended")
    private Date ended;

    /**
     * 发币时间
     */
    @TableField("coin_created")
    private Date coinCreated;

    /**
     * 登陆渠道(默认官版
     */
    @TableField("login_channel")
    private String loginChannel;

    /**
     * 登陆版本（eg：1.1.5
     */
    @TableField("version")
    private String version;

    /**
     * 国家
     */
    @TableField("country")
    private String country;

    /**
     * 发放迷你币操作ip
     */
    @TableField("ip")
    private String ip;

    /**
     * 下发迷你币数量
     */
    @TableField("add_num")
    private Integer addNum;

}
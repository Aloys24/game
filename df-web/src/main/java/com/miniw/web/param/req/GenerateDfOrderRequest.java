package com.miniw.web.param.req;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 生成免流订单请求集
 *
 * @author luoquan
 * @date 2021/07/30
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GenerateDfOrderRequest {
    /**
     * isp： 运营商类型（当前仅有联通：1-中国移动，2-中国联通，3-中国电信（预留）
     * uin: 迷你号
     */

    @NotNull(message = "isp不能为空") @Min(1) @Max(3)
    private String isp;
    @NotBlank(message = "uin不能为空")
    private String uin;
}

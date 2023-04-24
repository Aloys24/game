package com.miniw.external.client.minicoin.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.miniw.external.client.minicoin.params.CoinPayResultCode;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 充值迷你币异常
 *
 * @author luoquan
 * @date 2021/08/09
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@EqualsAndHashCode(callSuper = false)
public class CoinPayException extends RuntimeException {

    private String code;
    private String  reason;

    public CoinPayException(CoinPayResultCode code) {
        super(code.getMsg());
        this.code = code.getCode();
    }

    public CoinPayException(String reason, CoinPayResultCode resultCode) {
        super(reason);
        this.reason = reason;
        this.code   = resultCode.getCode();
    }
}

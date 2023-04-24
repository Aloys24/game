package com.miniw.common.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.miniw.common.base.ResultCode;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 自定义异常
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@EqualsAndHashCode(callSuper = false)
public class ResultException extends RuntimeException {
    private Integer code;
    private String  reason;

    public ResultException(ResultCode code) {
        super(code.getMsg());
        this.code = code.getCode();
    }

    public ResultException(String reason, ResultCode resultCode) {
        super(reason);
        this.reason = reason;
        this.code   = resultCode.getCode();
    }
}

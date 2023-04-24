package com.miniw.common.base;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


public class ResultMsg implements Serializable {
    private static final long serialVersionUID = 1L;
    private Integer code;
    private String msg;
    private Map<String, Object> data;

    public ResultMsg() {
        this.code = ResultCode.SYS_SUCCESS.getCode();
        this.msg = ResultCode.SYS_SUCCESS.getMsg();
        this.data = new HashMap(32);
    }

    public ResultMsg(Integer code, String msg) {
        this.code = ResultCode.SYS_SUCCESS.getCode();
        this.msg = ResultCode.SYS_SUCCESS.getMsg();
        this.data = new HashMap(32);
        this.code = code;
        this.msg = msg;
    }

    public ResultMsg(Integer code, String msg, Map<String, Object> data) {
        this.code = ResultCode.SYS_SUCCESS.getCode();
        this.msg = ResultCode.SYS_SUCCESS.getMsg();
        this.data = new HashMap(32);
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public void addData(String key, Object value) {
        this.data.put(key, value);
    }

    public static ResultMsg success(String msg) {
        ResultMsg r = new ResultMsg();
        r.setCode(ResultCode.SYS_SUCCESS.getCode());
        r.setMsg(msg);
        return r;
    }

    public static ResultMsg error() {
        ResultMsg r = new ResultMsg();
        r.setCode(ResultCode.SYS_ERROR.getCode());
        r.setMsg(ResultCode.SYS_ERROR.getMsg());
        return r;
    }

    public static ResultMsg success(Object obj) {
        ResultMsg r = new ResultMsg();
        r.setCode(ResultCode.SYS_SUCCESS.getCode());
        r.setMsg("success");
        r.addData("data", obj);
        return r;
    }

    public static ResultMsg fail(Integer code, String msg) {
        ResultMsg resultData = new ResultMsg();
        resultData.setCode(code);
        resultData.setMsg(msg);
        return resultData;
    }

    public ResultMsg error(Exception e) {
        this.setCode(ResultCode.SYS_ERROR.getCode());
        this.setMsg(String.format("系统发生错误: %s 请联系客服处理！", e.getMessage()));
        return this;
    }

    public ResultMsg error(int code, String msg) {
        this.setCode(code);
        this.setMsg(msg);
        return this;
    }

    public ResultMsg paramEror() {
        this.setCode(ResultCode.SYS_PARAM_ERROR.getCode());
        this.setMsg(ResultCode.SYS_PARAM_ERROR.getMsg());
        return this;
    }

    public ResultMsg notExist() {
        this.setCode(ResultCode.SYS_NON_EXIST.getCode());
        this.setMsg(ResultCode.SYS_NON_EXIST.getMsg());
        return this;
    }

    public Integer getCode() {
        return this.code;
    }

    public String getMsg() {
        return this.msg;
    }

    public Map<String, Object> getData() {
        return this.data;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof ResultMsg)) {
            return false;
        } else {
            ResultMsg other = (ResultMsg)o;
            if (!other.canEqual(this)) {
                return false;
            } else {
                label47: {
                    Object this$code = this.getCode();
                    Object other$code = other.getCode();
                    if (this$code == null) {
                        if (other$code == null) {
                            break label47;
                        }
                    } else if (this$code.equals(other$code)) {
                        break label47;
                    }

                    return false;
                }

                Object this$msg = this.getMsg();
                Object other$msg = other.getMsg();
                if (this$msg == null) {
                    if (other$msg != null) {
                        return false;
                    }
                } else if (!this$msg.equals(other$msg)) {
                    return false;
                }

                Object this$data = this.getData();
                Object other$data = other.getData();
                if (this$data == null) {
                    if (other$data != null) {
                        return false;
                    }
                } else if (!this$data.equals(other$data)) {
                    return false;
                }

                return true;
            }
        }
    }

    protected boolean canEqual(Object other) {
        return other instanceof ResultMsg;
    }

    @Override
    public int hashCode() {
        return Objects.hash(code, msg, data);
    }

    @Override
    public String toString() {
        return "ResultMsg(code=" + this.getCode() + ", msg=" + this.getMsg() + ", data=" + this.getData() + ")";
    }
}

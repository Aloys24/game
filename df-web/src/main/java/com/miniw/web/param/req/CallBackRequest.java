package com.miniw.web.param.req;

import lombok.Data;

/**
 * 回调请求实体
 *
 * @author luoquan
 * @date 2021/08/07
 */
@Data
public class CallBackRequest {

    /**
     * data: 加密字符串
     * sign：接口签名
     *
     * eg：
     * {
     *   "sign": "LKB3naAdDAHsQzfeV+ntMwvsxtBj3YEr8kVpz0M2M8ao4Jr6DFWfDFr8m/0Qz5rrb5txaWTeeRXQj25a6FvcGUMsR4VS9VX851AZRRdJYKsXr8s0HZxgYzO8O29Rf66kuQw+GdlkK7AgjUZ56uHli6vRAt+WszOqrjGyRvziGf6OF8kFqUZVrRI7EW9yIXU31AJ0ah0fVLWVWKDxTms1ScBeP+cR/kDWmKzEchzOBfxJ2RfwcvLWxn4I0VLADsmrgB19dEA/p6Ksbu2UFzaMSiKao56ao7Gl6MgL7rG2ATAKHfEr9rieuKgL9wUNEnDdHl+KCTN78GeaCP5S4EPRmQ==",
     *   "data": "ZfLt9CaX+TYuwIVhLVT/I/kO8S0MM3dfQtnjd1/FRPKo+mbjyRTx7n46isMVvAyM6fIsDLXuJgLLXANJqRmiVez/vdv38lflPdM+kt9Auwt9THmxLWYlBYdSIrVLCPRoCCL2box7aNbfU4LpeOqE+9tleLwPUv9/9oFRxfPl72v/+PmFwJZh5xYStttLjLCZ6MCrxJ+KxHBJL+f6YHWlYrGNgZgxEUClfqMqrY4dVbB8ictmXG6mMCG7DGqEXCgE1gpXRdO87nqxac8+9q2TZSj2pWisPrORITTZtZEOeoSzzqsF8Z+3OxQPXuL0thDE77Njill1OorlMNirDWrrPQ=="
     * }
     */
    private String data;
    private String sign;
}

//package com.miniw.common.aspect;
//
//
//import cn.hutool.json.JSONArray;
//import cn.hutool.json.JSONUtil;
//import com.alibaba.fastjson.JSON;
//import com.alibaba.fastjson.JSONObject;
//import com.miniw.common.annotation.SignCheck;
//import com.miniw.common.constant.SignConstant;
//import com.miniw.common.utils.RSAUtil;
//import com.miniw.common.utils.RequestParamsToMapUtil;
//import com.miniw.common.vo.ResultCode;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.lang3.StringUtils;
//import org.aspectj.lang.ProceedingJoinPoint;
//import org.aspectj.lang.annotation.Around;
//import org.aspectj.lang.annotation.Aspect;
//import org.aspectj.lang.annotation.Pointcut;
//import org.aspectj.lang.reflect.MethodSignature;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.core.annotation.Order;
//import org.springframework.stereotype.Component;
//import org.springframework.web.context.request.RequestAttributes;
//import org.springframework.web.context.request.RequestContextHolder;
//import org.springframework.web.context.request.ServletRequestAttributes;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.io.PrintWriter;
//import java.lang.reflect.Method;
//import java.util.Arrays;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.stream.Collectors;
//
///**
// * 切面：签名校验
// *
// * @author luoquan
// * @date 2021/07/29
// */
//@Slf4j
//@Aspect
//@Configuration
//@Order(0)
//public class SignCheckAdvice {
//
//    @Pointcut(value = "@annotation(com.miniw.common.annotation.SignCheck)")
//    private void signCheck(){};
//
//    @Around("signCheck()")
//    public Object permissionCheckFirst(ProceedingJoinPoint joinPoint) {
//        try {
//            Object result = null;
//            if(beforePoint(joinPoint)){
//                // 放行
//                result = joinPoint.proceed();
//            }
//            return result;
//        } catch (Throwable e) {
//            return null;
//        }
//    }
//
//    /**
//     * 切入之前
//     *
//     * @param joinPoint 连接点
//     * @return boolean
//     */
//    private boolean beforePoint(ProceedingJoinPoint joinPoint) throws Exception {
//        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
//        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) requestAttributes;
//        assert servletRequestAttributes != null;
//        HttpServletRequest request = servletRequestAttributes.getRequest();
//        HttpServletResponse response = servletRequestAttributes.getResponse();
//        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
//        Method method = signature.getMethod();
//        log.info(String.format("AOP:SignCheckAdvice Ready to enter method{%s}",method.getName()));
//
//        // 对SignCheck进行拦截校验
//        if(method.isAnnotationPresent(SignCheck.class)){
//            // 返回信息设置
//            assert response != null;
//            response.setHeader("Access-Control-Allow-Origin", request.getHeader("origin"));
//            response.setHeader("Access-Control-Allow-Credentials", "true");
//            response.setHeader("Access-Control-Allow-Methods", "POST,GET,OPTIONS,DELETE,PATCH,PUT");
//            response.setHeader("Access-Control-Max-Age", "3600");
//            response.setHeader("Access-Control-Allow-Headers",
//                    "Origin,X-Requested-With,x-requested-with,X-Custom-Header,"
//                            + "Content-Type,Accept,Authorization");
//            response.setCharacterEncoding("UTF-8");
//            response.setContentType("text/html;charset=utf-8");
//            PrintWriter out = response.getWriter();
//            JSONObject jsonObject = new JSONObject();
//
//            String sign = request.getHeader("Mini-DF-Signature");
//            // 未携带sign的请求进行拦截
//            if(StringUtils.isBlank(sign)){
//                jsonObject.put("code", ResultCode.SYS_BAN.getCode());
//                jsonObject.put("msg", ResultCode.SYS_BAN.getMsg());
//                jsonObject.put("success", false);
//                out.write(jsonObject.toString());
//                out.close();
//                response.flushBuffer();
//                return false;
//            }
//
//            SignCheck signCheck = method.getAnnotation(SignCheck.class);
//            if (signCheck.required()){
//                // 签名合法性校验
//                System.out.println(JSONUtil.parseObj(joinPoint.getArgs()[0]));
//                Map<String, Object> itemMap = JSONObject.toJavaObject(itemJSONObj, Map.class);
////                return RSAUtil.verify(str,
////                        RSAUtil.getPublicKey(SignConstant.UNICOM_PUBLIC_KEY_PEM),
////                        request.getHeader("Mini-DF-Signature"));
//            }
//        }
//        return true;
//    }
//
//}

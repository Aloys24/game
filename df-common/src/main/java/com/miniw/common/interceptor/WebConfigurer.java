package com.miniw.common.interceptor;

import com.miniw.common.utils.RedisUtil;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;

/**
 * 拦截器注册
 *
 * @author luoquan
 * @date 2021/08/03
 */
@Configuration
public class WebConfigurer implements WebMvcConfigurer {

    @Resource
    private RedisUtil redisUtil;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new UserInterceptor(redisUtil))
                .addPathPatterns("/v2/data_free/check/**")
                .addPathPatterns("/v2/data_free/generateDfOrder")

                .excludePathPatterns("/v2/data_free/callback/**")
                .excludePathPatterns("/v2/data_free/renew/callback/**")
                .excludePathPatterns("/v2/data_free/reissueMiniCoin")
                .excludePathPatterns("/static/**");
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
//        registry.addMapping("/**")
//                .allowCredentials(true)
//                .allowedMethods("POST", "GET", "PUT", "OPTIONS", "DELETE")
//                .allowedOrigins("*");
    }
}

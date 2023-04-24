package com.miniw.web;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 *
 * @author admin
 * @date 2021/08/02
 */
@SpringBootApplication
@EnableFeignClients(basePackages = "com.miniw.external.client")
@MapperScan(basePackages = "com.miniw.persistence.mapper")
@EnableAspectJAutoProxy
@EnableScheduling
@EnableAsync
@ComponentScan(basePackages = "com.miniw.*")
public class DfWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(DfWebApplication.class, args);
    }

}

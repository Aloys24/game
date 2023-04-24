package com.miniw.external.client.email.config;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


/**
 * 电子邮件参数配置
 *
 * @author luoquan
 * @date 2021/08/16
 */
@Component
@ConfigurationProperties("mini-email")
public class EmailParamsConfig {
    /**
     *
     */

    public static String SEND_URL;

    public void setSendUrl(String sendUrl) { EmailParamsConfig.SEND_URL = sendUrl; }

}

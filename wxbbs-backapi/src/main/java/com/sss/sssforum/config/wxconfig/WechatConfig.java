package com.sss.sssforum.config.wxconfig;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 微信公众号所需配置
 * @author lws
 * @date 2020-08-06 12:07
 **/
@Data
@ConfigurationProperties(prefix = "wechat")
@Component
public class WechatConfig {

    private String appId;

    private String appSecret;

    private String token;

    private String state;

    /**
     * 回调地址
     */
    private String redireUrl;

    /**
     * 跳转前端页面的地址
     */
    private String returnUrl;


}

package com.sss.sssforum.config.wxconfig;

import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.api.impl.WxMpServiceImpl;
import me.chanjar.weixin.mp.config.WxMpConfigStorage;
import me.chanjar.weixin.mp.config.impl.WxMpDefaultConfigImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * wxJava公众号 SDK 配置
 *
 * @author lws
 * @date 2020-08-06 14:09
 **/
@Configuration
public class WxBeanConfig {

    @Autowired
    private WechatConfig wechatConfig;
    @Bean
    public WxMpService wxMpService() {
        WxMpService wxMpService = new WxMpServiceImpl();
        wxMpService.setWxMpConfigStorage(WxMpConfigStorage());
        return wxMpService;
    }

    @Bean
    public WxMpConfigStorage WxMpConfigStorage() {
        WxMpDefaultConfigImpl wxMpDefaultConfig = new WxMpDefaultConfigImpl();
        wxMpDefaultConfig.setAppId(wechatConfig.getAppId());
        wxMpDefaultConfig.setSecret(wechatConfig.getAppSecret());
        wxMpDefaultConfig.setToken(wechatConfig.getToken());
        return wxMpDefaultConfig;
    }
}

package com.sss.sssforum.config.springconfig;

import com.baomidou.mybatisplus.core.injector.ISqlInjector;
import com.baomidou.mybatisplus.extension.injector.LogicSqlInjector;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author lws
 * @date 2020-07-28 16:10
 **/
@Configuration
public class WebmvcConfig implements WebMvcConfigurer {

    @Value("${file.config.windows-path}")
    private   String windowsFileExistPath;

    @Value("${file.config.linux-path}")
    private   String linuxFileExistPath;

    @Value("${file.config.release-path}")
    private   String releasePath;

    @Autowired
    private Environment environment;

    /**
     *
     * 逻辑删除必需配置 否则逻辑删除不生效
     *
    **/
    @Bean
    public ISqlInjector sqlInjector() {
        return new LogicSqlInjector();
    }


    /**
     *  https 请求访问图片配置
     *
     * @param registry
     * @return void
     * @author lws
     * @date 2020/8/17 15:05
    **/
//    @Override
//    public void addResourceHandlers(ResourceHandlerRegistry registry) {
//        String property = environment.getProperty("os.name");
//        if (StringUtils.isNotBlank(property) && property.equalsIgnoreCase("linux")) {
//            registry.addResourceHandler(releasePath,"/**").addResourceLocations(linuxFileExistPath,"classpath:/static/");
//        }else {
//            registry.addResourceHandler(releasePath,"/**").addResourceLocations(windowsFileExistPath,"classpath:/static/");
//        }
//
//
//    }
}

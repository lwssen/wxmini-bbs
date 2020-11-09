package com.sss.sssforum.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author lws
 * @date 2020-08-17 14:57
 **/
@ConfigurationProperties("file.config")
@Data
@Component
public class FileProperties {

    /**
     * 文件访问路径
     */
    private String requestPath;

    /**
     * 服务器IP地址
     */
    private String serverAddress;

    /**
     * 配置 https 访问文件的路径
     */
    private String releasePath;
    /**
     * 文件保存路径
     */
    private String windowsSavePath;

    private String linuxSavePath;
    /**
     * 限制文件上传的大小
     */
    private Long fileSize;
    /**
     * 支持文件上传的类型
     */
    private String fileSupportType;


}

package com.sss.sssforum.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author lws
 * @date 2020-09-12 14:24
 **/
@Component
@ConfigurationProperties(prefix = "oss")
@Data
public class AliyunOSSProperties {

    /**
     * oss上bucket的名称
     */
    private String bucketName;
    /**
     * 阿里对应的访问id
     */
    private String accessKeyId;
    /**
     * 阿里对应的密钥
     */
    private String accessKeySecret;

    /**
     * oss对应的区域节点
     */
    private String endpoint;

    /**
     * 文件存储路径
     */
    private String savePath;


    private String filePath;
    private String requestUrl;
}

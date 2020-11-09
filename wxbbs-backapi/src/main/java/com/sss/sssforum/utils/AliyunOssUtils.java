package com.sss.sssforum.utils;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.ObjectMetadata;
import com.aliyun.oss.model.PutObjectResult;
import com.sss.sssforum.config.properties.AliyunOSSProperties;
import com.sss.sssforum.enums.FileTypeEnum;
import com.sss.sssforum.wxclient.file.entity.FileInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lws
 * @date 2020-09-12 14:29
 **/
@Component
@Slf4j
public class AliyunOssUtils {
    @Autowired
    private AliyunOSSProperties properties;

    /**
     * 单个文件上传
     *
     * @param file 文件名称
     * @param storagePath 文件保存路径
     * @param fileTypeEnum 文件类型枚举
     * @return java.lang.String  {@link }
     * @author lws
    **/
    public FileInfo ossUploadFile(MultipartFile file, String storagePath, FileTypeEnum fileTypeEnum) {
        // 创建OSSClient实例
        String url=null;
        String fileName=null;
        //文件后缀名称
        String oldFilename = file.getOriginalFilename();
        String fileSuffix = oldFilename.substring(oldFilename.lastIndexOf("."));
        log.info("文件后缀名称："+fileSuffix);
        final OSS ossClient = new OSSClientBuilder().build(properties.getEndpoint(),
                properties.getAccessKeyId(), properties.getAccessKeySecret());
        try {

                // 创建一个唯一的文件名，类似于id，就是保存在OSS服务器上文件的文件名(自定义文件名)
                IdWorker idWorker = new IdWorker();
                long nextId = idWorker.nextId();
                long currentLocalDateTimeStamp = MyDateTimeUtils.getCurrentLocalDateTimeStamp();
                 fileName=String.valueOf(nextId)+currentLocalDateTimeStamp+fileSuffix;
                final InputStream inputStream = file.getInputStream();

                // 设置对象
                final ObjectMetadata objectMetadata = new ObjectMetadata();
                // 设置数据流里有多少个字节可以读取
                objectMetadata.setContentLength(inputStream.available());
                objectMetadata.setCacheControl("no-cache");
                objectMetadata.setHeader("Pragma", "no-cache");
                objectMetadata.setContentType(getcontentType(file));
                objectMetadata.setContentDisposition("inline;filename=" + fileName);
                fileName = storagePath + "/" + fileName;
                // 上传文件
                final PutObjectResult result = ossClient.putObject(properties.getBucketName(), fileName, inputStream, objectMetadata);
               //  url="https://"+properties.getRequestUrl()+"/"+fileName;
                 url="https://"+properties.getBucketName()+"."+properties.getEndpoint()+"/"+fileName;
                log.info("OSS返回的路径:{}", url);
                log.info("Aliyun OSS AliyunOSSUtil.uploadFileToAliyunOSS,result:{}", result);

        } catch ( IOException e) {
            log.error("Aliyun OSS AliyunOSSUtil.uploadFileToAliyunOSS fail,reason:{}", e);
        } finally{
            ossClient.shutdown();
        }
        FileInfo fileInfo = new FileInfo();
        fileInfo.setSavePath(storagePath+"/"+fileName).setRequestUrl(url).setFileName(fileName+fileSuffix);
        return fileInfo;
    }

    /**
     * 多个个文件上传
     *
     * @param files 文件名称数组
     * @param storagePath 文件保存路径
     * @param fileTypeEnum 文件类型枚举
     * @return java.lang.String  {@link }
     * @author lws
     **/
    public List<String> ossManyUploadFile(MultipartFile[] files, String storagePath, FileTypeEnum fileTypeEnum) {
        // 创建OSSClient实例
        final OSS ossClient = new OSSClientBuilder().build(properties.getEndpoint(),
                properties.getAccessKeyId(), properties.getAccessKeySecret());
        final List<String> fileUrl = new ArrayList<>();
        try {
            for (final MultipartFile file : files) {
                //文件后缀名称
                String oldFilename = file.getOriginalFilename();
                String fileSuffix = oldFilename.substring(oldFilename.lastIndexOf("."));
                log.info("文件后缀名称："+fileSuffix);
                // 创建一个唯一的文件名，类似于id，就是保存在OSS服务器上文件的文件名(自定义文件名)
                IdWorker idWorker = new IdWorker();
                long nextId = idWorker.nextId();
                long currentLocalDateTimeStamp = MyDateTimeUtils.getCurrentLocalDateTimeStamp();
                String fileName=String.valueOf(nextId)+currentLocalDateTimeStamp;
                final InputStream inputStream = file.getInputStream();
                // 设置对象
                final ObjectMetadata objectMetadata = new ObjectMetadata();
                // 设置数据流里有多少个字节可以读取
                objectMetadata.setContentLength(inputStream.available());
                objectMetadata.setCacheControl("no-cache");
                objectMetadata.setHeader("Pragma", "no-cache");
                objectMetadata.setContentType(getcontentType(file));
                objectMetadata.setContentDisposition("inline;filename=" + fileName);
                fileName = storagePath + "/" + fileName;
                log.info("文件后缀名称："+fileSuffix);
                // 上传文件
                final PutObjectResult result = ossClient.putObject(properties.getBucketName(), fileName, inputStream, objectMetadata);
                fileUrl.add("https://"+properties.getBucketName()+"."+properties.getEndpoint()+"/"+fileName+fileSuffix);
            }
        } catch ( IOException e) {
            log.error("Aliyun OSS AliyunOSSUtil.uploadFileToAliyunOSS fail,reason:{}", e);
        } finally{
            ossClient.shutdown();
        }
        return fileUrl;
    }

    /**
     * 删除文件
     */
    public void ossDeleteFile(String fileName) {
        final OSS ossClient = new OSSClientBuilder().build(properties.getEndpoint(),
                properties.getAccessKeyId(), properties.getAccessKeySecret());
        try {
            ossClient.deleteObject(properties.getBucketName(), fileName);
        } catch (final Exception e) {
            log.error("{}", e.fillInStackTrace());
        } finally {
            ossClient.shutdown();
        }
    }

    /**
     * 判断文件是否存在
     */
    public boolean doesObjectExist(String fileName) {
        final OSS ossClient = new OSSClientBuilder().build(properties.getEndpoint(),
                properties.getAccessKeyId(), properties.getAccessKeySecret());
        try {
            if (Strings.isEmpty(fileName)) {
                log.error("文件名不能为空");
                return false;
            } else {
                final boolean found = ossClient.doesObjectExist(properties.getBucketName(), fileName);
                return found;
            }
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
    }

    public static String getcontentType(MultipartFile file) {
        String oldFilename = file.getOriginalFilename();
        //文件后缀名称
        String FilenameExtension= oldFilename.substring(oldFilename.lastIndexOf("."));
        if (FilenameExtension.equalsIgnoreCase(".bmp")) {
            return "image/bmp";
        }
        if (FilenameExtension.equalsIgnoreCase(".gif")) {
            return "image/gif";
        }
        if (FilenameExtension.equalsIgnoreCase(".jpeg") ||
                FilenameExtension.equalsIgnoreCase(".jpg") ||
                FilenameExtension.equalsIgnoreCase(".png")) {
            return "image/jpg";
        }
        if (FilenameExtension.equalsIgnoreCase(".html")) {
            return "text/html";
        }
        if (FilenameExtension.equalsIgnoreCase(".txt")) {
            return "text/plain";
        }
        if (FilenameExtension.equalsIgnoreCase(".vsd")) {
            return "application/vnd.visio";
        }
        if (FilenameExtension.equalsIgnoreCase(".pptx") ||
                FilenameExtension.equalsIgnoreCase(".ppt")) {
            return "application/vnd.ms-powerpoint";
        }
        if (FilenameExtension.equalsIgnoreCase(".docx") ||
                FilenameExtension.equalsIgnoreCase(".doc")) {
            return "application/msword";
        }
        if (FilenameExtension.equalsIgnoreCase(".xml")) {
            return "text/xml";
        }
        return file.getContentType();
    }
}

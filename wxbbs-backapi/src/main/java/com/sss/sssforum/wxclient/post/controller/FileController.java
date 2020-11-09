package com.sss.sssforum.wxclient.post.controller;

import com.sss.sssforum.utils.MyDateTimeUtils;
import com.sss.sssforum.utils.MyFileUtils;
import com.sss.sssforum.utils.Result;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.Resource;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

/**
 * @author lws
 * @date 2020-10-10 11:36
 **/
@RestController
@RequestMapping("/file")
public class FileController {


    @PostMapping("/upload")
    public Result fileUpload(MultipartFile file, HttpServletRequest request){
        String filePath2="/images";
        StopWatch watch = new StopWatch();
        watch.start();
        String s = MyFileUtils.uploadFile(file, filePath2);
        watch.stop();
        long totalTimeMillis = watch.getTotalTimeMillis();
        System.err.println("上传文件共耗时 "+ totalTimeMillis+" 毫秒" );
        return Result.succes(totalTimeMillis);
    }

    @PostMapping("/upload2")
    public Result fileUpload2(MultipartFile file, HttpServletRequest request) throws IOException {
        String filePath2="F:\\images";
        StopWatch watch = new StopWatch();
        watch.start();
         String s = MyFileUtils.apacheUploadFile(file, filePath2);
        watch.stop();
        long totalTimeMillis = watch.getTotalTimeMillis();
        System.err.println("222上传文件共耗时 "+ totalTimeMillis+" 毫秒" );
        return Result.succes(s);
    }


    @GetMapping("/download")
    public void fileDownload(HttpServletResponse response) throws Exception {
        StopWatch watch = new StopWatch();
        watch.start();
        String filename="a001.png";
        String filePath="/images"+"/"+filename;
        MyFileUtils.apacheDownload(response,filePath);
        watch.stop();
        long totalTimeMillis = watch.getTotalTimeMillis();
        System.err.println("下载文件共耗时 "+ totalTimeMillis+" 毫秒" );
    }
}

package com.sss.sssforum.utils;

import com.power.common.util.UUIDUtil;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Positions;
import org.apache.catalina.connector.ClientAbortException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.env.Environment;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Properties;
import java.util.Random;

/**
 * @date: 2019-09-06 15:45
 **/
public class MyFileUtils {

    private static final Environment environment=new StandardEnvironment();

    public static void setDownloadResponse(HttpServletResponse response, String fileName) throws UnsupportedEncodingException {
        //对特殊符号进行处理
        fileName = URLEncoder.encode(fileName, "UTF-8")
                .replaceAll("\\+", "%20").replaceAll("%28", "\\(").replaceAll("%29", "\\)").replaceAll("%3B", ";").replaceAll("%40", "@").replaceAll("%23", "\\#").replaceAll("%26", "\\&").replaceAll("%2C", "\\,");
        //设置编码字符
        response.setCharacterEncoding("utf-8");
        //设置内容类型为下载类型
        response.setContentType("application/x-download");
        //设置 content-disposition 响应头控制浏览器以下载的形式打开文件、文件名称
        response.addHeader("Content-Disposition",
                "attachment;" +
                        "filename=" + fileName + ";" +
                        "filename* = UTF-8''" + fileName);
    }

    /**
     * 下载文件
     *
     * @param response HttpServletResponse
     * @param file     File型文件
     * @throws IOException 下载失败
     */
    public static void download(HttpServletResponse response, File file) throws IOException {
        setDownloadResponse(response, file.getName());
        try (InputStream in = new FileInputStream(file.getAbsolutePath());
             OutputStream out = response.getOutputStream()) {
            //文件大小
            response.setContentLength(in.available());
            byte[] b = new byte[9216];
            int len;
            while ((len = in.read(b)) != -1) {
                out.write(b, 0, len);
                out.flush();
            }
        } catch (ClientAbortException e) {
            e.printStackTrace();
        }
    }

    /**
     * Apache工具类下载文件
     *
     * @param response HttpServletResponse
     * @param filePath     文件路径
     * @throws IOException 下载失败
     */
    public static void apacheDownload(HttpServletResponse response, String filePath)  {
        FileInputStream inputStream = null;
        ServletOutputStream servletOutputStream = null;
        File file = new File(filePath);
        if(!file.exists()){
            throw new RuntimeException("文件不存在,下载失败");
        }
        try {
            String fileName = file.getName();
            inputStream=new FileInputStream(file);
            //设置内容类型为下载类型
            response.setContentType("application/x-download");
            response.addHeader("Cache-Control", "no-cache, no-store, must-revalidate");
            response.addHeader("charset", "utf-8");
            response.addHeader("Pragma", "no-cache");
            String encodeName = URLEncoder.encode(fileName, StandardCharsets.UTF_8.toString());
            response.setHeader("Content-Disposition",
                    "attachment; filename=\"" + encodeName + "\"; filename*=utf-8''" + encodeName);
            servletOutputStream = response.getOutputStream();
            IOUtils.copy(inputStream,servletOutputStream);
            response.flushBuffer();
            if (servletOutputStream != null) {
                servletOutputStream.close();
            }
            inputStream.close();
        } catch (  IOException  e ) {
            e.printStackTrace();
        }

    }


    /**
     * 文件上传
     *
     * @param file
     * @param savePath 文件存放位置
     * @return java.lang.String 上传文件后的文件名称
     * @author SSS
    **/
    public static  String uploadFile(MultipartFile file, String savePath) {
        String fileName = file.getOriginalFilename();
        String  suffixName = fileName.substring(fileName.lastIndexOf("."));
        String  newSavePath = "";
        //防止文件名字重复,重新命名文件
       // String newFileName = MyDateTimeUtils.getCurrentLocalDateTimeStamp() + suffixName;
        //文件名称
        String oldFilename = file.getOriginalFilename();
        //文件后缀名称
        String fileSuffix = oldFilename.substring(oldFilename.lastIndexOf("."));
        IdWorker idWorker = new IdWorker();
        long nextId = idWorker.nextId();
        long currentLocalDateTimeStamp = MyDateTimeUtils.getCurrentLocalDateTimeStamp();
        String newFileName=String.valueOf(nextId)+currentLocalDateTimeStamp+fileSuffix;
        String systemType = environment.getProperty("os.name");
        if (StringUtils.isNotBlank(systemType) && systemType.equalsIgnoreCase("linux")) {
            newSavePath=savePath +"/" +newFileName;
        }else {
            newSavePath=savePath +"\\" +newFileName;
        }
        File dest = new File(newSavePath);
        // 检测是否存在目录
        if (!dest.getParentFile().exists()) {
            dest.getParentFile().mkdirs();
        }
        try {
            //文件上传
            file.transferTo(dest);
        } catch (IOException e) {
            e.printStackTrace();
        }
         return newFileName;
    }


    /**
     * Apache工具类文件上传
     *
     * @param file 文件
     * @param savePath  文件保存的路径
     * @return java.lang.String
     * @author lws
     * @date 2020/8/17 14:36
    **/
    public static String apacheUploadFile(MultipartFile file,String savePath){
        String resultFileName=null;
        FileOutputStream outputStream =null;
     //   Environment environment=new StandardEnvironment();
        String newSavePath;
        try {
            //文件名称
            String oldFilename = file.getOriginalFilename();
            //文件后缀名称
            String fileSuffix = oldFilename.substring(oldFilename.lastIndexOf("."));
            IdWorker idWorker = new IdWorker();
            long nextId = idWorker.nextId();
            long currentLocalDateTimeStamp = MyDateTimeUtils.getCurrentLocalDateTimeStamp();
            String newFileName=String.valueOf(nextId)+currentLocalDateTimeStamp+fileSuffix;
            resultFileName=newFileName;
            String systemType = environment.getProperty("os.name");
            if (StringUtils.isNotBlank(systemType) && systemType.equalsIgnoreCase("linux")) {
                newSavePath=savePath +"/" +newFileName;
            }else {
                newSavePath=savePath +"\\" +newFileName;
            }
            File tempFilePath = new File(newSavePath);
            // 检测是否存在目录
            if (!tempFilePath.getParentFile().exists()) {
                tempFilePath.getParentFile().mkdirs();
            }
             outputStream = new FileOutputStream(tempFilePath);
            IOUtils.copy(file.getInputStream(),outputStream);
            //图片添加水印
            ThumbnailsUtils.addTextWatermark(newSavePath,newSavePath);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(outputStream);
        }
        return resultFileName;
    }
}

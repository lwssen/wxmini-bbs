package com.sss.sssforum.utils;

import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageEncoder;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Positions;
import org.apache.commons.lang3.StringUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;

import static jdk.nashorn.internal.runtime.regexp.joni.encoding.CharacterType.ALPHA;

/**
 * 图片处理工具类
 *
 * @author lws
 * @date 2020-11-06 15:34
 **/
public class ThumbnailsUtils {
    public static final String MARK_TEXT = "木木木";
    public static final String FONT_NAME = "微软雅黑";
    public static final int FONT_STYLE = Font.BOLD;
    public static final int FONT_SIZE = 40;
    public static final Color FONT_COLOR = Color.RED;
    public static final int X = 10;
    public static final int Y = 10;
    //透明度
    public static final float ALPHA = 0.3F;
    //水印图片
    public static final String LOGO = "bbb.png";

    /**
     *  图片添加水印
     *
     * @param artworkImgPath 需要添加水印的图片的绝对路径+图片名称
     * @param watermarkImgPath 水印图片的绝对路径+图片名称
     * @param newImgPath 添加水印后的新图片绝对路径+图片名称
     * @return void
     * @author lws
     * @date 2020/11/6 15:41
    **/
    public static void addWatermark(String artworkImgPath,String watermarkImgPath,String newImgPath){
        try {
            int width = ImageIO.read(new File(artworkImgPath)).getWidth();
            int height = ImageIO.read(new File(artworkImgPath)).getHeight();
            Thumbnails.of(artworkImgPath).size(width, height)
                     //watermark(位置，水印图，透明度)
                    .watermark(Positions.BOTTOM_RIGHT, ImageIO.read(new File(watermarkImgPath)), 0.5f)
                    .outputQuality(0.8f).toFile(newImgPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void addTextWatermark(String artworkImgPath,String newImgPath) throws IOException {
        OutputStream outputStream=null;
        try {
            BufferedImage image = ImageIO.read(new File(artworkImgPath));
            int width = image.getWidth();
            int height = image.getHeight();
            BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_BGR);
            Graphics2D graphics2D = bufferedImage.createGraphics();
            graphics2D.drawImage(image,0,0,width,height,null);
            graphics2D.setFont(new Font(FONT_NAME,FONT_STYLE,FONT_SIZE));
            graphics2D.setColor(FONT_COLOR);

            int width1 = FONT_SIZE * getTextLength(MARK_TEXT);
            int height1 = FONT_SIZE;

            int widthDiff= width - width1;
            int heightDiff= height - height1;

            int x=X;
            int y=Y;

            if (x > widthDiff) {
                x=widthDiff;
            }

            if (y >heightDiff) {
                y=height1;
            }

            graphics2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP,ALPHA));
            //设置文字水印位置
           // graphics2D.drawString(MARK_TEXT,width - 160,height - 20);
            //添加图片水印
//            File fileLogo = new File(LOGO);
//            BufferedImage logoImage = ImageIO.read(fileLogo);
//            graphics2D.drawImage(logoImage,width - 160,height - 20,null);
            graphics2D.dispose();

            outputStream=new FileOutputStream(newImgPath);
            JPEGImageEncoder jpegEncoder = JPEGCodec.createJPEGEncoder(outputStream);
            jpegEncoder.encode(bufferedImage);
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (outputStream!=null) {
                outputStream.close();
            }
        }
    }

    private static int getTextLength(String text){
        int length = text.length();
        for (int i = 0; i <text.length() ; i++) {
            String s = String.valueOf(text.charAt(i));
            if (s.getBytes().length> 1) {
                length++;
            }
        }
         length = length % 2 == 0 ? length / 2 : length / 2 + 1;
        return length;
    }
    /**
     * 图片大小剪切
     *
     * @param artworkImgPath 需要剪裁的图片的绝对路径+图片名称
     * @param cutWidth 剪切的宽度
     * @param cutHeight 剪切的高度
     * @param newWidth  剪切后的图片宽度
     * @param newHeight  剪切后的图片高度度
     * @param newImgPath 剪切后的图片存放绝对路径+图片名称
     * @return void
     * @author lws
     * @date 2020/11/6 15:50
    **/
    public static void cutImage(String artworkImgPath,Integer cutWidth,Integer cutHeight,
                                Integer newWidth,Integer newHeight,String newImgPath ){
        try {

            Thumbnails.of(artworkImgPath)
                    //图片中心400*400的区域
                    .sourceRegion(Positions.CENTER, cutWidth, cutHeight)
                    .size(newWidth, newHeight).keepAspectRatio(false)
                    .toFile(newImgPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     *  图片旋转
     *
     * @param artworkImgPath 需要旋转的图片的绝对路径+图片名称
     * @param rotate 旋转度数  正数：顺时针 负数：逆时针
     * @param newImgPath 旋转后的图片存放绝对路径加+图片名称
     * @return void
     * @author lws
     * @date 2020/11/6 15:57
    **/
    public static void rotationImage(String artworkImgPath, Integer rotate,String newImgPath ){
        try {

            int width = ImageIO.read(new File(artworkImgPath)).getWidth();
            int height = ImageIO.read(new File(artworkImgPath)).getHeight();
            Thumbnails.of(artworkImgPath).size(width, height)
                    .rotate(rotate)
                    .toFile(newImgPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     *  图片格式转换
     *
     * @param artworkImgPath 需要转换格式的图片的绝对路径+图片名称
     * @param newImgType 新的图片类型
     * @param newImgPath 转换格式后的图片存放绝对路径加+图片名称
     * @return void
     * @author lws
     * @date 2020/11/6 16:01
    **/
    public static void converterImage(String artworkImgPath, String newImgType,String newImgPath ){
        try {

            int width = ImageIO.read(new File(artworkImgPath)).getWidth();
            int height = ImageIO.read(new File(artworkImgPath)).getHeight();
            Thumbnails.of(artworkImgPath).size(width, height)
                    .outputFormat(newImgType)
                    .toFile(newImgPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 图片大小缩放
     *
     * @param artworkImgPath 需要缩放的图片的绝对路径+图片名称
     * @param width  缩略宽度
     * @param height 缩略高度
     * @param newImgPath 缩放后的图片存放绝对路径加+图片名称
     * @return void
     * @author lws
     * @date 2020/11/6 16:05
    **/
    public static void sizeScaleImage(String artworkImgPath, Integer width,Integer height,String newImgPath ){
        try {
            Thumbnails.of(artworkImgPath)
                    //不按照比例，指定大小进行缩放
                    .size(width, height).keepAspectRatio(false)
                    .toFile(newImgPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     *  图片按比例缩放
     *
     * @param artworkImgPath 需要缩放的图片的绝对路径+图片名称
     * @param scaling 缩方比例
     * @param newImgPath  缩放后的图片存放绝对路径加+图片名称
     * @return void
     * @author lws
     * @date 2020/11/6 16:09
    **/
    public static void scalingImage(String artworkImgPath, float scaling,String newImgPath ){
        try {
            Thumbnails.of(artworkImgPath)
                    //按照比例进行缩放
                    .scale(scaling)
                    .toFile(newImgPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据指定大小和指定精度压缩图片
     *
     * @param srcPath 源图片地址
     * @param desPath 目标图片地址
     * @param desFileSize 指定压缩后的图片大小，单位kb
     * @param accuracy 精度，递归压缩的比率，建议小于0.9
     * @return
     */
    public static void commpressPicForScale(String srcPath, String desPath, long desFileSize, double accuracy) {
        if (StringUtils.isEmpty(srcPath) || StringUtils.isEmpty(srcPath)) {
            System.err.println("图片路径为空，压缩失败");
            return ;
        }
        if (!new File(srcPath).exists()) {
            System.err.println("图片不存在，压缩失败");
            return ;
        }
        try {
            File srcFile = new File(srcPath);
            long srcFileSize = srcFile.length();
            System.out.println("源图片：" + srcPath + "，大小：" + srcFileSize / 1024
                    + "kb");

            // 1、先转换成jpg
            Thumbnails.of(srcPath).scale(1f).toFile(desPath);
            // 递归压缩，直到目标文件大小小于desFileSize
            commpressPicCycle(desPath, desFileSize, accuracy);

            File desFile = new File(desPath);
            System.out.println("目标图片：" + desPath + "，大小" + desFile.length()
                    / 1024 + "kb");
            System.out.println("图片压缩完成！");
        } catch (Exception e) {
            e.printStackTrace();
           // return null;
        }
     //   return desPath;
    }

    private static void commpressPicCycle(String desPath, long desFileSize, double accuracy) throws IOException {
            File srcFileJPG = new File(desPath);
            long srcFileSizeJPG = srcFileJPG.length();
            // 2、判断大小，如果小于500kb，不压缩；如果大于等于500kb，压缩
            if (srcFileSizeJPG <= desFileSize * 1024) {
                return;
            }
            // 计算宽高
            BufferedImage bim = ImageIO.read(srcFileJPG);
            int srcWdith = bim.getWidth();
            int srcHeigth = bim.getHeight();
            int desWidth = new BigDecimal(srcWdith).multiply(
                    new BigDecimal(accuracy)).intValue();
            int desHeight = new BigDecimal(srcHeigth).multiply(
                    new BigDecimal(accuracy)).intValue();

            Thumbnails.of(desPath).size(desWidth, desHeight)
                    .outputQuality(accuracy).toFile(desPath);
            commpressPicCycle(desPath, desFileSize, accuracy);
        }
    }

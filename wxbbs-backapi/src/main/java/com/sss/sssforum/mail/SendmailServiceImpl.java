package com.sss.sssforum.mail;

import com.sss.sssforum.mail.ISendmailService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author lws
 * @date 2020-05-22 17:00
 **/
@Service
@Slf4j
public class SendmailServiceImpl implements ISendmailService {

    @Resource
    private JavaMailSenderImpl javaMailSender;

    /**
     * 发送简单邮件
     *
     * @param subject 邮件主题
     * @param text 邮件内容
     * @param receiverMail 接收者邮箱
     * @param senderMail 发送者邮箱
     * @return void
     * @author lws
     * @date 2020/5/22 16:48
     **/
    @Override
    public void sendSimpleMail(String subject, String text, String receiverMail, String senderMail) {

        StopWatch watch = StopWatch.createStarted();
        SimpleMailMessage message = new SimpleMailMessage();
        //邮件设置
        message.setSubject(subject);
        message.setText(text);
        message.setTo(receiverMail);
        message.setFrom(senderMail);
        javaMailSender.send(message);
        watch.stop();
        long time = watch.getTime(TimeUnit.MILLISECONDS);
        log.info("简单邮件发送成功,耗时 {} 毫秒",time);
    }


    /**
     * 发送复杂邮件
     *
     * @param subject 邮件主题
     * @param htmlText HTML代码邮件内容
     * @param receiverMail 接收者邮箱
     * @param senderMail 发送者邮箱
     * @param accessors  附件集合 Key=文件名称  value=文件路径
     * @return void
     * @author lws
     * @date 2020/5/22 16:59
     **/
    @Override
    public void sendComplexMail(String subject, String htmlText, String receiverMail, String senderMail, Map<String,String> accessors) throws MessagingException {
        StopWatch watch = StopWatch.createStarted();
        //1、创建一个复杂的邮件
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
        helper.setSubject(subject);
        helper.setText(htmlText,true);
        helper.setTo(receiverMail);
        helper.setFrom(senderMail);
//        //文本中添加图片
//        helper.addInline("image1",new FileSystemResource("E:\\mail\\mailTest.png"));
//        //附件添加图片
//        helper.addAttachment("mailTest.jpg",new File("E:\\mail\\mailTest.png"));
//        //附件添加word文档
//        helper.addAttachment("Git常用命令.docx",new File("E:\\mail\\Git常用命令.docx"));
        accessors.forEach((k,v)->{
            try {
                helper.addAttachment(k,new File(v));
            } catch (MessagingException e) {
                e.printStackTrace();
                log.info("邮件发送失败原因:"+e.getMessage());
            }
        });
        javaMailSender.send(mimeMessage);

        watch.stop();
        long time = watch.getTime(TimeUnit.MILLISECONDS);
        log.info("简单邮件发送成功,耗时 {} 毫秒",time);

    }
}

package com.sss.sssforum.mail;


import javax.mail.MessagingException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 *  邮件发送服务类
 * @author lws
 * @date 2020-05-22 16:45
 **/
public interface ISendmailService {


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
    void sendSimpleMail(String subject,String text,String receiverMail,String senderMail);


    /**
     * 发送复杂邮件
     *
     * @param subject 邮件主题
     * @param htmlText HTML代码邮件内容
     * @param receiverMail 接收者邮箱
     * @param senderMail 发送者邮箱
     * @param accessors  附件集合
     * @return void
     * @author lws
     * @date 2020/5/22 16:59
    **/
    void sendComplexMail(String subject, String htmlText,
                         String receiverMail, String senderMail, Map<String,String> accessors) throws MessagingException;
}

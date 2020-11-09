package com.sss.sssforum.config.springconfig;

import com.sss.sssforum.mail.ISendmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * 异步任务处理
 *
 * @author lws
 * @date 2020-05-16 10:28
 **/
@Service
@Slf4j
public class AsyncService {

    @Autowired
    private  ISendmailService sendmailService;
    /**
     * 无返回值异步任务
     *
     * @return void
     * @author lws
    **/
    @Async("getAsyncExecutor")
    public void asyncSendMail(String s) {

        sendmailService.sendSimpleMail("新的评论发布",s,"986298329@qq.com","liuweisenss@163.com");
        log.info("异步发送邮件成功****"+Thread.currentThread().getName()+s);
    }


    /**
     * 有返回值异步任务
     *
     * @return void
     * @author lws
     **/
    @Async("getAsyncExecutor")
    public Future<Integer> asyncProcessHasReturn(String s) throws InterruptedException {
        log.info("有返回值异步任务02****"+Thread.currentThread().getName()+s);
        TimeUnit.SECONDS.sleep(3L);
        return new AsyncResult<>(1024);
    }
}

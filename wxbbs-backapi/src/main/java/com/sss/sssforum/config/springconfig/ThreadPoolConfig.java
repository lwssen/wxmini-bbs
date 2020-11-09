package com.sss.sssforum.config.springconfig;

import org.springframework.stereotype.Component;

import java.util.concurrent.*;

/**
 * @author lws
 * @date 2020-09-15 22:12
 **/
@Component
public class ThreadPoolConfig {

    public Executor getExecutor(){
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(2, 5, 3L, TimeUnit.MINUTES,
                new LinkedBlockingQueue<>(10), Executors.defaultThreadFactory(),new ThreadPoolExecutor.AbortPolicy());
        return threadPoolExecutor;
    }

}

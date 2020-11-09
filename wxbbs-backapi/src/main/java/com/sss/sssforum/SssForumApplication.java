package com.sss.sssforum;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = "com.sss.sssforum")
@MapperScan("com.sss.sssforum.**.dao")
@EnableScheduling
public class SssForumApplication {

    public static void main(String[] args) {
        SpringApplication.run(SssForumApplication.class, args);
    }

}

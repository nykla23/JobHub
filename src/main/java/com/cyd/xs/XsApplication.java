package com.cyd.xs;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.cyd.xs.mapper")
public class XsApplication {

    public static void main(String[] args) {
        SpringApplication.run(XsApplication.class, args);
    }

}

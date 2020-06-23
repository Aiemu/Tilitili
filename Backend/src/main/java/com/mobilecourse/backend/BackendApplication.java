package com.mobilecourse.backend;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@MapperScan(basePackages = "com.mobilecourse.backend.dao")
@EnableTransactionManagement
public class BackendApplication extends SpringBootServletInitializer {

    // 程序入口
    public static void main(String[] args) {
        ConfigurableApplicationContext applicationContext = SpringApplication.run(BackendApplication.class, args);
        WebSocketServer.setApplicationContext(applicationContext);
    }

    @Override//为了打包springboot项目
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(this.getClass());
    }

}

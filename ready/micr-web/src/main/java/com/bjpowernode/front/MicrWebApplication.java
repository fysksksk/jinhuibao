package com.bjpowernode.front;

import com.bjpowernode.common.util.JwtUtils;
import com.github.xiaoymin.swaggerbootstrapui.annotations.EnableSwaggerBootstrapUI;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

// 启动swagger 和 ui
@EnableSwaggerBootstrapUI
@EnableSwagger2
// 启动Dubbo服务
@EnableDubbo
@SpringBootApplication
public class MicrWebApplication {

    @Value("${jwt.secret}")
    private String secretKey;

    // 创建JwtUtil
    @Bean
    public JwtUtils jwtUtils() {
        JwtUtils jwtUtils = new JwtUtils(secretKey);
        return jwtUtils;
    }

    public static void main(String[] args) {
        SpringApplication.run(MicrWebApplication.class, args);
    }
}

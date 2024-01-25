package com.sorcery.coupon;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 模版微服务的启动入口
 *
 * @author jinglv
 * @date 2024/1/4 16:33
 */
@EnableScheduling
@EnableJpaAuditing
@EnableEurekaClient
@SpringBootApplication
public class CouponTemplateApplication {
    public static void main(String[] args) {
        SpringApplication.run(CouponTemplateApplication.class, args);
    }
}
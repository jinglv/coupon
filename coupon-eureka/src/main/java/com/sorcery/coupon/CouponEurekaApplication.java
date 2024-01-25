package com.sorcery.coupon;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * Eureka启动入口
 *
 * @author jinglv
 * @date 2023/12/29 09:39
 */
@EnableEurekaServer
@SpringBootApplication
public class CouponEurekaApplication {
    public static void main(String[] args) {
        SpringApplication.run(CouponEurekaApplication.class, args);
    }
}
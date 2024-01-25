package com.sorcery.coupon;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

/**
 * 优惠券结算微服务的启动入口
 *
 * @author jinglv
 * @date 2024/1/17 09:40
 */
@EnableEurekaClient
@SpringBootApplication
public class CouponSettlementApplication {
    public static void main(String[] args) {
        SpringApplication.run(CouponSettlementApplication.class, args);
    }
}
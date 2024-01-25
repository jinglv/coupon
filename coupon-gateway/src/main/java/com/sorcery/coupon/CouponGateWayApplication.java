package com.sorcery.coupon;

import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;

/**
 * 网关应用启动入口
 * 1.EnableZuulProxy 标识当前的应用是Zuul Server
 * 2.SpringCloudApplication 包含SpringBootApplication + 服务发现注解 + 熔断注解等组合注解
 *
 * @author jinglv
 * @date 2023/12/29 15:16
 */
@EnableZuulProxy
@SpringCloudApplication
public class CouponGateWayApplication {
    public static void main(String[] args) {
        SpringApplication.run(CouponGateWayApplication.class, args);
    }
}
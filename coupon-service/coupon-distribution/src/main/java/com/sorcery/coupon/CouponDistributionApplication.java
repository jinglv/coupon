package com.sorcery.coupon;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.web.client.RestTemplate;

/**
 * 分发系统的启动入口
 * EnableFeignClients -- 启用Feign功能
 * EnableCircuitBreaker -- 启用熔断降级功能
 * EnableJpaAuditing -- 启用JPA审计功能
 *
 * @author jinglv
 * @date 2024/1/10 09:14
 */
@EnableJpaAuditing
@EnableFeignClients
@EnableCircuitBreaker
@EnableEurekaClient
@SpringBootApplication
public class CouponDistributionApplication {

    /**
     * Ribbon服务发现，动态平衡
     * restTemplate调用其他服务功能
     *
     * @return RestTemplate
     */
    @Bean
    @LoadBalanced
    RestTemplate restTemplate() {
        return new RestTemplate();
    }

    public static void main(String[] args) {
        SpringApplication.run(CouponDistributionApplication.class, args);
    }
}
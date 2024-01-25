package com.sorcery.coupon.controller;

import com.sorcery.coupon.exception.CouponException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.serviceregistry.Registration;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 健康检查接口
 *
 * @author jinglv
 * @date 2024/1/9 10:25
 */
@Slf4j
@RequiredArgsConstructor
@RestController
public class HealthCheck {
    /**
     * 服务发现客户端
     */
    private final DiscoveryClient discoveryClient;
    /**
     * 服务注册接口，提供了获取服务id的方法
     */
    private final Registration registration;

    /**
     * 健康检查接口
     * 127.0.0.1：7001/coupon-template/health
     *
     * @return str
     */
    @GetMapping("/health")
    public String health() {
        log.debug("view health api");
        return "CouponTemplate Is OK!";
    }

    /**
     * 异常测试接口
     * 127.0.0.1：7001/coupon-template/exception
     *
     * @return str
     * @throws CouponException 自定义异常
     */
    @GetMapping("/exception")
    public String exception() throws CouponException {
        log.debug("view exception api");
        throw new CouponException("CouponTemplate Has Some Problem");
    }

    /**
     * 获取Eureka Server上的微服务元信息
     * 127.0.0.1：7001/coupon-template/info
     *
     * @return list
     */
    @GetMapping("/info")
    public List<Map<String, Object>> info() {
        // 大约需要等待两分钟才能获取到注册信息
        List<ServiceInstance> instances = discoveryClient.getInstances(registration.getServiceId());
        List<Map<String, Object>> result = new ArrayList<>(instances.size());
        instances.forEach(i -> {
            Map<String, Object> info = new HashMap<>();
            info.put("serviceId", i.getServiceId());
            info.put("instanceId", i.getInstanceId());
            info.put("port", i.getPort());
            result.add(info);
        });
        return result;
    }
}

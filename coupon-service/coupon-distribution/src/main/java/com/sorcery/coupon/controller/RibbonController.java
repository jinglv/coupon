package com.sorcery.coupon.controller;

import com.sorcery.coupon.annotation.IgnoreResponseAdvice;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

/**
 * Ribbon应用Controller
 *
 * @author jinglv
 * @date 2024/1/16 10:03
 */
@Slf4j
@RequiredArgsConstructor
@RestController
public class RibbonController {
    /**
     * rest客户端
     */
    private final RestTemplate restTemplate;

    /**
     * 通过Ribbon组件调用模板微服务
     *
     * @return TemplateInfo
     */
    @IgnoreResponseAdvice
    @GetMapping("/info")
    public TemplateInfo getTemplateInfo() {
        String infoUrl = "http://eureka-client-coupon-template" + "/coupon-template/info";
        return restTemplate.getForEntity(infoUrl, TemplateInfo.class).getBody();
    }

    /**
     * 模版服务的原信息
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    private static class TemplateInfo {
        private Integer code;
        private String message;
        private List<Map<String, Object>> data;
    }
}

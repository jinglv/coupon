package com.sorcery.coupon.feign;

import com.sorcery.coupon.feign.hystrix.CouponTemplateClientHystrix;
import com.sorcery.coupon.vo.CommonResponse;
import com.sorcery.coupon.vo.CouponTemplateSDK;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 优惠券模板微服务Feign接口定义
 * FeignClient的value是微服务的应用名application.yml中的spring.application.name
 *
 * @author jinglv
 * @date 2024/1/12 10:37
 */
@FeignClient(value = "eureka-client-coupon-distribution", fallback = CouponTemplateClientHystrix.class)
public interface CouponTemplateClient {
    /**
     * 查找所有可用的优惠券模版
     *
     * @return CouponTemplateSDK的接口响应
     */
    @RequestMapping(value = "/sorcery/template/sdk/all", method = RequestMethod.GET)
    CommonResponse<List<CouponTemplateSDK>> findAllUsableCouponTemplate();

    /**
     * 获取模版ids到CouponTemplateSDK的映射
     *
     * @param ids 优惠券模版ids
     * @return 接口响应
     */
    @RequestMapping(value = "/sorcery/template/sdk/infos/{ids}", method = RequestMethod.GET)
    CommonResponse<Map<Integer, CouponTemplateSDK>> findIdsToTemplateSDK(@RequestParam("ids") Collection<Integer> ids);
}

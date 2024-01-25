package com.sorcery.coupon.feign.hystrix;

import com.sorcery.coupon.feign.CouponTemplateClient;
import com.sorcery.coupon.vo.CommonResponse;
import com.sorcery.coupon.vo.CouponTemplateSDK;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 优惠券模板Feign接口的熔断降级策略
 *
 * @author jinglv
 * @date 2024/1/12 10:59
 */
@Slf4j
@Component
public class CouponTemplateClientHystrix implements CouponTemplateClient {
    /**
     * 查找所有可用的优惠券模版
     *
     * @return CouponTemplateSDK的接口响应
     */
    @Override
    public CommonResponse<List<CouponTemplateSDK>> findAllUsableCouponTemplate() {
        log.error("[eureka-client-coupon-template] findAllUsableTemplate");
        return new CommonResponse<>(-1, "[eureka-client-coupon-template] request error", Collections.emptyList());
    }

    /**
     * 获取模版ids到CouponTemplateSDK的映射
     *
     * @param ids 优惠券模版ids
     * @return 接口响应
     */
    @Override
    public CommonResponse<Map<Integer, CouponTemplateSDK>> findIdsToTemplateSDK(Collection<Integer> ids) {
        log.error("[eureka-client-coupon-template] findIdsToTemplateSDK");
        return new CommonResponse<>(-1, "[eureka-client-coupon-template] request error", new HashMap<>());
    }
}

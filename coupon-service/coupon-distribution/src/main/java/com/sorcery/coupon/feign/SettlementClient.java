package com.sorcery.coupon.feign;

import com.sorcery.coupon.exception.CouponException;
import com.sorcery.coupon.feign.hystrix.SettlementClientHystrix;
import com.sorcery.coupon.vo.CommonResponse;
import com.sorcery.coupon.vo.SettlementInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * 优惠券结算微服务Feign接口定义
 *
 * @author jinglv
 * @date 2024/1/12 10:48
 */
@FeignClient(value = "eureka-client-coupon-settlement", fallback = SettlementClientHystrix.class)
public interface SettlementClient {
    /**
     * 优惠券规则计算
     *
     * @param settlementInfo {@link SettlementInfo}
     * @return 接口响应
     */
    @RequestMapping(value = "/settlement/compute", method = RequestMethod.POST)
    CommonResponse<SettlementInfo> computeRule(@RequestBody SettlementInfo settlementInfo) throws CouponException;
}

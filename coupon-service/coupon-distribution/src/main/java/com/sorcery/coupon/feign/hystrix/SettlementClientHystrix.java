package com.sorcery.coupon.feign.hystrix;

import com.sorcery.coupon.exception.CouponException;
import com.sorcery.coupon.feign.SettlementClient;
import com.sorcery.coupon.vo.CommonResponse;
import com.sorcery.coupon.vo.SettlementInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 结算微服务熔断策略实现
 *
 * @author jinglv
 * @date 2024/1/12 11:06
 */
@Slf4j
@Component
public class SettlementClientHystrix implements SettlementClient {
    /**
     * 优惠券规则计算
     *
     * @param settlementInfo {@link SettlementInfo}
     * @return 接口响应
     */
    @Override
    public CommonResponse<SettlementInfo> computeRule(SettlementInfo settlementInfo) throws CouponException {
        log.error("[eureka-client-coupon-settlement] computeRule request error");
        // 设置结算微服务不可用的数值
        settlementInfo.setEmploy(false);
        settlementInfo.setCost(-1.0);
        return new CommonResponse<>(-1, "[eureka-client-coupon-settlement] request error", settlementInfo);
    }
}

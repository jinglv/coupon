package com.sorcery.coupon.controller;

import com.alibaba.fastjson.JSON;
import com.sorcery.coupon.exception.CouponException;
import com.sorcery.coupon.executor.ExecuteManager;
import com.sorcery.coupon.vo.SettlementInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 结算服务Controller
 *
 * @author jinglv
 * @date 2024/1/19 09:59
 */
@Slf4j
@RequiredArgsConstructor
@Controller
public class SettlementController {
    /**
     * 结算规则执行管理器
     */
    private final ExecuteManager executeManager;

    /**
     * 优惠券结算
     *
     * @param settlementInfo {@link SettlementInfo}
     * @return {@link SettlementInfo}
     * @throws CouponException 业务异常
     */
    @PostMapping("/settlement/compute")
    public SettlementInfo computeRule(@RequestBody SettlementInfo settlementInfo) throws CouponException {
        log.info("settlement: {}", JSON.toJSONString(settlementInfo));
        return executeManager.computeRule(settlementInfo);
    }
}

package com.sorcery.coupon.executor.impl;

import com.sorcery.coupon.constant.RuleFlag;
import com.sorcery.coupon.executor.AbstractExecutor;
import com.sorcery.coupon.executor.RuleExecutor;
import com.sorcery.coupon.vo.CouponTemplateSDK;
import com.sorcery.coupon.vo.SettlementInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;

/**
 * 满减优惠券结算规则执行器
 *
 * @author jinglv
 * @date 2024/1/17 10:38
 */
@Slf4j
@Component
public class FullExecutorImpl extends AbstractExecutor implements RuleExecutor {
    /**
     * 规则类型标记
     *
     * @return {@link RuleFlag}
     */
    @Override
    public RuleFlag ruleConfig() {
        return RuleFlag.FULL;
    }

    /**
     * 优惠券规则的计算
     *
     * @param settlementInfo {@link  SettlementInfo} 包含了选择的优惠券
     * @return {@link SettlementInfo} 修正过的结算信息
     */
    @Override
    public SettlementInfo computeRule(SettlementInfo settlementInfo) {
        double goodsSum = retainToDecimals(goodsCostSum(settlementInfo.getGoodsInfoList()));
        SettlementInfo probability = processGoodsTypeNotSatisfy(settlementInfo, goodsSum);
        if (null != probability) {
            log.debug("Full Template Is Not Match To GoodsType!");
            return probability;
        }
        // 判断满减是否符合折扣标准
        CouponTemplateSDK templateSDK = settlementInfo.getCouponAndTemplateInfoList().get(0).getTemplate();
        double base = (double) templateSDK.getRule().getDiscount().getBase();
        double quota = (double) templateSDK.getRule().getDiscount().getQuota();
        // 如果不符合标准，则直接返回商品总价
        if (goodsSum < base) {
            log.debug("Current Goods Cost Sum < Full Coupon Base!");
            settlementInfo.setCost(goodsSum);
            settlementInfo.setCouponAndTemplateInfoList(Collections.emptyList());
            return settlementInfo;
        }
        // 如果符合标准，计算使用优惠券之后的价格 - 结算
        settlementInfo.setCost(retainToDecimals((goodsSum - quota) > minCost() ? (goodsSum - quota) : minCost()));
        log.debug("Use Full Coupon Make Goods Const From {} To {}", goodsSum, settlementInfo.getCost());
        return settlementInfo;
    }
}

package com.sorcery.coupon.executor.impl;

import com.sorcery.coupon.constant.RuleFlag;
import com.sorcery.coupon.executor.AbstractExecutor;
import com.sorcery.coupon.executor.RuleExecutor;
import com.sorcery.coupon.vo.CouponTemplateSDK;
import com.sorcery.coupon.vo.SettlementInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 折扣优惠卷结算规则执行器
 *
 * @author jinglv
 * @date 2024/1/18 09:28
 */
@Slf4j
@Component
public class DiscountExecutorImpl extends AbstractExecutor implements RuleExecutor {
    /**
     * 规则类型标记
     *
     * @return {@link RuleFlag}
     */
    @Override
    public RuleFlag ruleConfig() {
        return RuleFlag.DISCOUNT;
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
            log.debug("Discount Template Is Not Match To GoodsType!");
            return probability;
        }
        // 折扣优惠券可以直接使用，没有门槛
        CouponTemplateSDK templateSDK = settlementInfo.getCouponAndTemplateInfoList().get(0).getTemplate();
        double quota = (double) templateSDK.getRule().getDiscount().getQuota();
        // 计算使用优惠券之后的价格
        settlementInfo.setCost(retainToDecimals((goodsSum * (quota * 1.0 / 100))) > minCost() ? retainToDecimals((goodsSum * (quota * 1.0 / 100))) : minCost());
        log.debug("Use Discount Coupon Make Goods Cost From {} To {}", goodsSum, settlementInfo.getCost());
        return settlementInfo;
    }
}

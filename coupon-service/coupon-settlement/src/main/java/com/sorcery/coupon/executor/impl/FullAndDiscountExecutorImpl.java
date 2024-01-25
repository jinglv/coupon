package com.sorcery.coupon.executor.impl;

import com.alibaba.fastjson.JSON;
import com.sorcery.coupon.constant.CouponCategory;
import com.sorcery.coupon.constant.RuleFlag;
import com.sorcery.coupon.executor.AbstractExecutor;
import com.sorcery.coupon.executor.RuleExecutor;
import com.sorcery.coupon.vo.GoodsInfo;
import com.sorcery.coupon.vo.SettlementInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 满减 + 折扣优惠券结算规则执行器
 *
 * @author jinglv
 * @date 2024/1/18 09:47
 */
@Slf4j
@Component
public class FullAndDiscountExecutorImpl extends AbstractExecutor implements RuleExecutor {
    /**
     * 规则类型标记
     *
     * @return {@link RuleFlag}
     */
    @Override
    public RuleFlag ruleConfig() {
        return RuleFlag.FULL_DISCOUNT;
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
        // 商品类型的校验
        SettlementInfo probability = processGoodsTypeNotSatisfy(settlementInfo, goodsSum);
        if (null != probability) {
            log.debug("Full And Discount Template Is Not Match To GoodsType!");
            return probability;
        }
        SettlementInfo.CouponAndTemplateInfo full = null;
        SettlementInfo.CouponAndTemplateInfo discount = null;
        for (SettlementInfo.CouponAndTemplateInfo ct : settlementInfo.getCouponAndTemplateInfoList()) {
            if (CouponCategory.of(ct.getTemplate().getCategory()) == CouponCategory.FULL) {
                full = ct;
            } else {
                discount = ct;
            }
        }
        assert null != full;
        assert null != discount;
        // 当前的折扣优惠券和满减券如果不能共用（一起使用），清空优惠券，返货商品原价
        if (!isTemplateCanShared(full, discount)) {
            log.debug("Current Full And Discount Can Not Shared!");
            settlementInfo.setCost(goodsSum);
            settlementInfo.setCouponAndTemplateInfoList(Collections.emptyList());
            return settlementInfo;
        }
        List<SettlementInfo.CouponAndTemplateInfo> ctInfoList = new ArrayList<>();
        double fullBase = (double) full.getTemplate().getRule().getDiscount().getBase();
        double fullQuota = (double) full.getTemplate().getRule().getDiscount().getQuota();
        // 最终价格
        double targetSum = goodsSum;
        // 先计算满减
        if (targetSum >= fullBase) {
            targetSum -= fullQuota;
            ctInfoList.add(full);
        }
        // 再计算折扣
        double discountQuota = (double) discount.getTemplate().getRule().getDiscount().getQuota();
        targetSum *= (discountQuota * 1.0) / 100;
        ctInfoList.add(discount);

        settlementInfo.setCouponAndTemplateInfoList(ctInfoList);
        settlementInfo.setCost(retainToDecimals(targetSum > minCost() ? targetSum : minCost()));
        log.debug("Use Full And Discount Coupon Make Goods Cost From {} To {}", goodsSum, settlementInfo.getCost());
        return settlementInfo;
    }

    /**
     * 校验商品类型与优惠券是否匹配
     * 需要注意：
     * 1.这里实现的满减 + 折扣优惠券的校验，多品类优惠券重载此方法
     * 2.如果想要使用多累优惠券，则必须要所有的商品类型都包含在内，即差集为空
     *
     * @param settlementInfo {@link SettlementInfo} 用户传递的结算信息
     * @return boolean
     */
    @Override
    protected boolean isGoodsTypeSatisfy(SettlementInfo settlementInfo) {
        log.debug("Check Full And Discount Is Match Or Not！");
        List<Integer> goodsTypes = settlementInfo.getGoodsInfoList().stream().map(GoodsInfo::getType).collect(Collectors.toList());
        List<Integer> templateGoodsTypes = new ArrayList<>();
        settlementInfo.getCouponAndTemplateInfoList().forEach(ct -> {
            templateGoodsTypes.addAll(JSON.parseObject(ct.getTemplate().getRule().getUsage().getGoodsType(), List.class));
        });
        // 如果想要使用多累优惠券，则必须要所有的商品类型都包含在内，即差集为空
        return CollectionUtils.isNotEmpty(CollectionUtils.subtract(goodsTypes, templateGoodsTypes));
    }

    /**
     * 校验当前两张优惠券是否可以共用
     * 即校验TemplateRule中weight是否满足条件
     *
     * @param full     满减优惠券
     * @param discount 折扣优惠券
     * @return bool
     */
    @SuppressWarnings("all")
    private boolean isTemplateCanShared(SettlementInfo.CouponAndTemplateInfo full, SettlementInfo.CouponAndTemplateInfo discount) {
        String fullKey = full.getTemplate().getKey() + String.format("%04d", full.getTemplate().getId());
        String discountKey = discount.getTemplate().getKey() + String.format("%04d", discount.getTemplate().getId());
        List<String> allSharedKeysForFull = new ArrayList<>();
        allSharedKeysForFull.add(fullKey);
        allSharedKeysForFull.addAll(JSON.parseObject(full.getTemplate().getRule().getWeight(), List.class));

        List<String> allSharedKeysForDiscount = new ArrayList<>();
        allSharedKeysForDiscount.add(discountKey);
        allSharedKeysForDiscount.addAll(JSON.parseObject(discount.getTemplate().getRule().getWeight(), List.class));
        return CollectionUtils.isSubCollection(Arrays.asList(fullKey, discountKey), allSharedKeysForFull) || CollectionUtils.isSubCollection(Arrays.asList(fullKey, discountKey), allSharedKeysForDiscount);
    }
}

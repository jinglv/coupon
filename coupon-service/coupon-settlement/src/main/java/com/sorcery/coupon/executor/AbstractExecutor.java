package com.sorcery.coupon.executor;

import com.alibaba.fastjson.JSON;
import com.sorcery.coupon.vo.GoodsInfo;
import com.sorcery.coupon.vo.SettlementInfo;
import org.apache.commons.collections4.CollectionUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 规则执行器抽象类，定义通用方法
 *
 * @author jinglv
 * @date 2024/1/17 10:16
 */
public abstract class AbstractExecutor {
    /**
     * 校验商品类型与优惠券是否匹配
     * 需要注意：
     * 1.这里实现的单品类优惠券的校验，多品类优惠券重载此方法
     * 2.商品只需要有一个优惠券要求的商品类型去匹配就可以
     *
     * @param settlementInfo {@link SettlementInfo}
     * @return boolean
     */
    @SuppressWarnings("all")
    protected boolean isGoodsTypeSatisfy(SettlementInfo settlementInfo) {
        List<Integer> goodsTypes = settlementInfo.getGoodsInfoList().stream().map(GoodsInfo::getType).collect(Collectors.toList());
        List<Integer> templateGoodsTypes = JSON.parseObject(settlementInfo.getCouponAndTemplateInfoList().get(0).getTemplate().getRule().getUsage().getGoodsType(), List.class);
        // 存在交集即可
        return CollectionUtils.isNotEmpty(CollectionUtils.intersection(goodsTypes, templateGoodsTypes));
    }

    /**
     * 处理商品类型与优惠券限制不匹配的情况
     *
     * @param settlementInfo {@link SettlementInfo} 用户传递的结算信息
     * @param goodSum        商品总价
     * @return {@link SettlementInfo} 已经修改过的结算信息
     */
    protected SettlementInfo processGoodsTypeNotSatisfy(SettlementInfo settlementInfo, double goodSum) {
        boolean isGoodsTypeSatisfy = isGoodsTypeSatisfy(settlementInfo);
        // 当商品类型不满足时，直接返回总价，并清空优惠券
        if (!isGoodsTypeSatisfy) {
            settlementInfo.setCost(goodSum);
            settlementInfo.setCouponAndTemplateInfoList(Collections.emptyList());
            return settlementInfo;
        }
        return null;
    }

    /**
     * 计算商品总价
     *
     * @param goodsInfoList {@link GoodsInfo}s 商品信息集合
     * @return 商品总价
     */
    protected double goodsCostSum(List<GoodsInfo> goodsInfoList) {
        return goodsInfoList.stream().mapToDouble(g -> g.getPrice() * g.getCount()).sum();
    }

    /**
     * 保留两位小数
     *
     * @param value 传入的数值
     * @return 两位小数
     */
    protected double retainToDecimals(double value) {
        return new BigDecimal(value).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    /**
     * 最小支付费用
     *
     * @return 费用
     */
    protected double minCost() {
        // 可自行设置规则
        return 0.1;
    }
}


package com.sorcery.coupon.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 规则类型枚举定义
 *
 * @author jinglv
 * @date 2024/1/17 09:49
 */
@Getter
@AllArgsConstructor
public enum RuleFlag {
    // 单类别优惠券定义
    FULL("满减券的计算规则"),
    DISCOUNT("折扣券的计算规则"),
    INSTANT("立减券的计算规则"),
    // 多类别优惠券定义
    FULL_DISCOUNT("满减券 + 折扣券的计算规则");

    // TODO 更多优惠券类别的组合
    /**
     * 规则描述
     */
    private final String description;
}

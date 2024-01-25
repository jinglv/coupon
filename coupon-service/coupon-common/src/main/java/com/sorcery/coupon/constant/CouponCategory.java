package com.sorcery.coupon.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;
import java.util.stream.Stream;

/**
 * 优惠券分类枚举
 *
 * @author jinglv
 * @date 2024/1/4 16:54
 */
@Getter
@AllArgsConstructor
public enum CouponCategory {

    FULL("满减券", "001"),
    DISCOUNT("折扣券", "002"),
    INSTANT("立减券", "003");

    /**
     * 优惠券描述（分类）
     */
    private final String description;
    /**
     * 优惠券分类编码
     */
    private final String code;

    public static CouponCategory of(String code) {
        Objects.requireNonNull(code);
        return Stream.of(values()).filter(bean -> bean.code.equals(code)).findAny().orElseThrow(() -> new IllegalArgumentException(code + "not exists!"));
    }

}

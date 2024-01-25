package com.sorcery.coupon.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;
import java.util.stream.Stream;

/**
 * 用户优惠券的状态
 *
 * @author jinglv
 * @date 2024/1/10 09:36
 */
@Getter
@AllArgsConstructor
public enum CouponStatus {
    USABLE("可用的", 1),
    USED("已使用的", 2),
    EXPIRED("未被使用的(已过期)", 3);
    /**
     * 优惠券状态描述信息
     */
    private final String description;
    /**
     * 优惠券状态编码
     */
    private final Integer code;

    /**
     * 根据code获取到CouponStatus
     *
     * @param code 优惠券状态编码
     * @return 用户优惠券的状态
     */
    public static CouponStatus of(Integer code) {
        Objects.requireNonNull(code);
        return Stream.of(values()).filter(bean -> bean.code.equals(code)).findAny().orElseThrow(() -> new IllegalArgumentException(code + "not exists"));
    }
}

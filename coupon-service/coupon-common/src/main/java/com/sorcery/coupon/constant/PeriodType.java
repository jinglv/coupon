package com.sorcery.coupon.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;
import java.util.stream.Stream;

/**
 * 有效期的类型枚举
 *
 * @author jinglv
 * @date 2024/1/5 10:23
 */
@Getter
@AllArgsConstructor
public enum PeriodType {

    REGULAR("固定日期", 1),
    SHIFT("变动日期（以领取之日开始计算）", 2);

    /**
     * 有效期的描述
     */
    private final String description;
    /**
     * 有效期编码
     */
    private final Integer code;

    public static PeriodType of(Integer code) {
        Objects.requireNonNull(code);
        return Stream.of(values()).filter(bean -> Objects.equals(bean.code, code)).findAny().orElseThrow(() -> new IllegalArgumentException(code + "not exists!"));
    }
}

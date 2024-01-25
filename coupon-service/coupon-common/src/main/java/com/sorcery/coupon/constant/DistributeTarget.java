package com.sorcery.coupon.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;
import java.util.stream.Stream;

/**
 * 分发目标类型枚举
 *
 * @author jinglv
 * @date 2024/1/4 17:11
 */
@Getter
@AllArgsConstructor
public enum DistributeTarget {
    SINGLE("单用户", 1),
    MULTI("多用户", 2);

    /**
     * 分发目标描述
     */
    private final String description;
    /**
     * 分发目标编码
     */
    private final Integer code;

    public static DistributeTarget of(Integer code) {
        Objects.requireNonNull(code);
        return Stream.of(values()).filter(bean -> Objects.equals(bean.code, code)).findAny().orElseThrow(() -> new IllegalArgumentException(code + "not exists!"));
    }
}

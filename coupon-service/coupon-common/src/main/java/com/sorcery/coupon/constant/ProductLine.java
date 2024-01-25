package com.sorcery.coupon.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;
import java.util.stream.Stream;

/**
 * 产品线枚举
 *
 * @author jinglv
 * @date 2024/1/4 17:06
 */
@Getter
@AllArgsConstructor
public enum ProductLine {
    BIG_DOG("满减券", 1),
    BIG_CAT("立减券", 2);

    /**
     * 产品线描述（分类）
     */
    private final String description;
    /**
     * 产品线编码
     */
    private final Integer code;

    public static ProductLine of(Integer code) {
        Objects.requireNonNull(code);
        return Stream.of(values()).filter(bean -> Objects.equals(bean.code, code)).findAny().orElseThrow(() -> new IllegalArgumentException(code + "not exists!"));
    }
}

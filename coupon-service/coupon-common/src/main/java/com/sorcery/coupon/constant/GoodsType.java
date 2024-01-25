package com.sorcery.coupon.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;
import java.util.stream.Stream;

/**
 * 商品类型枚举
 *
 * @author jinglv
 * @date 2024/1/10 10:48
 */
@Getter
@AllArgsConstructor
public enum GoodsType {

    CULTURAL("文娱", 1),
    FRESH("生鲜", 2),
    HOME("家居", 3),
    OTHERS("其他", 4),
    ALL("全品类", 5);
    /**
     * 分发目标描述
     */
    private final String description;
    /**
     * 分发目标编码
     */
    private final Integer code;

    public static GoodsType of(Integer code) {
        Objects.requireNonNull(code);
        return Stream.of(values()).filter(bean -> Objects.equals(bean.code, code)).findAny().orElseThrow(() -> new IllegalArgumentException(code + "not exists!"));
    }
}

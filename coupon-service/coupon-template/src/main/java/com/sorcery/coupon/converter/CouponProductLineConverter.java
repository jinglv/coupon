package com.sorcery.coupon.converter;

import com.sorcery.coupon.constant.ProductLine;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * 产品线枚举属性转换器
 *
 * @author jinglv
 * @date 2024/1/5 11:18
 */
@Converter
public class CouponProductLineConverter implements AttributeConverter<ProductLine, Integer> {
    @Override
    public Integer convertToDatabaseColumn(ProductLine productLine) {
        return productLine.getCode();
    }

    @Override
    public ProductLine convertToEntityAttribute(Integer integer) {
        return ProductLine.of(integer);
    }
}

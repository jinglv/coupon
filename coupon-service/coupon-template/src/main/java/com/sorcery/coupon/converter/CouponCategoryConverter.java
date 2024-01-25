package com.sorcery.coupon.converter;

import com.sorcery.coupon.constant.CouponCategory;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * 优惠券分类枚举属性转换器
 * AttributeConverter<X, Y>
 * X: 是实体属性的类型
 * Y: 是数据库字段的类型
 *
 * @author jinglv
 * @date 2024/1/5 11:13
 */
@Converter
public class CouponCategoryConverter implements AttributeConverter<CouponCategory, String> {
    /**
     * 将实体属性X转换为Y存储到数据库中，插入和更新时执行的动作
     *
     * @param couponCategory 实体
     * @return string
     */
    @Override
    public String convertToDatabaseColumn(CouponCategory couponCategory) {
        return couponCategory.getCode();
    }

    /**
     * 将数据库的列值转换为实体属性，查询操作时执行的动作
     *
     * @param s 数据库列值
     * @return CouponCategory
     */
    @Override
    public CouponCategory convertToEntityAttribute(String s) {
        return CouponCategory.of(s);
    }
}

package com.sorcery.coupon.converter;

import com.alibaba.fastjson.JSON;
import com.sorcery.coupon.vo.TemplateRule;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * 优惠券规则枚举属性转换器
 *
 * @author jinglv
 * @date 2024/1/5 11:30
 */
@Converter
public class CouponTemplateRuleConverter implements AttributeConverter<TemplateRule, String> {

    @Override
    public String convertToDatabaseColumn(TemplateRule templateRule) {
        return JSON.toJSONString(templateRule);
    }

    @Override
    public TemplateRule convertToEntityAttribute(String rule) {
        return JSON.parseObject(rule, TemplateRule.class);
    }
}

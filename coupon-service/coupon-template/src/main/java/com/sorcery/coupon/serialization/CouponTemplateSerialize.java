package com.sorcery.coupon.serialization;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.sorcery.coupon.entity.CouponTemplate;

import java.io.IOException;
import java.text.SimpleDateFormat;

/**
 * 优惠券模版实体类自定义序列化器
 *
 * @author jinglv
 * @date 2024/1/8 09:36
 */
public class CouponTemplateSerialize extends JsonSerializer<CouponTemplate> {
    /**
     * @param couponTemplate     序列化对象
     * @param jsonGenerator      生成json生成器
     * @param serializerProvider 序列化工具（无用）
     * @throws IOException IO异常
     */
    @Override
    public void serialize(CouponTemplate couponTemplate, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        // 开始序列化对象
        jsonGenerator.writeStartObject();
        jsonGenerator.writeStringField("id", String.valueOf(couponTemplate.getId()));
        jsonGenerator.writeStringField("name", couponTemplate.getName());
        jsonGenerator.writeStringField("logo", couponTemplate.getLogo());
        jsonGenerator.writeStringField("desc", couponTemplate.getDesc());
        jsonGenerator.writeStringField("category", couponTemplate.getCategory().getDescription());
        jsonGenerator.writeStringField("productLine", couponTemplate.getProductLine().getDescription());
        jsonGenerator.writeStringField("count", String.valueOf(couponTemplate.getCount()));
        jsonGenerator.writeStringField("createTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(couponTemplate.getCreateTime()));
        jsonGenerator.writeStringField("userId", String.valueOf(couponTemplate.getUserId()));
        jsonGenerator.writeStringField("key", couponTemplate.getKey() + String.format("%04d", couponTemplate.getId()));
        jsonGenerator.writeStringField("target", couponTemplate.getTarget().getDescription());
        jsonGenerator.writeStringField("rule", JSON.toJSONString(couponTemplate.getRule()));
        // 结束序列化对象
        jsonGenerator.writeEndObject();
    }
}

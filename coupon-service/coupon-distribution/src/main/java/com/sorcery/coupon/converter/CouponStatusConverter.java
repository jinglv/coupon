package com.sorcery.coupon.converter;

import com.sorcery.coupon.constant.CouponStatus;

import javax.persistence.AttributeConverter;
import javax.persistence.Convert;

/**
 * 优惠券状态枚举转换器
 *
 * @author jinglv
 * @date 2024/1/10 09:57
 */
@Convert
public class CouponStatusConverter implements AttributeConverter<CouponStatus, Integer> {
    @Override
    public Integer convertToDatabaseColumn(CouponStatus status) {
        return status.getCode();
    }

    @Override
    public CouponStatus convertToEntityAttribute(Integer integer) {
        return CouponStatus.of(integer);
    }
}

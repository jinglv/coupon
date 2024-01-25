package com.sorcery.coupon.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 优惠券kafka消息对象定义
 *
 * @author jinglv
 * @date 2024/1/12 09:48
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CouponKafkaMessage {
    /**
     * 优惠券状态
     */
    private Integer status;
    /**
     * Coupon主键
     */
    private List<Integer> ids;
}

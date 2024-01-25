package com.sorcery.coupon.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 获取优惠券请求对象定义
 *
 * @author jinglv
 * @date 2024/1/10 10:43
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AcquireTemplateRequestVO {
    /**
     * 用户Id
     */
    private Long userId;
    /**
     * 优惠券模板信息
     */
    private CouponTemplateSDK templateSDK;
}

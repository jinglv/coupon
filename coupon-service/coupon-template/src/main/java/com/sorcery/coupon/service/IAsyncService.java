package com.sorcery.coupon.service;

import com.sorcery.coupon.entity.CouponTemplate;

/**
 * 异步服务接口定义
 *
 * @author jinglv
 * @date 2024/1/8 10:53
 */
public interface IAsyncService {
    /**
     * 根据模板异步的创建优惠券码
     *
     * @param template {@link  CouponTemplate} 优惠券模版实体
     */
    void asyncConstructCouponByTemplate(CouponTemplate template);
}

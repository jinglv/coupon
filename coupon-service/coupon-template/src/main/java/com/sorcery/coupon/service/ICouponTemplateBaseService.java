package com.sorcery.coupon.service;

import com.sorcery.coupon.entity.CouponTemplate;
import com.sorcery.coupon.exception.CouponException;
import com.sorcery.coupon.vo.CouponTemplateSDK;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 优惠券模版基础服务(view,delete...)服务定义
 *
 * @author jinglv
 * @date 2024/1/8 11:03
 */
public interface ICouponTemplateBaseService {
    /**
     * 根据优惠模版 id 获取优惠券模版信息
     *
     * @param id 模版id
     * @return {@link CouponTemplate} 优惠券模板实体
     * @throws CouponException 自定义异常
     */
    CouponTemplate buildCouponTemplateInfo(Integer id) throws CouponException;

    /**
     * 查找所有可用的优惠券模版
     *
     * @return {@link CouponTemplateSDK}s 优惠券模板实体集合
     */
    List<CouponTemplateSDK> findAllUsableCouponTemplate();

    /**
     * 获取模板ids到CouponTemplateSDK的映射
     *
     * @param ids 模板 ids
     * @return Map<key: 模版id, value: CouponTemplateSDK>
     */
    Map<Integer, CouponTemplateSDK> findIdsToCouponTemplateSDK(Collection<Integer> ids);
}

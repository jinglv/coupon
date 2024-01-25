package com.sorcery.coupon.service;

import com.sorcery.coupon.entity.Coupon;
import com.sorcery.coupon.exception.CouponException;

import java.util.List;

/**
 * Redis相关的操作服务接口定义
 * 1. 用户的三个状态优惠券Cache操作
 * 2. 优惠券模版生成的优惠券码Cache操作
 *
 * @author jinglv
 * @date 2024/1/10 10:20
 */
public interface IRedisService {
    /**
     * 根据userId和状态找到缓存的优惠券列表数据
     *
     * @param userId 用户id
     * @param status {@link com.sorcery.coupon.constant.CouponStatus} 优惠券状态
     * @return {@link  Coupon}s 注意，可能返回null，代表从没有过记录
     */
    List<Coupon> getCachedCoupons(Long userId, Integer status);

    /**
     * 保存空的优惠券列表到缓存中（fake数据到缓存中）
     * 避免缓存穿透
     *
     * @param userId 用户id
     * @param status {@link com.sorcery.coupon.constant.CouponStatus} 优惠券状态
     */
    void saveEmptyCouponListToCache(Long userId, List<Integer> status);

    /**
     * 尝试从Cache中获取一个优惠券码
     *
     * @param templateId 优惠券模板主键
     * @return 优惠券码
     */
    String tryToAcquireCouponCodeFromCache(Integer templateId);

    /**
     * 将优惠券保存到Cache中
     *
     * @param userId  用户id
     * @param coupons {@link Coupon}s
     * @param status  优惠券状态
     * @return 保存成功的个数
     * @throws CouponException 业务异常
     */
    Integer addCouponToCache(Long userId, List<Coupon> coupons, Integer status) throws CouponException;
}

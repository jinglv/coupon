package com.sorcery.coupon.service;

import com.sorcery.coupon.entity.Coupon;
import com.sorcery.coupon.exception.CouponException;
import com.sorcery.coupon.vo.AcquireTemplateRequestVO;
import com.sorcery.coupon.vo.CouponTemplateSDK;
import com.sorcery.coupon.vo.SettlementInfo;

import java.util.List;

/**
 * 用户服务相关的接口定义
 * 1.用户三类状态优惠券信息展示服务
 * 2.查看用户当前可以领取的优惠券模板 - coupon-template 微服务配合实现
 * 3.用户领取优惠券服务
 * 4.用户消费优惠券服务 - coupon-settlement（结算微服务）微服务配合实现
 *
 * @author jinglv
 * @date 2024/1/10 10:36
 */
public interface IUserService {
    /**
     * 根据用户id和状态查询优惠券记录
     *
     * @param userId 用户id
     * @param status 优惠券状态
     * @return {@link Coupon}s
     * @throws CouponException 业务异常
     */
    List<Coupon> findCouponsByStatus(Long userId, Integer status) throws CouponException;

    /**
     * 根据用户id查找当前可以领取的优惠券模板
     *
     * @param userId 用户id
     * @return {@link  CouponTemplateSDK}s
     */
    List<CouponTemplateSDK> findAvailableTemplate(Long userId) throws CouponException;

    /**
     * 用户领取优惠券
     *
     * @param acquireTemplateRequestVO {@link  AcquireTemplateRequestVO}
     * @return {@link Coupon}
     * @throws CouponException 业务异常
     */
    Coupon acquireTemplate(AcquireTemplateRequestVO acquireTemplateRequestVO) throws CouponException;

    /**
     * 结算（核销）优惠券
     *
     * @param info {@link SettlementInfo}
     * @return {@link SettlementInfo}
     * @throws CouponException 业务异常
     */
    SettlementInfo settlement(SettlementInfo info) throws CouponException;

}

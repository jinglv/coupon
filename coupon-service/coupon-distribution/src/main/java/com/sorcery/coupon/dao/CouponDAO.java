package com.sorcery.coupon.dao;

import com.sorcery.coupon.constant.CouponStatus;
import com.sorcery.coupon.entity.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Coupon DAO接口定义
 *
 * @author jinglv
 * @date 2024/1/10 10:13
 */
public interface CouponDAO extends JpaRepository<Coupon, Integer> {
    /**
     * 根据userId + 状态寻找优惠券记录
     * where user_id = ... and status ...
     *
     * @param userId 领取用户
     * @param status 优惠券状态
     * @return 优惠券信息集合
     */
    List<Coupon> findAllByUserIdAndStatus(Long userId, CouponStatus status);
}

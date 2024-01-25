package com.sorcery.coupon.vo;

import com.sorcery.coupon.constant.CouponStatus;
import com.sorcery.coupon.constant.PeriodType;
import com.sorcery.coupon.entity.Coupon;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.time.DateUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 用户优惠券的分类，根据优惠券状态
 *
 * @author jinglv
 * @date 2024/1/15 13:53
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CouponClassify {
    /**
     * 可使用
     */
    private List<Coupon> usable;
    /**
     * 已使用的
     */
    private List<Coupon> used;
    /**
     * 已过期的
     */
    private List<Coupon> expired;

    /**
     * 对当前的优惠券进行分类
     *
     * @param coupons {@link Coupon}
     * @return CouponClassify
     */
    public static CouponClassify classify(List<Coupon> coupons) {
        List<Coupon> usable = new ArrayList<>(coupons.size());
        List<Coupon> used = new ArrayList<>(coupons.size());
        List<Coupon> expired = new ArrayList<>(coupons.size());
        coupons.forEach(c -> {
            // 判断优惠券是否过期
            boolean isTimeExpired;
            long currentTime = new Date().getTime();
            if (c.getTemplateSDK().getRule().getExpiration().getPeriod().equals(PeriodType.REGULAR.getCode())) {
                isTimeExpired = c.getTemplateSDK().getRule().getExpiration().getDeadLine() <= currentTime;
            } else {
                isTimeExpired = DateUtils.addDays(c.getAssignTime(), c.getTemplateSDK().getRule().getExpiration().getGap()).getTime() <= currentTime;
            }
            if (c.getStatus() == CouponStatus.USED) {
                used.add(c);
            } else if (c.getStatus() == CouponStatus.EXPIRED || isTimeExpired) {
                expired.add(c);
            } else {
                usable.add(c);
            }
        });
        return new CouponClassify(usable, used, expired);
    }
}

package com.sorcery.coupon.entity;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.sorcery.coupon.constant.CouponStatus;
import com.sorcery.coupon.converter.CouponStatusConverter;
import com.sorcery.coupon.serialization.CouponSerialize;
import com.sorcery.coupon.vo.CouponTemplateSDK;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;

/**
 * 优惠券（用户领取的优惠券记录）实体表
 *
 * @author jinglv
 * @date 2024/1/10 09:42
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "coupon")
@JsonSerialize(using = CouponSerialize.class)
public class Coupon {
    /**
     * 主键(自增)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;
    /**
     * 关联优惠券模板的主键（逻辑外键）
     */
    @Column(name = "template_id", nullable = false)
    private Integer templateId;
    /**
     * 领取用户
     */
    @Column(name = "user_id", nullable = false)
    private Long userId;
    /**
     * 优惠券码
     */
    @Column(name = "coupon_code", nullable = false)
    private String couponCode;
    /**
     * 领取时间
     */
    @CreatedDate
    @Column(name = "assign_time", nullable = false)
    private Date assignTime;
    /**
     * 优惠券状态
     */
    @Column(name = "status", nullable = false)
    @Convert(converter = CouponStatusConverter.class)
    private CouponStatus status;
    /**
     * 用户优惠券对应模板信息
     * 不属于表的属性，使用注解：Transient
     */
    @Transient
    private CouponTemplateSDK templateSDK;

    /**
     * 返回一个无效的Coupon对象
     *
     * @return Coupon对象
     */
    public static Coupon invalidCoupon() {
        Coupon coupon = new Coupon();
        coupon.setId(-1);
        return coupon;
    }

    /**
     * 构造优惠券
     */
    public Coupon(Integer templateId, Long userId, String couponCode, CouponStatus status) {
        this.templateId = templateId;
        this.userId = userId;
        this.couponCode = couponCode;
        this.status = status;
    }
}

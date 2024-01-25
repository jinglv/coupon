package com.sorcery.coupon.dao;

import com.sorcery.coupon.entity.CouponTemplate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * CouponTemplate DAO 接口定义
 * CouponTemplate -- 实体
 * Integer -- 主键ID
 *
 * @author jinglv
 * @date 2024/1/8 09:52
 */
public interface CouponTemplateDAO extends JpaRepository<CouponTemplate, Integer> {

    /**
     * 根据模版名称查询模板
     * where name = ...
     *
     * @param name 优惠券模版名称
     * @return 返回优惠券模版信息
     */
    CouponTemplate findByName(String name);

    /**
     * 根据available和expired标记查找模板记录
     * where available = ... and expired = ...
     *
     * @param available 是否可用状态
     * @param expired   是否过期
     * @return 返回优惠券模版信息集合
     */
    List<CouponTemplate> findAllByAvailableAndExpired(Boolean available, Boolean expired);

    /**
     * 根据expired标记查找模板记录
     * where expired = ...
     *
     * @param expired 是否过期
     * @return 返回优惠券模版信息集合
     */
    List<CouponTemplate> findAllByExpired(Boolean expired);
}

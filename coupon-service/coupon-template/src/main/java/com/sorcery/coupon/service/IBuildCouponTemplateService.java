package com.sorcery.coupon.service;

import com.sorcery.coupon.entity.CouponTemplate;
import com.sorcery.coupon.exception.CouponException;
import com.sorcery.coupon.vo.CouponTemplateRequestVO;

/**
 * 构建优惠券模版接口定义
 *
 * @author jinglv
 * @date 2024/1/8 10:35
 */
public interface IBuildCouponTemplateService {

    /**
     * 创建优惠券模版
     *
     * @param couponTemplateRequestVO {@link CouponTemplateRequestVO} 模板信息请求对象
     * @return {@link CouponTemplate} 优惠券模版实体
     * @throws CouponException 自定业务义异常
     */
    CouponTemplate buildCouponTemplate(CouponTemplateRequestVO couponTemplateRequestVO) throws CouponException;
}

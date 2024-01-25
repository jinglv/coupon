package com.sorcery.coupon.service;

import com.alibaba.fastjson.JSON;
import com.sorcery.coupon.BaseTest;
import com.sorcery.coupon.exception.CouponException;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;

/**
 * 优惠券模板基础服务的测试
 *
 * @author jinglv
 * @date 2024/1/9 14:23
 */
public class ICouponTemplateBaseServiceTest extends BaseTest {

    @Autowired
    private ICouponTemplateBaseService couponTemplateBaseService;

    @Test
    public void testBuildCouponTemplateInfo() throws CouponException {
        System.out.println(JSON.toJSONString(couponTemplateBaseService.buildCouponTemplateInfo(11)));
    }

    @Test
    public void testFindAllUsableCouponTemplate() {
        System.out.println(JSON.toJSONString(couponTemplateBaseService.findAllUsableCouponTemplate()));
    }

    @Test
    public void testFindIdsToCouponTemplateSDK() {
        System.out.println(JSON.toJSONString(couponTemplateBaseService.findIdsToCouponTemplateSDK(Arrays.asList(10, 11))));
    }
}
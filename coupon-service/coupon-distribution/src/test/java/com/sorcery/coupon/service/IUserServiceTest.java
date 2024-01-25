package com.sorcery.coupon.service;

import com.alibaba.fastjson.JSON;
import com.sorcery.coupon.BaseTest;
import com.sorcery.coupon.constant.CouponStatus;
import com.sorcery.coupon.exception.CouponException;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 用户服务功能测试用例
 *
 * @author jinglv
 * @date 2024/1/16 10:58
 */
public class IUserServiceTest extends BaseTest {

    private Long fakeUserId = 2024L;

    @Autowired
    private IUserService userService;

    @Test
    public void testFindCouponsByStatus() throws CouponException {
        System.out.println(JSON.toJSONString(userService.findCouponsByStatus(fakeUserId, CouponStatus.USED.getCode())));
    }

    @Test
    public void testFindAvailableTemplate() throws CouponException {
        System.out.println(JSON.toJSONString(userService.findAvailableTemplate(fakeUserId)));
    }

    @Test
    public void acquireTemplate() {
    }

    @Test
    public void settlement() {
    }
}
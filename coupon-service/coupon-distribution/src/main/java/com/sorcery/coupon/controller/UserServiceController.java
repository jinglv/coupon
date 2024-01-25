package com.sorcery.coupon.controller;

import com.alibaba.fastjson.JSON;
import com.sorcery.coupon.entity.Coupon;
import com.sorcery.coupon.exception.CouponException;
import com.sorcery.coupon.service.IUserService;
import com.sorcery.coupon.vo.AcquireTemplateRequestVO;
import com.sorcery.coupon.vo.CouponTemplateSDK;
import com.sorcery.coupon.vo.SettlementInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户服务Controller
 *
 * @author jinglv
 * @date 2024/1/16 10:24
 */
@Slf4j
@RequiredArgsConstructor
@RestController
public class UserServiceController {
    /**
     * 用户服务接口
     */
    private final IUserService userService;

    /**
     * 根据用户id和优惠券状态查找用户优惠券记录
     *
     * @param userId 用户id
     * @param status 优惠券状态
     * @return 用户优惠券记录
     * @throws CouponException 业务异常
     */
    @GetMapping("/coupons/{userId}/{status}")
    public List<Coupon> findCouponsByStatus(@PathVariable Long userId, @PathVariable Integer status) throws CouponException {
        log.info("Find Coupons By Status: {}, {}", userId, status);
        return userService.findCouponsByStatus(userId, status);
    }

    /**
     * 根据用户id查找当前可以领取的优惠券模板
     *
     * @param userId 用户id
     * @return 可以领取的优惠券模板
     * @throws CouponException 业务异常
     */
    @GetMapping("/template/{userId}")
    public List<CouponTemplateSDK> findAvailableTemplate(@PathVariable Long userId) throws CouponException {
        log.info("Find Available Template: {}", userId);
        return userService.findAvailableTemplate(userId);
    }

    /**
     * 用户领取优惠券
     *
     * @param acquireTemplateRequestVO 领取优惠券入参
     * @return 优惠券信息
     * @throws CouponException 业务异常
     */
    @PostMapping("/acquire/template")
    public Coupon acquireTemplate(@RequestBody AcquireTemplateRequestVO acquireTemplateRequestVO) throws CouponException {
        log.info("Acquire Template: {}", JSON.toJSONString(acquireTemplateRequestVO));
        return userService.acquireTemplate(acquireTemplateRequestVO);
    }

    /**
     * 结算（核销）优惠券
     *
     * @param info 结算（核销）优惠券入参
     * @return 结算（核销）优惠券信息
     * @throws CouponException 业务异常
     */
    @PostMapping("/settlement")
    public SettlementInfo settlementInfo(@RequestBody SettlementInfo info) throws CouponException {
        log.info("Settlement: {}", JSON.toJSONString(info));
        return userService.settlement(info);
    }
}

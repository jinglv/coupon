package com.sorcery.coupon.service.impl;

import com.google.common.base.Stopwatch;
import com.sorcery.coupon.constant.Constant;
import com.sorcery.coupon.dao.CouponTemplateDAO;
import com.sorcery.coupon.entity.CouponTemplate;
import com.sorcery.coupon.service.IAsyncService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 异步服务接口实现
 *
 * @author jinglv
 * @date 2024/1/8 11:23
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class AsyncServiceImpl implements IAsyncService {
    /**
     * CouponTemplate DAO
     */
    private final CouponTemplateDAO couponTemplateDAO;
    /**
     * 注入Redis模板类
     */
    private final StringRedisTemplate redisTemplate;

    /**
     * 根据模板异步的创建优惠券码
     *
     * @param template {@link  CouponTemplate} 优惠券模版实体
     */
    @Async("getAsyncExecutor")
    @SuppressWarnings("all")
    @Override
    public void asyncConstructCouponByTemplate(CouponTemplate template) {
        // 创建计时器
        Stopwatch stopwatch = Stopwatch.createStarted();
        Set<String> couponCodes = this.buildCouponCode(template);
        String redisKey = String.format("%s%s", Constant.RedisPrefix.COUPON_TEMPLATE, String.valueOf(template.getId()));
        log.info("Push CouponCode to Redis: {}", redisTemplate.opsForList().rightPushAll(redisKey, couponCodes));
        template.setAvailable(true);
        couponTemplateDAO.save(template);
        stopwatch.stop();
        log.info("Construct CouponCode By Template Cost: {}ms", stopwatch.elapsed(TimeUnit.MILLISECONDS));
        // TODO 发送短信或邮件通知优惠券模版已经可用
        log.info("CouponTemplate({}) is available", template.getId());
    }

    /**
     * 构造优惠券码
     * 优惠券码（对应每一张优惠券，18位）
     * 前四位：产品线 + 类型
     * 中间六位：日期随机（190101）
     * 后八位：0~9随机数构成
     *
     * @param couponTemplate {@link  CouponTemplate} 优惠券模版实体
     * @return Set<String>与couponTemplate.count相同个数的优惠券码
     */
    @SuppressWarnings("all")
    private Set<String> buildCouponCode(CouponTemplate couponTemplate) {
        // 创建计时器
        Stopwatch stopwatch = Stopwatch.createStarted();
        Set<String> result = new HashSet<>(couponTemplate.getCount());
        // 前四位
        String prefix = String.valueOf(couponTemplate.getProductLine().getCode()) + String.valueOf(couponTemplate.getCategory().getCode());
        String date = new SimpleDateFormat("yyyyMMdd").format(couponTemplate.getCreateTime());
        for (int i = 0; i < couponTemplate.getCount(); i++) {
            result.add(prefix + this.buildCouponCodeSuffix(date));
        }
        while (result.size() < couponTemplate.getCount()) {
            result.add(prefix + this.buildCouponCodeSuffix(date));
        }
        assert result.size() == couponTemplate.getCount();
        stopwatch.stop();
        log.info("Build Coupon Code Coset: {}ms", stopwatch.elapsed(TimeUnit.MILLISECONDS));
        return result;
    }

    /**
     * 构造优惠券码后14位
     *
     * @param date 创建优惠券的日期
     * @return 14位优惠券码
     */
    private String buildCouponCodeSuffix(String date) {
        char[] bases = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
        // 中间六位
        List<Character> characters = date.chars().mapToObj(e -> (char) e).collect(Collectors.toList());
        // shuffle洗牌算法
        Collections.shuffle(characters);
        String mid = characters.stream().map(Objects::toString).collect(Collectors.joining());
        // 后八位
        String suffix = RandomStringUtils.random(1, bases) + RandomStringUtils.random(7);
        return mid + suffix;
    }
}

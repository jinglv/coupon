package com.sorcery.coupon.service.impl;

import com.alibaba.fastjson.JSON;
import com.sorcery.coupon.constant.Constant;
import com.sorcery.coupon.constant.CouponStatus;
import com.sorcery.coupon.entity.Coupon;
import com.sorcery.coupon.exception.CouponException;
import com.sorcery.coupon.service.IRedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Redis相关操作服务接口实现
 *
 * @author jinglv
 * @date 2024/1/11 09:24
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class RedisServiceImpl implements IRedisService {
    /**
     * Redis 客户端
     * key和value类型都是String，使用StringRedisTemplate
     * RedisTemplate 的 KV 都是对象
     */
    private final StringRedisTemplate redisTemplate;

    /**
     * 根据userId和状态找到缓存的优惠券列表数据
     *
     * @param userId 用户id
     * @param status {@link com.sorcery.coupon.constant.CouponStatus} 优惠券状态
     * @return {@link  Coupon}s 注意，可能返回null，代表从没有过记录
     */
    @Override
    public List<Coupon> getCachedCoupons(Long userId, Integer status) {
        log.info("Get Coupons From Cache: {}, {}", userId, status);
        String redisKey = statusToRedisKey(status, userId);
        List<String> couponStr = redisTemplate.opsForHash().values(redisKey)
                .stream()
                .map(o -> Objects.toString(o, null))
                .collect(Collectors.toList());
        // 没有在缓存中获取到信息
        if (CollectionUtils.isEmpty(couponStr)) {
            saveEmptyCouponListToCache(userId, Collections.singletonList(status));
            return Collections.emptyList();
        }
        return couponStr.stream().map(cs -> JSON.parseObject(cs, Coupon.class)).collect(Collectors.toList());
    }

    /**
     * 保存空的优惠券列表到缓存中（fake数据到缓存中）
     * 目的：避免缓存穿透
     *
     * @param userId 用户id
     * @param status {@link com.sorcery.coupon.constant.CouponStatus} 优惠券状态
     */
    @Override
    @SuppressWarnings("all")
    public void saveEmptyCouponListToCache(Long userId, List<Integer> status) {
        log.info("Save Empty List To Cache For User: {}, Status: {}", userId, JSON.toJSONString(status));
        // key: coupon_id, value:是序列化的Coupon
        Map<String, String> invalidCouponMap = new HashMap<>();
        // Redis 中没有优惠券信息时，会从 MySQL 中检索一次，如果有数据，则加入到缓存中；如果 MySQL 中也没有数据，则添加一个 -1 优惠券。
        invalidCouponMap.put("-1", JSON.toJSONString(Coupon.invalidCoupon()));
        // 用户优惠券缓存信息，KV结构
        // K: status -> redisKey
        // V: {coupon_id:序列化的Coupon}
        // 使用SessionCallback把数据命令放入到Redis的pipeline
        SessionCallback<Object> sessionCallback = new SessionCallback<Object>() {
            @Override
            public Object execute(RedisOperations redisOperations) throws DataAccessException {
                status.forEach(s -> {
                    // 不需要泛型，已确定是String类型
                    String redisKey = statusToRedisKey(s, userId);
                    redisOperations.opsForHash().putAll(redisKey, invalidCouponMap);
                });
                return null;
            }
        };
        log.info("Pipeline Exe Result: {}", JSON.toJSONString(redisTemplate.executePipelined(sessionCallback)));
    }

    /**
     * 尝试从Cache中获取一个优惠券码
     *
     * @param templateId 优惠券模板主键
     * @return 优惠券码
     */
    @Override
    public String tryToAcquireCouponCodeFromCache(Integer templateId) {
        String redisKey = String.format("%s%s", Constant.RedisPrefix.COUPON_TEMPLATE, String.valueOf(templateId));
        // 因为优惠券码不存在顺序关系，左边pop或右边pop没有影响
        String couponCode = redisTemplate.opsForList().leftPop(redisKey);
        log.info("Acquire Coupon Code: {},{}, {} ", templateId, redisKey, couponCode);
        return couponCode;
    }

    /**
     * 将优惠券保存到Cache中
     *
     * @param userId  用户id
     * @param coupons {@link Coupon}s
     * @param status  优惠券状态
     * @return 保存成功的个数
     * @throws CouponException 业务异常
     */
    @Override
    public Integer addCouponToCache(Long userId, List<Coupon> coupons, Integer status) throws CouponException {
        log.info("Add Coupon To Cache:{}, {}, {}", userId, JSON.toJSONString(coupons), status);
        // 保存到对应Cache里Coupon的个数
        int result = -1;
        // 获取优惠券的类别
        CouponStatus couponStatus = CouponStatus.of(status);
        switch (couponStatus) {
            case USABLE:
                result = addCouponToCacheForUsable(userId, coupons);
                break;
            case USED:
                result = addCouponToCacheFoeUsed(userId, coupons);
                break;
            case EXPIRED:
                result = addCouponToCacheForExpired(userId, coupons);
                break;
        }
        return result;
    }

    /**
     * 根据status 获取到对应的Redis Key
     *
     * @param status 状态
     * @param userId 用户Id
     * @return Redis Key
     */
    private String statusToRedisKey(Integer status, Long userId) {
        String redisKey = null;
        CouponStatus couponStatus = CouponStatus.of(status);
        switch (couponStatus) {
            case USABLE:
                redisKey = String.format("%s%s", Constant.RedisPrefix.USER_COUPON_USABLE, userId);
                break;
            case USED:
                redisKey = String.format("%s%s", Constant.RedisPrefix.USER_COUPON_USED, userId);
                break;
            case EXPIRED:
                redisKey = String.format("%s%s", Constant.RedisPrefix.USER_COUPON_EXPIRED, userId);
                break;
            // couponStatus一定不会为Null，因此可以不需要default
        }
        return redisKey;
    }

    /**
     * 新增加优惠券到Cache中
     *
     * @param userId  用户Id
     * @param coupons Coupons
     * @return cache信息个数
     */
    private Integer addCouponToCacheForUsable(Long userId, List<Coupon> coupons) {
        // 如果 status是USABLE，代表是新增加的优惠券
        // 只会影响一个Cache：USER_COUPON_USABLE
        log.debug("Add Coupon To Cache FOr Usable.");
        Map<String, String> needCachedObject = new HashMap<>();
        coupons.forEach(c -> needCachedObject.put(String.valueOf(c.getId()), JSON.toJSONString(c)));
        String redisKey = statusToRedisKey(CouponStatus.USABLE.getCode(), userId);
        redisTemplate.opsForHash().putAll(redisKey, needCachedObject);
        log.info("Add {} Coupons To Cache: {}, {}", needCachedObject.size(), userId, redisKey);
        // 设置Redis过期时间
        redisTemplate.expire(redisKey, getRandomExpirationTime(1, 2), TimeUnit.SECONDS);
        return needCachedObject.size();
    }

    /**
     * 将已使用的优惠券加入到Cache中
     *
     * @param userId  用户Id
     * @param coupons Coupons
     * @return cache信息个数
     * @throws CouponException 业务异常
     */
    @SuppressWarnings("all")
    private Integer addCouponToCacheFoeUsed(Long userId, List<Coupon> coupons) throws CouponException {
        // 如果status是USED，代表用户操作是使用当前的优惠券，影响到两个Cache
        // USABLE -> USED 状态转换
        log.debug("Add Coupon To Cache For Used");
        Map<String, String> needCachedForUsed = new HashMap<>(coupons.size());
        //获取 redis key 为Usable缓存
        String redisKeyForUsable = statusToRedisKey(CouponStatus.USABLE.getCode(), userId);
        //获取 redis key 为Used缓存
        String redisKeyForUsed = statusToRedisKey(CouponStatus.USED.getCode(), userId);
        // 获取当前用户可用的优惠券
        List<Coupon> curUsableCoupons = getCachedCoupons(userId, CouponStatus.USABLE.getCode());
        // 当前可用的优惠券个数一定是大于1的
        assert curUsableCoupons.size() > coupons.size();
        coupons.forEach(c -> needCachedForUsed.put(String.valueOf(c.getId()), JSON.toJSONString(c)));
        // 校验当前的优惠券参数是否与Cached中的匹配
        List<Integer> curUsableIds = curUsableCoupons.stream().map(Coupon::getId).collect(Collectors.toList());
        List<Integer> paramIds = coupons.stream().map(Coupon::getId).collect(Collectors.toList());
        // paramIds是curUsableIds的子集，则返回true
        if (!CollectionUtils.isSubCollection(paramIds, curUsableIds)) {
            log.error("CurCoupons Is Not Equal ToCache: {}, {}, {}", userId, JSON.toJSONString(curUsableIds), JSON.toJSONString(paramIds));
            throw new CouponException("CurCoupons Is Not Equal To Cache!");
        }
        List<String> needCleanKey = paramIds.stream().map(String::valueOf).collect(Collectors.toList());
        SessionCallback<Objects> sessionCallback = new SessionCallback<Objects>() {
            @Override
            public Objects execute(RedisOperations redisOperations) throws DataAccessException {
                // 1. 已使用的优惠券Cache缓存添加
                redisOperations.opsForHash().putAll(redisKeyForUsed, needCachedForUsed);
                // 2. 可用的优惠券Cache需要清理
                redisOperations.opsForHash().delete(redisKeyForUsable, needCleanKey.toArray());
                // 3重置过期时间
                redisOperations.expire(redisKeyForUsable, getRandomExpirationTime(1, 2), TimeUnit.SECONDS);
                redisOperations.expire(redisKeyForUsed, getRandomExpirationTime(1, 2), TimeUnit.SECONDS);
                return null;
            }
        };
        log.info("Pipleline Exe Result: {}", JSON.toJSONString(redisTemplate.executePipelined(sessionCallback)));
        return coupons.size();
    }

    /**
     * 将过期优惠券加入到Cache中
     *
     * @param userId  userId  用户Id
     * @param coupons coupons Coupons
     * @return cache信息个数
     * @throws CouponException 业务异常
     */
    @SuppressWarnings("all")
    private Integer addCouponToCacheForExpired(Long userId, List<Coupon> coupons) throws CouponException {
        // status 是 expired，代表是已有的优惠券过期了，影响到两个Cache
        // USABLE EXPIRED
        log.debug("Add COupon To Cache For Expired.");
        // 最终需要保存的Cache
        Map<String, String> neddCachedForExpired = new HashMap<>(coupons.size());
        String redisKeyForUsable = statusToRedisKey(CouponStatus.USABLE.getCode(), userId);
        String redisKeyForExpired = statusToRedisKey(CouponStatus.EXPIRED.getCode(), userId);
        List<Coupon> currentUsableCoupons = getCachedCoupons(userId, CouponStatus.USABLE.getCode());
        List<Coupon> currentExpiredCoupons = getCachedCoupons(userId, CouponStatus.EXPIRED.getCode());
        // 当前可用的优惠券个数一定大于1的
        assert currentUsableCoupons.size() > coupons.size();
        coupons.forEach(c -> neddCachedForExpired.put(String.valueOf(c.getId()), JSON.toJSONString(c)));
        // 校验当前的优惠券参数是否与Cached中的匹配
        List<Integer> currentUsableIds = currentUsableCoupons.stream().map(Coupon::getId).collect(Collectors.toList());
        List<Integer> paramIds = coupons.stream().map(Coupon::getId).collect(Collectors.toList());
        if (!CollectionUtils.isSubCollection(paramIds, currentUsableIds)) {
            log.error("CurrentCoupons Is Not Equal To Cache: {}, {}, {}", userId, JSON.toJSONString(currentUsableIds), JSON.toJSONString(paramIds));
            throw new CouponException("CurrentCoupons Is Not Equal To Cache");
        }
        List<String> needCleanKey = paramIds.stream().map(i -> i.toString()).collect(Collectors.toList());
        SessionCallback<Objects> sessionCallback = new SessionCallback<Objects>() {
            @Override
            public Objects execute(RedisOperations redisOperations) throws DataAccessException {
                // 1. 已过期优惠券Cache缓存
                redisOperations.opsForHash().putAll(redisKeyForExpired, neddCachedForExpired);
                // 2. 可用的优惠券Cache需要清理
                redisOperations.opsForHash().delete(redisKeyForExpired, needCleanKey.toArray());
                // 3. 重置过期时间
                redisOperations.expire(redisKeyForUsable, getRandomExpirationTime(1, 2), TimeUnit.SECONDS);
                redisOperations.expire(redisKeyForExpired, getRandomExpirationTime(1, 2), TimeUnit.SECONDS);
                return null;
            }
        };
        log.info("Pipeline Exe Result: {}", JSON.toJSONString(redisTemplate.executePipelined(sessionCallback)));
        return coupons.size();
    }

    /**
     * 获取一个随机的过期时间
     * 缓存雪崩：key在同一时间失效（直接请求MySQL，造成MySQL压力大）
     *
     * @param min 最小的小时数
     * @param max 最大的小时数
     * @return 返回[min, max]之间的随机秒数
     */
    private Long getRandomExpirationTime(Integer min, Integer max) {
        return RandomUtils.nextLong(min * 60 * 60, max * 60 * 60);
    }
}

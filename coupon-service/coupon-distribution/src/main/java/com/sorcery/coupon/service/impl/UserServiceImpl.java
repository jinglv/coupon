package com.sorcery.coupon.service.impl;

import com.alibaba.fastjson.JSON;
import com.sorcery.coupon.constant.Constant;
import com.sorcery.coupon.constant.CouponStatus;
import com.sorcery.coupon.dao.CouponDAO;
import com.sorcery.coupon.entity.Coupon;
import com.sorcery.coupon.exception.CouponException;
import com.sorcery.coupon.feign.CouponTemplateClient;
import com.sorcery.coupon.feign.SettlementClient;
import com.sorcery.coupon.service.IRedisService;
import com.sorcery.coupon.service.IUserService;
import com.sorcery.coupon.vo.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 用户服务相关的接口实现
 * 所有的草错过程，状态都保存在Redis中，并通过Kafka吧消息传递到MySQL中（异步处理）
 * 为什么使用Kafka，而不是直接使用SpringBoot中的异步处理？
 *
 * @author jinglv
 * @date 2024/1/15 14:07
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class UserServiceImpl implements IUserService {
    /**
     * Coupon DAO
     */
    private final CouponDAO couponDAO;
    /**
     * Redis服务
     */
    private final IRedisService redisService;
    /**
     * 模版微服务客户端
     */
    private final CouponTemplateClient couponTemplateClient;
    /**
     * 结算微服务客户端
     */
    private final SettlementClient settlementClient;
    /**
     * kafka客户端
     */
    private final KafkaTemplate<String, String> kafkaTemplate;

    /**
     * 根据用户id和状态查询优惠券记录
     *
     * @param userId 用户id
     * @param status 优惠券状态
     * @return {@link Coupon}s
     * @throws CouponException 业务异常
     */
    @Override
    public List<Coupon> findCouponsByStatus(Long userId, Integer status) throws CouponException {
        // 从redis获取优惠券数据
        List<Coupon> curCached = redisService.getCachedCoupons(userId, status);
        List<Coupon> preTarget;
        if (CollectionUtils.isNotEmpty(curCached)) {
            log.debug("coupon cache is not empty: {}, {}", userId, status);
            preTarget = curCached;
        } else {
            log.debug("coupon cache is empty, get coupon from db: {}, {}", userId, status);
            List<Coupon> dbCoupons = couponDAO.findAllByUserIdAndStatus(userId, CouponStatus.of(status));
            // 如果数据库中没有记录，直接返回，Cache中已经加入了一张无效的优惠券
            if (CollectionUtils.isEmpty(dbCoupons)) {
                log.debug("current user do not have coupon: {}, {}", userId, status);
                return dbCoupons;
            }
            // 填充dbCoupons的templateSDK字段
            Map<Integer, CouponTemplateSDK> idToTemplateSDK = couponTemplateClient
                    .findIdsToTemplateSDK(dbCoupons.stream().map(Coupon::getTemplateId).collect(Collectors.toList())).getData();
            dbCoupons.forEach(dc -> dc.setTemplateSDK(idToTemplateSDK.get(dc.getTemplateId())));
            // 数据库中存在记录
            preTarget = dbCoupons;
            // 将记录写入Cache
            redisService.addCouponToCache(userId, preTarget, status);
        }
        // 将无效优惠券剔除（Redis中无效优惠券为-1）
        preTarget = preTarget.stream().filter(c -> c.getId() != -1).collect(Collectors.toList());
        // 如果当前获取的是可用优惠券，还需要对已过期优惠券的延迟处理
        if (CouponStatus.of(status) == CouponStatus.USABLE) {
            CouponClassify classify = CouponClassify.classify(preTarget);
            // 如果已过期状态不为空，需要做延迟处理
            if (CollectionUtils.isNotEmpty(classify.getExpired())) {
                log.info("Add Expired Coupons To Cache From FindCouponsByStatus: {}, {}", userId, status);
                redisService.addCouponToCache(userId, classify.getExpired(), CouponStatus.EXPIRED.getCode());
                // 发送到kafka中做异步处理
                kafkaTemplate.send(Constant.TOPIC, JSON.toJSONString(new CouponKafkaMessage(CouponStatus.EXPIRED.getCode(),
                        classify.getExpired().stream().map(Coupon::getId).collect(Collectors.toList()))));
            }
            return classify.getUsable();
        }
        return preTarget;
    }

    /**
     * 根据用户id查找当前可以领取的优惠券模板
     *
     * @param userId 用户id
     * @return {@link  CouponTemplateSDK}s
     */
    @Override
    public List<CouponTemplateSDK> findAvailableTemplate(Long userId) throws CouponException {
        long curTime = new Date().getTime();
        List<CouponTemplateSDK> templateSDKS = couponTemplateClient.findAllUsableCouponTemplate().getData();
        log.debug("Find All Template(From Template) Count: {}", templateSDKS.size());
        // 过滤过期的优惠券模板
        templateSDKS = templateSDKS.stream().filter(t -> t.getRule().getExpiration().getDeadLine() > curTime).collect(Collectors.toList());
        log.info("Find Usable Template Count: {}", templateSDKS.size());
        // key是templateId
        // value中的left是Template limitation， right是优惠券模版
        Map<Integer, Pair<Integer, CouponTemplateSDK>> limitToTemplate = new HashMap<>(templateSDKS.size());
        templateSDKS.forEach(t -> limitToTemplate.put(t.getId(), Pair.of(t.getRule().getLimitation(), t)));
        List<CouponTemplateSDK> result = new ArrayList<>(limitToTemplate.size());
        List<Coupon> userUsableCoupons = this.findCouponsByStatus(userId, CouponStatus.USABLE.getCode());
        log.debug("Current User Usable Coupons: {}, {}", userId, userUsableCoupons.size());
        // key是TemplateId
        Map<Integer, List<Coupon>> templateIdToCoupons = userUsableCoupons.stream().collect(Collectors.groupingBy(Coupon::getTemplateId));
        // 根据Template的Rule判断是否可以领取优惠券模版
        limitToTemplate.forEach((k, v) -> {
            int limitation = v.getLeft();
            CouponTemplateSDK templateSDK = v.getRight();
            if (templateIdToCoupons.containsKey(k) && templateIdToCoupons.get(k).size() >= limitation) {
                // 不可领取
                return;
            }
            result.add(templateSDK);
        });
        return result;
    }

    /**
     * 用户领取优惠券
     * 1. 从CouponTemplateClient拿到对应的优惠券，并检查是否过期
     * 2. 根据limitation判断用户是否可以领取
     * 3. save to db
     * 4. 填充CouponTemplateSDK
     * 5. save to cache
     *
     * @param acquireTemplateRequestVO {@link  AcquireTemplateRequestVO}
     * @return {@link Coupon}
     * @throws CouponException 业务异常
     */
    @Override
    public Coupon acquireTemplate(AcquireTemplateRequestVO acquireTemplateRequestVO) throws CouponException {
        Map<Integer, CouponTemplateSDK> idToTemplate = couponTemplateClient.findIdsToTemplateSDK(Collections.singletonList(acquireTemplateRequestVO.getTemplateSDK().getId())).getData();
        // 优惠券模版是需要存在的
        if (idToTemplate.size() <= 0) {
            log.error("Can Not Acquired Template From TemplateClient: {}", acquireTemplateRequestVO.getTemplateSDK().getId());
            throw new CouponException("Can Not Acquired Template From TemplateClient");
        }
        // 用户是否可以领取这张优惠券
        List<Coupon> userUsableCoupons = findCouponsByStatus(acquireTemplateRequestVO.getUserId(), CouponStatus.USABLE.getCode());
        Map<Integer, List<Coupon>> templateIdToCoupons = userUsableCoupons.stream().collect(Collectors.groupingBy(Coupon::getTemplateId));
        if (templateIdToCoupons.containsKey(acquireTemplateRequestVO.getTemplateSDK().getId()) &&
                templateIdToCoupons.get(acquireTemplateRequestVO.getTemplateSDK().getId()).size() >= acquireTemplateRequestVO.getTemplateSDK().getRule().getLimitation()) {
            log.error("Exceed Template Assign Limitation: {}", acquireTemplateRequestVO.getTemplateSDK().getId());
            throw new CouponException("Exceed Template Assign Limitation: {}");
        }
        // 尝试去获取优惠券码
        String couponCode = redisService.tryToAcquireCouponCodeFromCache(acquireTemplateRequestVO.getTemplateSDK().getId());
        if (StringUtils.isEmpty(couponCode)) {
            log.error("Can Not Acquire Coupon Code: {}", acquireTemplateRequestVO.getTemplateSDK().getId());
            throw new CouponException("Can Not Acquire Coupon Code");
        }
        Coupon newCoupon = new Coupon(acquireTemplateRequestVO.getTemplateSDK().getId(), acquireTemplateRequestVO.getUserId(), couponCode, CouponStatus.USABLE);
        newCoupon = couponDAO.save(newCoupon);
        // 填充Coupon对象的CouponTemplateSDK，一定要在放入缓存之前去填充
        newCoupon.setTemplateSDK(acquireTemplateRequestVO.getTemplateSDK());
        // 放入缓存中
        redisService.addCouponToCache(acquireTemplateRequestVO.getUserId(), Collections.singletonList(newCoupon), CouponStatus.USABLE.getCode());
        return newCoupon;
    }

    /**
     * 结算（核销）优惠券
     * 规则相关处理需要由Settlement系统去做，当前系统仅仅做业务处理过程（校验过程）
     *
     * @param info {@link SettlementInfo}
     * @return {@link SettlementInfo}
     * @throws CouponException 业务异常
     */
    @Override
    public SettlementInfo settlement(SettlementInfo info) throws CouponException {
        // 当没有传递优惠券时，直接返回商品总价
        List<SettlementInfo.CouponAndTemplateInfo> couponAndTemplateInfoList = info.getCouponAndTemplateInfoList();
        if (CollectionUtils.isNotEmpty(couponAndTemplateInfoList)) {
            log.info("Empty Coupons For Settle");
            double goodsSum = 0.0;

            for (GoodsInfo goodsInfo : info.getGoodsInfoList()) {
                goodsSum += goodsInfo.getPrice() * goodsInfo.getCount();
            }
            // 没有优惠券也就不存在优惠券核销，SettlementInfo其他字段不需要修改
            info.setCost(retainToDecimals(goodsSum));
        }
        // 校验传递的优惠券是否是用户自己的
        List<Coupon> coupons = findCouponsByStatus(info.getUserId(), CouponStatus.USABLE.getCode());
        Map<Integer, Coupon> idToCoupon = coupons.stream().collect(Collectors.toMap(Coupon::getId, Function.identity()));
        if (MapUtils.isEmpty(idToCoupon) || !CollectionUtils.isSubCollection(
                couponAndTemplateInfoList.stream().map(SettlementInfo.CouponAndTemplateInfo::getId).collect(Collectors.toList()),
                idToCoupon.keySet())) {
            log.info("{}", idToCoupon.keySet());
            log.info("{}", couponAndTemplateInfoList.stream().map(SettlementInfo.CouponAndTemplateInfo::getId).collect(Collectors.toList()));
            log.error("User Coupon Has Some Problem, It is not SubCollection Of Coupons!");
            throw new CouponException("User Coupon Has Some Problem, It is not SubCollection Of Coupons!");
        }
        log.debug("Current Settlement Coupons Is User's: {}", couponAndTemplateInfoList.size());
        List<Coupon> settleCoupons = new ArrayList<>(couponAndTemplateInfoList.size());
        couponAndTemplateInfoList.forEach(ci -> settleCoupons.add(idToCoupon.get(ci.getId())));
        // 通过结算服务获取结算信息
        SettlementInfo processedInfo = settlementClient.computeRule(info).getData();
        if (processedInfo.getEmploy() && CollectionUtils.isNotEmpty(processedInfo.getCouponAndTemplateInfoList())) {
            log.info("Settle User Coupon: {}, {}", info.getUserId(), JSON.toJSONString(settleCoupons));
            // 更新缓存
            redisService.addCouponToCache(info.getUserId(), settleCoupons, CouponStatus.USED.getCode());
            // 更新DB
            kafkaTemplate.send(Constant.TOPIC, JSON.toJSONString(new CouponKafkaMessage(CouponStatus.USED.getCode(), settleCoupons.stream().map(Coupon::getId).collect(Collectors.toList()))));
        }
        return processedInfo;
    }

    /**
     * 保留两位小数
     *
     * @param value 数值
     * @return 已保留两位小数
     */
    private double retainToDecimals(double value) {
        // BigDecimal.ROUND_HALF_UP 代表四舍五入
        return new BigDecimal(value).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }
}

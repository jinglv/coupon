package com.sorcery.coupon.executor;

import com.alibaba.fastjson.JSON;
import com.sorcery.coupon.BaseTest;
import com.sorcery.coupon.constant.CouponCategory;
import com.sorcery.coupon.constant.GoodsType;
import com.sorcery.coupon.exception.CouponException;
import com.sorcery.coupon.vo.CouponTemplateSDK;
import com.sorcery.coupon.vo.GoodsInfo;
import com.sorcery.coupon.vo.SettlementInfo;
import com.sorcery.coupon.vo.TemplateRule;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.Collections;

/**
 * 结算规则执行管理器测试用例
 * 对Executor的分发与结算逻辑进行测试
 *
 * @author jinglv
 * @date 2024/1/22 10:07
 */
@Slf4j
public class ExecuteManagerTest extends BaseTest {
    /**
     * fake一个UserId
     */
    private Long fakeUserId = 20001L;

    @Autowired
    private ExecuteManager executeManager;

    @Test
    public void testComputeRule() throws CouponException {
        // 满减优惠券结算测试
//        log.info("Full Coupon Executor Test...");
//        SettlementInfo fullSettlementInfo = fakeFullCouponSettlement();
//        SettlementInfo result = executeManager.computeRule(fullSettlementInfo);
//        log.info("{}", result.getCost());
//        log.info("{}", result.getCouponAndTemplateInfoList().size());
//        log.info("{}", result.getCouponAndTemplateInfoList());

        // 折扣优惠券结算测试
//        log.info("Discount Coupon Executor Test...");
//        SettlementInfo discountSettlementInfo = fakeDiscountCouponSettlement();
//        SettlementInfo result = executeManager.computeRule(discountSettlementInfo);
//        log.info("{}", result.getCost());
//        log.info("{}", result.getCouponAndTemplateInfoList().size());
//        log.info("{}", result.getCouponAndTemplateInfoList());

        // 立减优惠券结算测试
//        log.info("Instant Coupon Executor Test...");
//        SettlementInfo instantSettlementInfo = fakeInstantCouponSettlement();
//        SettlementInfo result = executeManager.computeRule(instantSettlementInfo);
//        log.info("{}", result.getCost());
//        log.info("{}", result.getCouponAndTemplateInfoList().size());
//        log.info("{}", result.getCouponAndTemplateInfoList());

        // 满减折扣优惠券结算测试
        log.info("Full And Discount Coupon Executor Test...");
        SettlementInfo fullAndDiscountSettlementInfo = fakeFullAndDiscountCouponSettlement();
        SettlementInfo result = executeManager.computeRule(fullAndDiscountSettlementInfo);
        log.info("{}", result.getCost());
        log.info("{}", result.getCouponAndTemplateInfoList().size());
        log.info("{}", result.getCouponAndTemplateInfoList());
    }

    @Test
    public void postProcessBeforeInitialization() {
    }

    @Test
    public void postProcessAfterInitialization() {
    }

    /**
     * fake 满减优惠券的结算信息
     *
     * @return SettlementInfo
     */
    private SettlementInfo fakeFullCouponSettlement() {
        SettlementInfo info = new SettlementInfo();
        info.setUserId(fakeUserId);
        info.setEmploy(false);
        info.setCost(0.0);
        GoodsInfo goodsInfo01 = new GoodsInfo();
        goodsInfo01.setCount(2);
        goodsInfo01.setPrice(10.88);
        goodsInfo01.setType(GoodsType.CULTURAL.getCode());

        GoodsInfo goodsInfo02 = new GoodsInfo();
        // 达到满减标准
//        goodsInfo02.setCount(10);
        // 没有达到满减标准
        goodsInfo02.setCount(5);
        goodsInfo02.setPrice(20.88);
        goodsInfo02.setType(GoodsType.CULTURAL.getCode());

        info.setGoodsInfoList(Arrays.asList(goodsInfo01, goodsInfo02));

        SettlementInfo.CouponAndTemplateInfo ctInfo = new SettlementInfo.CouponAndTemplateInfo();
        ctInfo.setId(1);

        CouponTemplateSDK templateSDK = new CouponTemplateSDK();
        templateSDK.setId(1);
        templateSDK.setCategory(CouponCategory.FULL.getCode());
        templateSDK.setKey("1001202401228888");
        TemplateRule rule = new TemplateRule();
        rule.setDiscount(new TemplateRule.Discount(20, 199));
        rule.setUsage(new TemplateRule.Usage("北京市", "北京市", JSON.toJSONString(Arrays.asList(GoodsType.CULTURAL.getCode(), GoodsType.HOME.getCode()))));
        templateSDK.setRule(rule);
        ctInfo.setTemplate(templateSDK);
        info.setCouponAndTemplateInfoList(Collections.singletonList(ctInfo));
        return info;
    }

    /**
     * fake 折扣优惠券的结算信息
     *
     * @return SettlementInfo
     */
    private SettlementInfo fakeDiscountCouponSettlement() {
        SettlementInfo info = new SettlementInfo();
        info.setUserId(fakeUserId);
        info.setEmploy(false);
        info.setCost(0.0);
        GoodsInfo goodsInfo01 = new GoodsInfo();
        goodsInfo01.setCount(2);
        goodsInfo01.setPrice(10.88);
        goodsInfo01.setType(GoodsType.CULTURAL.getCode());

        GoodsInfo goodsInfo02 = new GoodsInfo();
        goodsInfo02.setCount(10);
        goodsInfo02.setPrice(20.88);
        goodsInfo02.setType(GoodsType.CULTURAL.getCode());

        info.setGoodsInfoList(Arrays.asList(goodsInfo01, goodsInfo02));

        SettlementInfo.CouponAndTemplateInfo ctInfo = new SettlementInfo.CouponAndTemplateInfo();
        ctInfo.setId(1);

        CouponTemplateSDK templateSDK = new CouponTemplateSDK();
        templateSDK.setId(2);
        templateSDK.setCategory(CouponCategory.DISCOUNT.getCode());
        templateSDK.setKey("1001202401229999");
        // 设置模版规则
        TemplateRule rule = new TemplateRule();
        rule.setDiscount(new TemplateRule.Discount(85, 1));
//        rule.setUsage(new TemplateRule.Usage("北京市", "北京市", JSON.toJSONString(Arrays.asList(GoodsType.CULTURAL.getCode(), GoodsType.HOME.getCode()))));
        // 测试商品类型不匹配
        rule.setUsage(new TemplateRule.Usage("北京市", "北京市", JSON.toJSONString(Arrays.asList(GoodsType.FRESH.getCode(), GoodsType.HOME.getCode()))));

        templateSDK.setRule(rule);
        ctInfo.setTemplate(templateSDK);
        info.setCouponAndTemplateInfoList(Collections.singletonList(ctInfo));
        return info;
    }

    /**
     * fake立减优惠券的结算信息
     *
     * @return SettlementInfo
     */
    private SettlementInfo fakeInstantCouponSettlement() {
        SettlementInfo info = new SettlementInfo();
        info.setUserId(fakeUserId);
        info.setEmploy(false);
        info.setCost(0.0);
        GoodsInfo goodsInfo01 = new GoodsInfo();
        goodsInfo01.setCount(2);
        goodsInfo01.setPrice(10.88);
        goodsInfo01.setType(GoodsType.CULTURAL.getCode());

        GoodsInfo goodsInfo02 = new GoodsInfo();
        goodsInfo02.setCount(10);
        goodsInfo02.setPrice(20.88);
        goodsInfo02.setType(GoodsType.CULTURAL.getCode());

        info.setGoodsInfoList(Arrays.asList(goodsInfo01, goodsInfo02));

        SettlementInfo.CouponAndTemplateInfo ctInfo = new SettlementInfo.CouponAndTemplateInfo();
        ctInfo.setId(1);

        CouponTemplateSDK templateSDK = new CouponTemplateSDK();
        templateSDK.setId(3);
        templateSDK.setCategory(CouponCategory.INSTANT.getCode());
        templateSDK.setKey("1001202401226666");
        // 设置模版规则
        TemplateRule rule = new TemplateRule();
        rule.setDiscount(new TemplateRule.Discount(5, 1));
        rule.setUsage(new TemplateRule.Usage("北京市", "北京市", JSON.toJSONString(Arrays.asList(GoodsType.CULTURAL.getCode(), GoodsType.HOME.getCode()))));

        templateSDK.setRule(rule);
        ctInfo.setTemplate(templateSDK);
        info.setCouponAndTemplateInfoList(Collections.singletonList(ctInfo));
        return info;
    }

    /**
     * fake 满减和折扣优惠券的结算信息
     *
     * @return SettlementInfo
     */
    private SettlementInfo fakeFullAndDiscountCouponSettlement() {
        SettlementInfo info = new SettlementInfo();
        info.setUserId(fakeUserId);
        info.setEmploy(false);
        info.setCost(0.0);
        GoodsInfo goodsInfo01 = new GoodsInfo();
        goodsInfo01.setCount(2);
        goodsInfo01.setPrice(10.88);
        goodsInfo01.setType(GoodsType.CULTURAL.getCode());

        GoodsInfo goodsInfo02 = new GoodsInfo();
        // 达到满减标准
//        goodsInfo02.setCount(10);
        // 没有达到满减标准
        goodsInfo02.setCount(5);
        goodsInfo02.setPrice(20.88);
        goodsInfo02.setType(GoodsType.CULTURAL.getCode());

        info.setGoodsInfoList(Arrays.asList(goodsInfo01, goodsInfo02));
        // 满减优惠券
        SettlementInfo.CouponAndTemplateInfo fullInfo = new SettlementInfo.CouponAndTemplateInfo();
        fullInfo.setId(1);

        CouponTemplateSDK fullTemplateSDK = new CouponTemplateSDK();
        fullTemplateSDK.setId(1);
        fullTemplateSDK.setCategory(CouponCategory.FULL.getCode());
        fullTemplateSDK.setKey("100120240123");
        TemplateRule fullRule = new TemplateRule();
        fullRule.setDiscount(new TemplateRule.Discount(20, 199));
        fullRule.setUsage(new TemplateRule.Usage("北京市", "北京市", JSON.toJSONString(Arrays.asList(GoodsType.CULTURAL.getCode(), GoodsType.HOME.getCode()))));
        fullRule.setWeight(JSON.toJSONString(Collections.emptyList()));
        fullTemplateSDK.setRule(fullRule);
        fullInfo.setTemplate(fullTemplateSDK);

        // 折扣优惠券
        SettlementInfo.CouponAndTemplateInfo discountInfo = new SettlementInfo.CouponAndTemplateInfo();
        discountInfo.setId(1);

        CouponTemplateSDK discountTemplateSDK = new CouponTemplateSDK();
        discountTemplateSDK.setId(2);
        discountTemplateSDK.setCategory(CouponCategory.DISCOUNT.getCode());
        discountTemplateSDK.setKey("100120240123");
        TemplateRule discountRule = new TemplateRule();
        discountRule.setDiscount(new TemplateRule.Discount(85, 1));
        discountRule.setUsage(new TemplateRule.Usage("北京市", "北京市", JSON.toJSONString(Arrays.asList(GoodsType.CULTURAL.getCode(), GoodsType.HOME.getCode()))));
        discountTemplateSDK.setRule(discountRule);
        discountRule.setWeight(JSON.toJSONString(Collections.singletonList("1001202401238888")));
        discountTemplateSDK.setRule(discountRule);
        discountInfo.setTemplate(discountTemplateSDK);

        info.setCouponAndTemplateInfoList(Arrays.asList(fullInfo, discountInfo));
        return info;
    }


}
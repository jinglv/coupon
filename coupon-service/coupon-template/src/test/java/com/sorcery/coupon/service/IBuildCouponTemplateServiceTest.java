package com.sorcery.coupon.service;

import com.alibaba.fastjson.JSON;
import com.sorcery.coupon.BaseTest;
import com.sorcery.coupon.constant.CouponCategory;
import com.sorcery.coupon.constant.DistributeTarget;
import com.sorcery.coupon.constant.PeriodType;
import com.sorcery.coupon.constant.ProductLine;
import com.sorcery.coupon.vo.CouponTemplateRequestVO;
import com.sorcery.coupon.vo.TemplateRule;
import org.apache.commons.lang.time.DateUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;

/**
 * 构造优惠券模板服务测试
 *
 * @author jinglv
 * @date 2024/1/9 13:56
 */
public class IBuildCouponTemplateServiceTest extends BaseTest {

    @Autowired
    private IBuildCouponTemplateService buildCouponTemplateService;

    @Test
    public void testBuildCouponTemplate() throws Exception {
        System.out.println(JSON.toJSONString(buildCouponTemplateService.buildCouponTemplate(fakeCouponTemplateRequest())));
        // 异步执行，留够足够时间
        Thread.sleep(5000);
    }

    /**
     * fake CouponTemplateRequestVO
     *
     * @return CouponTemplateRequestVO
     */
    private CouponTemplateRequestVO fakeCouponTemplateRequest() {
        CouponTemplateRequestVO templateRequestVO = new CouponTemplateRequestVO();
        templateRequestVO.setName("优惠券模版-" + new Date().getTime());
        templateRequestVO.setLogo("http://www.baidu.com");
        templateRequestVO.setDesc("这是一张优惠券");
        templateRequestVO.setCategory(CouponCategory.FULL.getCode());
        templateRequestVO.setProductLine(ProductLine.BIG_CAT.getCode());
        templateRequestVO.setCount(10000);
        templateRequestVO.setUserId(10001L);
        templateRequestVO.setTarget(DistributeTarget.SINGLE.getCode());
        TemplateRule rule = new TemplateRule();
        rule.setExpiration(new TemplateRule.Expiration(
                PeriodType.SHIFT.getCode(), 1, DateUtils.addDays(new Date(), 60).getTime()
        ));
        rule.setDiscount(new TemplateRule.Discount(5, 1));
        rule.setLimitation(1);
        rule.setUsage(new TemplateRule.Usage("北京市", "北京市", JSON.toJSONString(Arrays.asList("文娱", "家居"))));
        rule.setWeight(JSON.toJSONString(Collections.EMPTY_LIST));
        templateRequestVO.setRule(rule);
        return templateRequestVO;
    }
}
package com.sorcery.coupon.controller;

import com.alibaba.fastjson.JSON;
import com.sorcery.coupon.entity.CouponTemplate;
import com.sorcery.coupon.exception.CouponException;
import com.sorcery.coupon.service.IBuildCouponTemplateService;
import com.sorcery.coupon.service.ICouponTemplateBaseService;
import com.sorcery.coupon.vo.CouponTemplateRequestVO;
import com.sorcery.coupon.vo.CouponTemplateSDK;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 优惠券模版相关的功能控制器
 *
 * @author jinglv
 * @date 2024/1/9 10:42
 */
@Slf4j
@RequiredArgsConstructor
@RestController
public class CouponTemplateController {
    /**
     * 构建优惠券模板服务
     */
    private final IBuildCouponTemplateService buildCouponTemplateService;
    /**
     * 优惠券模版基础服务
     */
    private final ICouponTemplateBaseService couponTemplateBaseService;

    /**
     * 构建优惠券模版
     * 127.0.0.1:7001/template/build
     * 网关转发访问地址：127.0.0.1:9005/sorcery/coupon-template/template/build
     *
     * @param couponTemplateRequestVO {@link CouponTemplateRequestVO} 构建优惠券模版请求信息
     * @return 接口返回信息
     * @throws CouponException 自定义异常
     */
    @PostMapping("/template/build")
    public CouponTemplate buildTemplate(@RequestBody CouponTemplateRequestVO couponTemplateRequestVO) throws CouponException {
        log.info("Build Template: {}", JSON.toJSONString(couponTemplateRequestVO));
        return buildCouponTemplateService.buildCouponTemplate(couponTemplateRequestVO);
    }

    /**
     * 构造优惠券模版详情
     * 127.0.0.1:7001/template/info/{id}
     *
     * @param id 模块id
     * @return 接口返回信息
     * @throws CouponException 自定义异常
     */
    @GetMapping("/template/info/{id}")
    public CouponTemplate buildTemplateInfo(@PathVariable Integer id) throws CouponException {
        log.info("Build Template Info For: {}", id);
        return couponTemplateBaseService.buildCouponTemplateInfo(id);
    }

    /**
     * 查找所有可用的优惠券模板
     *
     * @return 接口返回信息
     */
    @GetMapping("/template/sdk/all")
    public List<CouponTemplateSDK> findAllUsableTemplate() {
        log.info("Find All Usable Template.");
        return couponTemplateBaseService.findAllUsableCouponTemplate();
    }

    /**
     * 获取模板ids到CouponTemplateSDK的映射
     *
     * @param ids 模板id集合
     * @return 接口返回信息
     */
    @GetMapping("/template/sdk/infos/{ids}")
    public Map<Integer, CouponTemplateSDK> findIdsToTemplateSDK(@PathVariable Collection<Integer> ids) {
        log.info("FindIdsToTemplateSDK:{}", JSON.toJSONString(ids));
        return couponTemplateBaseService.findIdsToCouponTemplateSDK(ids);
    }
}

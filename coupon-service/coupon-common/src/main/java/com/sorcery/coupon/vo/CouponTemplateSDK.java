package com.sorcery.coupon.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 微服务之间用的优惠券模版信息定义
 *
 * @author jinglv
 * @date 2024/1/8 10:58
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CouponTemplateSDK {
    /**
     * 优惠券模块主键
     */
    private Integer id;
    /**
     * 优惠券名称
     */
    private String name;
    /**
     * 优惠券logo
     */
    private String logo;
    /**
     * 优惠券描述
     */
    private String desc;
    /**
     * 优惠券分类
     * category的code值
     */
    private String category;
    /**
     * 产品线
     */
    private Integer productLine;
    /**
     * 优惠券模版的编码
     */
    private String key;
    /**
     * 目标用户
     */
    private Integer target;
    /**
     * 优惠券规则
     */
    private TemplateRule rule;
}

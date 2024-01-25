package com.sorcery.coupon.entity;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.sorcery.coupon.constant.CouponCategory;
import com.sorcery.coupon.constant.DistributeTarget;
import com.sorcery.coupon.constant.ProductLine;
import com.sorcery.coupon.converter.CouponCategoryConverter;
import com.sorcery.coupon.converter.CouponDistributeTargetConverter;
import com.sorcery.coupon.converter.CouponProductLineConverter;
import com.sorcery.coupon.converter.CouponTemplateRuleConverter;
import com.sorcery.coupon.serialization.CouponTemplateSerialize;
import com.sorcery.coupon.vo.TemplateRule;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 优惠券模板实体类定义：基础属性和规则属性
 *
 * @author jinglv
 * @date 2024/1/5 10:46
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "coupon_template")
@JsonSerialize(using = CouponTemplateSerialize.class)
public class CouponTemplate implements Serializable {
    /**
     * 主键(自增)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;
    /**
     * 是否可用状态
     */
    @Column(name = "available", nullable = false)
    private Boolean available;
    /**
     * 是否过期
     */
    @Column(name = "expired", nullable = false)
    private Boolean expired;
    /**
     * 优惠券名称
     */
    @Column(name = "name", nullable = false)
    private String name;
    /**
     * 优惠券logo
     */
    @Column(name = "logo", nullable = false)
    private String logo;
    /**
     * 优惠券描述
     * desc是MySQL数据库的关键字
     */
    @Column(name = "intro", nullable = false)
    private String desc;
    /**
     * 优惠券分类
     */
    @Column(name = "category", nullable = false)
    @Convert(converter = CouponCategoryConverter.class)
    private CouponCategory category;
    /**
     * 产品线
     */
    @Column(name = "product_line", nullable = false)
    @Convert(converter = CouponProductLineConverter.class)
    private ProductLine productLine;
    /**
     * 总数
     */
    @Column(name = "coupon_count", nullable = false)
    private Integer count;
    /**
     * 创建时间
     * EntityListeners(AuditingEntityListener.class) 注解会自动创建时间
     */
    @CreatedDate
    @Column(name = "create_time", nullable = false)
    private Date createTime;
    /**
     * 创建用户
     */
    @Column(name = "user_id", nullable = false)
    private Long userId;
    /**
     * 优惠券模板创建编码
     * key是MySQL数据库的关键字
     */
    @Column(name = "template_key", nullable = false)
    private String key;
    /**
     * 目标用户
     */
    @Column(name = "target", nullable = false)
    @Convert(converter = CouponDistributeTargetConverter.class)
    private DistributeTarget target;
    /**
     * 优惠券规则
     */
    @Column(name = "rule", nullable = false)
    @Convert(converter = CouponTemplateRuleConverter.class)
    private TemplateRule rule;

    /**
     * 自定义构造函数
     *
     * @param name        优惠券名称
     * @param logo        优惠券logo
     * @param desc        优惠券描述
     * @param category    优惠券分类
     * @param productLine 产品线
     * @param count       总数
     * @param userId      创建用户
     * @param target      目标用户
     * @param rule        优惠券规则
     */
    public CouponTemplate(String name, String logo, String desc, String category, Integer productLine, Integer count, Long userId, Integer target, TemplateRule rule) {
        this.available = false;
        this.expired = false;
        this.name = name;
        this.logo = logo;
        this.desc = desc;
        this.category = CouponCategory.of(category);
        this.productLine = ProductLine.of(productLine);
        this.count = count;
        this.userId = userId;
        // 优惠券唯一编码 = 4(产品线和类型) + 8(日期：20240105) + id(扩充为4位) 注：id没有加时因为数据还未保存到数据库中还不知道Id是多少
        this.key = productLine.toString() + category + new SimpleDateFormat("yyyyMMdd").format(new Date());
        this.target = DistributeTarget.of(target);
        this.rule = rule;
    }
}

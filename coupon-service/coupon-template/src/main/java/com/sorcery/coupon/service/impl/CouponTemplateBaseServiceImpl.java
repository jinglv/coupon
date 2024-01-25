package com.sorcery.coupon.service.impl;

import com.sorcery.coupon.dao.CouponTemplateDAO;
import com.sorcery.coupon.entity.CouponTemplate;
import com.sorcery.coupon.exception.CouponException;
import com.sorcery.coupon.service.ICouponTemplateBaseService;
import com.sorcery.coupon.vo.CouponTemplateSDK;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 优惠券模版基础服务接口实现
 *
 * @author jinglv
 * @date 2024/1/9 09:56
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class CouponTemplateBaseServiceImpl implements ICouponTemplateBaseService {

    private final CouponTemplateDAO couponTemplateDAO;

    /**
     * 根据优惠模版 id 获取优惠券模版信息
     *
     * @param id 模版id
     * @return {@link CouponTemplate} 优惠券模板实体
     * @throws CouponException 自定义异常
     */
    @Override
    public CouponTemplate buildCouponTemplateInfo(Integer id) throws CouponException {
        Optional<CouponTemplate> template = couponTemplateDAO.findById(id);
        if (!template.isPresent()) {
            throw new CouponException("Template Is Not Exist:" + id);
        }
        return template.get();
    }

    /**
     * 查找所有可用的优惠券模版
     *
     * @return {@link CouponTemplateSDK}s 优惠券模板实体集合
     */
    @Override
    public List<CouponTemplateSDK> findAllUsableCouponTemplate() {
        // 查询优惠券可用状态且未过期的
        List<CouponTemplate> templates = couponTemplateDAO.findAllByAvailableAndExpired(true, false);
        return templates.stream().map(this::templateToTemplateSDK).collect(Collectors.toList());
    }

    /**
     * 获取模板ids到CouponTemplateSDK的映射
     *
     * @param ids 模板 ids
     * @return Map<key: 模版id, value: CouponTemplateSDK>
     */
    @Override
    public Map<Integer, CouponTemplateSDK> findIdsToCouponTemplateSDK(Collection<Integer> ids) {
        List<CouponTemplate> templates = couponTemplateDAO.findAllById(ids);
        return templates.stream().map(this::templateToTemplateSDK).collect(Collectors.toMap(
                CouponTemplateSDK::getId, Function.identity()
        ));
    }

    /**
     * 将CouponTemplate转换为CouponTemplateSDK
     *
     * @param couponTemplate {@link CouponTemplate} 优惠券模板实体
     * @return 优惠券模板实体
     */
    private CouponTemplateSDK templateToTemplateSDK(CouponTemplate couponTemplate) {
        return new CouponTemplateSDK(couponTemplate.getId(),
                couponTemplate.getName(),
                couponTemplate.getLogo(),
                couponTemplate.getDesc(),
                couponTemplate.getCategory().getCode(),
                couponTemplate.getProductLine().getCode(),
                // 并不是拼装好的Template key
                couponTemplate.getKey(),
                couponTemplate.getTarget().getCode(),
                couponTemplate.getRule());
    }
}

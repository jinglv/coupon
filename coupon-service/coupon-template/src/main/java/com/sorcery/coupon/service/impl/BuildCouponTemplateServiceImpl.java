package com.sorcery.coupon.service.impl;

import com.sorcery.coupon.dao.CouponTemplateDAO;
import com.sorcery.coupon.entity.CouponTemplate;
import com.sorcery.coupon.exception.CouponException;
import com.sorcery.coupon.service.IAsyncService;
import com.sorcery.coupon.service.IBuildCouponTemplateService;
import com.sorcery.coupon.vo.CouponTemplateRequestVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 构建优惠券模版接口实现
 *
 * @author jinglv
 * @date 2024/1/9 09:33
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class BuildCouponTemplateServiceImpl implements IBuildCouponTemplateService {
    /**
     * 异步服务
     */
    private final IAsyncService asyncService;
    /**
     * coupon template DAO
     */
    private final CouponTemplateDAO couponTemplateDAO;

    /**
     * 创建优惠券模版
     *
     * @param couponTemplateRequestVO {@link CouponTemplateRequestVO} 模板信息请求对象
     * @return {@link CouponTemplate} 优惠券模版实体
     * @throws CouponException 自定业务义异常
     */
    @Override
    public CouponTemplate buildCouponTemplate(CouponTemplateRequestVO couponTemplateRequestVO) throws CouponException {
        // 参数合法性校验
        if (!couponTemplateRequestVO.validate()) {
            throw new CouponException("BuildTemplate Param Is Not Valid!");
        }
        // 判断同名的优惠券是否存在
        if (null != couponTemplateDAO.findByName(couponTemplateRequestVO.getName())) {
            throw new CouponException("Exist Same Name Template!");
        }
        // 构造 CouponTemplate 并保存到数据库中
        CouponTemplate couponTemplate = this.requestToTemplate(couponTemplateRequestVO);
        couponTemplate = couponTemplateDAO.save(couponTemplate);
        // 根据优惠模板异步生成优惠券码
        asyncService.asyncConstructCouponByTemplate(couponTemplate);
        return couponTemplate;
    }

    /**
     * 将 CouponTemplateRequestVO 转为 CouponTemplate
     *
     * @param couponTemplateRequestVO {@link CouponTemplateRequestVO} 优惠券模板接口请求入参
     * @return 优惠券模板实体
     */
    private CouponTemplate requestToTemplate(CouponTemplateRequestVO couponTemplateRequestVO) {
        return new CouponTemplate(couponTemplateRequestVO.getName(),
                couponTemplateRequestVO.getLogo(),
                couponTemplateRequestVO.getDesc(),
                couponTemplateRequestVO.getCategory(),
                couponTemplateRequestVO.getProductLine(),
                couponTemplateRequestVO.getCount(),
                couponTemplateRequestVO.getUserId(),
                couponTemplateRequestVO.getTarget(),
                couponTemplateRequestVO.getRule());
    }
}

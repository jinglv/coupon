package com.sorcery.coupon.executor;

import com.sorcery.coupon.constant.CouponCategory;
import com.sorcery.coupon.constant.RuleFlag;
import com.sorcery.coupon.exception.CouponException;
import com.sorcery.coupon.vo.SettlementInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 优惠券结算规则执行管理器
 * 即根据用户的请求（SettlementInfo）找到对应的Executor，去做结算
 * BeanPostProcessor： Bean后置处理器
 *
 * @author jinglv
 * @date 2024/1/19 09:32
 */
@Slf4j
@Component
@SuppressWarnings("all")
public class ExecuteManager implements BeanPostProcessor {
    /**
     * 规则执行器映射
     */
    private static Map<RuleFlag, RuleExecutor> executorIndex = new HashMap<>(RuleFlag.values().length);

    /**
     * 优惠券结算规则计算入口
     * 注意：一定要保证传递进来的优惠券个数 >= 1
     *
     * @param settlementInfo
     * @return
     * @throws CouponException
     */
    public SettlementInfo computeRule(SettlementInfo settlementInfo) throws CouponException {
        SettlementInfo result = null;
        // 单类优惠券
        if (settlementInfo.getCouponAndTemplateInfoList().size() == 1) {
            // 获取优惠券的类别
            CouponCategory category = CouponCategory.of(settlementInfo.getCouponAndTemplateInfoList().get(0).getTemplate().getCategory());
            switch (category) {
                case FULL:
                    result = executorIndex.get(RuleFlag.FULL).computeRule(settlementInfo);
                    break;
                case DISCOUNT:
                    result = executorIndex.get(RuleFlag.DISCOUNT).computeRule(settlementInfo);
                    break;
                case INSTANT:
                    result = executorIndex.get(RuleFlag.INSTANT).computeRule(settlementInfo);
                    break;
            }
        } else {
            // 多类优惠券
            List<CouponCategory> couponCategoryList = new ArrayList<>(settlementInfo.getCouponAndTemplateInfoList().size());
            settlementInfo.getCouponAndTemplateInfoList().forEach(ct -> couponCategoryList.add(CouponCategory.of(ct.getTemplate().getCategory())));
            if (couponCategoryList.size() != 2) {
                throw new CouponException("Not Support For More Template Category");
            } else {
                if (couponCategoryList.contains(CouponCategory.FULL) && couponCategoryList.contains(CouponCategory.DISCOUNT)) {
                    result = executorIndex.get(RuleFlag.FULL_DISCOUNT).computeRule(settlementInfo);
                } else {
                    throw new CouponException("Not Support For Oyher Template Category");
                }
            }
        }
        return result;
    }

    /**
     * 在bean初始化之前去执行（before）
     *
     * @param bean
     * @param beanName
     * @return
     * @throws BeansException
     */
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (!(bean instanceof RuleExecutor)) {
            return bean;
        }
        RuleExecutor executor = (RuleExecutor) bean;
        RuleFlag ruleFlag = executor.ruleConfig();

        if (executorIndex.containsKey(ruleFlag)) {
            throw new IllegalStateException("There is already an executor for rule flag: " + ruleFlag);
        }
        log.info("Load execotr {} for rule flag {}", executor.getClass(), ruleFlag);
        executorIndex.put(ruleFlag, executor);
        return null;
    }

    /**
     * 在bean初始化之后去执行（after）
     *
     * @param bean
     * @param beanName
     * @return
     * @throws BeansException
     */
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }
}

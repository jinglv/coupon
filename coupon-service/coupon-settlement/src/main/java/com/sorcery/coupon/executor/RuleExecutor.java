package com.sorcery.coupon.executor;

import com.sorcery.coupon.constant.RuleFlag;
import com.sorcery.coupon.vo.SettlementInfo;

/**
 * 优惠券模版规则处理器接口定义
 *
 * @author jinglv
 * @date 2024/1/17 09:55
 */
public interface RuleExecutor {
    /**
     * 规则类型标记
     *
     * @return {@link RuleFlag}
     */
    RuleFlag ruleConfig();

    /**
     * 优惠券规则的计算
     *
     * @param settlementInfo {@link  SettlementInfo} 包含了选择的优惠券
     * @return {@link SettlementInfo} 修正过的结算信息
     */
    SettlementInfo computeRule(SettlementInfo settlementInfo);
}

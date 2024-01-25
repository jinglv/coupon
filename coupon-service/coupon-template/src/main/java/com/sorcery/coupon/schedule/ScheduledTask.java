package com.sorcery.coupon.schedule;

import com.sorcery.coupon.dao.CouponTemplateDAO;
import com.sorcery.coupon.entity.CouponTemplate;
import com.sorcery.coupon.vo.TemplateRule;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 定时请求已过期的优惠券模版
 *
 * @author jinglv
 * @date 2024/1/9 10:11
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class ScheduledTask {
    /**
     * coupon template DAO
     */
    private final CouponTemplateDAO couponTemplateDAO;

    /**
     * 下线已过期的优惠券模版
     */
    @Scheduled(fixedRate = 60 * 60 * 1000)
    public void offlineCouponTemplate() {
        log.info("Start To Expire CouponTemplate!");
        // 查询所有未过期的优惠券模版
        List<CouponTemplate> templates = couponTemplateDAO.findAllByExpired(false);
        if (CollectionUtils.isEmpty(templates)) {
            log.info("Done To Expire CouponTemplate!");
            return;
        }
        // 获取当前时间
        Date current = new Date();
        List<CouponTemplate> expiredTemplates = new ArrayList<>(templates.size());
        templates.forEach(t -> {
            // 根据优惠券模版规则中的“过期规则”校验模版是否过期
            TemplateRule rule = t.getRule();
            if (rule.getExpiration().getDeadLine() < current.getTime()) {
                t.setExpired(true);
                expiredTemplates.add(t);
            }
        });
        if (CollectionUtils.isNotEmpty(expiredTemplates)) {
            log.info("Expired CouponTemplate Num:{}", couponTemplateDAO.saveAll(expiredTemplates));
        }
        log.info("Done To Expire CouponTemplate!");
    }
}

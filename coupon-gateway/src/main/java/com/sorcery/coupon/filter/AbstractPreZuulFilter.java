package com.sorcery.coupon.filter;

import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;

/**
 * @author jinglv
 * @date 2024/1/3 09:40
 */
public abstract class AbstractPreZuulFilter extends AbstractZuulFilter {
    @Override
    public String filterType() {
        return FilterConstants.PRE_TYPE;
    }
}

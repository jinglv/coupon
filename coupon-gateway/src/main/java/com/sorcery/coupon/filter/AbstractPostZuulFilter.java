package com.sorcery.coupon.filter;

import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;

/**
 * @author jinglv
 * @date 2024/1/3 09:42
 */
public abstract class AbstractPostZuulFilter extends AbstractZuulFilter {
    @Override
    public String filterType() {
        return FilterConstants.POST_TYPE;
    }
}

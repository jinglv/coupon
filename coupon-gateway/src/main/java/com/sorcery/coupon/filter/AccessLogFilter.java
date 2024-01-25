package com.sorcery.coupon.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * @author jinglv
 * @date 2024/1/3 10:22
 */
@Slf4j
@Component
public class AccessLogFilter extends AbstractPostZuulFilter {
    @Override
    protected Object cRun() {
        // 获取当前请求对象
        HttpServletRequest request = requestContext.getRequest();
        // 从PreRequestFilter中获取设置的请求时间戳
        Long startTime = (Long) requestContext.get("startTime");
        String uri = request.getRequestURI();
        long duration = System.currentTimeMillis() - startTime;
        // 从网关通过的请求都会打印日志记录：uri + duration
        log.info("uri: {}, duration: {}", uri, duration);
        return success();
    }

    @Override
    public int filterOrder() {
        return FilterConstants.SEND_RESPONSE_FILTER_ORDER - 1;
    }
}

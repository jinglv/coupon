package com.sorcery.coupon.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 在过滤器中存储客户端发起请求的时间戳
 *
 * @author jinglv
 * @date 2024/1/3 10:19
 */
@Slf4j
@Component
public class PreRequestFilter extends AbstractPreZuulFilter {
    @Override
    protected Object cRun() {
        requestContext.set("startTime", System.currentTimeMillis());
        return success();
    }

    @Override
    public int filterOrder() {
        return 0;
    }
}

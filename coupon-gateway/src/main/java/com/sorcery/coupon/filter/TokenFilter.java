package com.sorcery.coupon.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * 校验请求中传递的Token
 *
 * @author jinglv
 * @date 2024/1/3 09:44
 */
@Slf4j
@Component
public class TokenFilter extends AbstractPreZuulFilter {
    @Override
    protected Object cRun() {
        // 获取当前请求对象
        HttpServletRequest request = requestContext.getRequest();
        log.info("{} request to {}", request.getMethod(), request.getRequestURL().toString());
        // 请求中获取token
        Object token = request.getParameter("token");
        if (null == token) {
            log.error("error: token is empty");
            return fail(401, "error: token is empty");
        }
        return success();
    }

    @Override
    public int filterOrder() {
        return 1;
    }
}

package com.sorcery.coupon.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;

/**
 * 通用的抽象过滤器类
 *
 * @author jinglv
 * @date 2024/1/3 09:17
 */
public abstract class AbstractZuulFilter extends ZuulFilter {
    /**
     * 用于在过滤器之间传递消息，数据保存在每个请求的，ThreadLocal中（线程安全）
     * 扩展了Map
     */
    RequestContext requestContext;
    private final static String NEXT = "next";

    @Override
    public boolean shouldFilter() {
        // 获取当前线程的requestContext
        RequestContext currentContext = RequestContext.getCurrentContext();
        return (boolean) currentContext.getOrDefault(NEXT, true);
    }

    @Override
    public Object run() throws ZuulException {
        requestContext = RequestContext.getCurrentContext();
        return cRun();
    }

    protected abstract Object cRun();

    Object fail(int code, String msg) {
        requestContext.set(NEXT, false);
        requestContext.setSendZuulResponse(false);
        requestContext.getResponse().setContentType("text/html;charset=UFT-8");
        requestContext.setResponseStatusCode(code);
        requestContext.setResponseBody(String.format("{\"result\": \"%s\"}", msg));
        return null;
    }

    Object success() {
        requestContext.set(NEXT, true);
        return null;
    }
}

package com.sorcery.coupon.advice;

import com.sorcery.coupon.annotation.IgnoreResponseAdvice;
import com.sorcery.coupon.vo.CommonResponse;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * 通用响应数据增强
 *
 * @author jinglv
 * @date 2024/1/3 11:12
 */
@RestControllerAdvice
public class CommonResponseDataAdvice implements ResponseBodyAdvice<Object> {
    /**
     * 判断是否需要响应进行处理
     *
     * @param methodParameter 当前请求方法的定义
     * @param aClass
     * @return
     */
    @Override
    @SuppressWarnings("all")
    public boolean supports(MethodParameter methodParameter, Class<? extends HttpMessageConverter<?>> aClass) {
        // 如果当前方法所在的类标识了@IgnoreResponseAdvice注解，则不需要处理
        if (methodParameter.getDeclaringClass().isAnnotationPresent(IgnoreResponseAdvice.class)) {
            return false;
        }
        // 如果当前方法标识了@IgnoreResponseAdvice注解，则不需要处理
        if (methodParameter.getMethod().isAnnotationPresent(IgnoreResponseAdvice.class)) {
            return false;
        }
        // 以上情况外，对响应进行处理，执行beforeBodyWrite方法
        return true;
    }


    /**
     * 响应返回之前的处理
     *
     * @param o
     * @param methodParameter
     * @param mediaType
     * @param aClass
     * @param serverHttpRequest
     * @param serverHttpResponse
     * @return
     */
    @Override
    @SuppressWarnings("all")
    public Object beforeBodyWrite(Object o,
                                  MethodParameter methodParameter,
                                  MediaType mediaType,
                                  Class aClass,
                                  ServerHttpRequest serverHttpRequest,
                                  ServerHttpResponse serverHttpResponse) {
        // 定义最终的返回对象
        CommonResponse<Object> response = new CommonResponse<>(0, "");
        // 如果o是null，response不不需要设置data
        if (null == o) {
            return response;
        } else if (o instanceof CommonResponse) {
            // 如果o已经是CommonResponse，不需要再次处理
            response = (CommonResponse<Object>) o;
        } else {
            // 否则，把响应对象作为CommonResponse的data部分
            response.setData(o);
        }
        return response;
    }

}

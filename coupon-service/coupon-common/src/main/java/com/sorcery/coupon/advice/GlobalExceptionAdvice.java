package com.sorcery.coupon.advice;

import com.sorcery.coupon.exception.CouponException;
import com.sorcery.coupon.vo.CommonResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

/**
 * 全局异常处理
 *
 * @author jinglv
 * @date 2024/1/3 14:03
 */
@RestControllerAdvice
public class GlobalExceptionAdvice {
    /**
     * 对CouponException异常进行统一处理
     *
     * @param request
     * @param ex
     * @return 统一响应结果
     */
    @ExceptionHandler(value = CouponException.class)
    public CommonResponse<String> handlerCouponException(HttpServletRequest request, CouponException ex) {
        CommonResponse<String> response = new CommonResponse<>(-1, "business error");
        response.setData(ex.getMessage());
        return response;
    }
}

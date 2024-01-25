package com.sorcery.coupon.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 通用响应对象定义
 *
 * @author jinglv
 * @date 2024/1/3 11:04
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommonResponse<T> implements Serializable {
    /**
     * 响应编码
     */
    private Integer code;
    /**
     * 响应信息
     */
    private String message;
    /**
     * 响应数据结果
     */
    private T data;

    public CommonResponse(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}

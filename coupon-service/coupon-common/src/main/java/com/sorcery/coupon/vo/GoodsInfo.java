package com.sorcery.coupon.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * fake 商品信息
 *
 * @author jinglv
 * @date 2024/1/10 11:00
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GoodsInfo {
    /**
     * 商品类型
     * {@link com.sorcery.coupon.constant.GoodsType}
     */
    private Integer type;
    /**
     * 商品价格
     */
    private Double price;
    /**
     * 商品数量
     */
    private Integer count;
    // TODO 名称，使用信息
}

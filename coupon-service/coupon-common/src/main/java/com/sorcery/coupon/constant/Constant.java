package com.sorcery.coupon.constant;

/**
 * 常用常量定义
 *
 * @author jinglv
 * @date 2024/1/8 11:13
 */
public class Constant {
    /**
     * 常量：用户优惠券操作 Kafka消息的topic
     */
    public static final String TOPIC = "user_coupon_op";

    /**
     * Redis Key前缀定义
     */
    public static class RedisPrefix {
        /**
         * 优惠券码key前缀
         */
        public static final String COUPON_TEMPLATE = "coupon_template_code_";
        /**
         * 用户当前所有可用的优惠券key前缀
         */
        public static final String USER_COUPON_USABLE = "user_coupon_usable_";
        /**
         * 用户当前所有已使用的优惠券key前缀
         */
        public static final String USER_COUPON_USED = "user_coupon_used_";
        /**
         * 用户当前所有已过期的优惠券key前缀
         */
        public static final String USER_COUPON_EXPIRED = "user_coupon_expired_";
    }
}

package com.sorcery.coupon.service;

import org.apache.kafka.clients.consumer.ConsumerRecord;

/**
 * Kafka相关服务接口定义
 *
 * @author jinglv
 * @date 2024/1/10 10:33
 */
public interface IKafkaService {
    /**
     * 消费优惠券 Kafka 消息
     *
     * @param record {@link ConsumerRecord}
     */
    void consumeCouponKafkaMessage(ConsumerRecord<?, ?> record);
}

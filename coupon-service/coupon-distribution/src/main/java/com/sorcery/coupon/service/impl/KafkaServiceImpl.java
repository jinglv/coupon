package com.sorcery.coupon.service.impl;

import com.alibaba.fastjson.JSON;
import com.sorcery.coupon.constant.Constant;
import com.sorcery.coupon.constant.CouponStatus;
import com.sorcery.coupon.dao.CouponDAO;
import com.sorcery.coupon.entity.Coupon;
import com.sorcery.coupon.service.IKafkaService;
import com.sorcery.coupon.vo.CouponKafkaMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Kafka相关的服务接口实现
 * 核心思想：是将Cache中的Coupon的状态变化同步到DB中
 *
 * @author jinglv
 * @date 2024/1/12 09:42
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class KafkaServiceImpl implements IKafkaService {

    private final CouponDAO couponDAO;

    /**
     * 消费优惠券 Kafka 消息
     *
     * @param record {@link ConsumerRecord}
     */
    @Override
    @KafkaListener(topics = {Constant.TOPIC}, groupId = "coupon-1")
    public void consumeCouponKafkaMessage(ConsumerRecord<?, ?> record) {
        Optional<?> kafkaMessage = Optional.ofNullable(record.value());
        if (kafkaMessage.isPresent()) {
            Object message = kafkaMessage.get();
            CouponKafkaMessage couponKafkaMessage = JSON.parseObject(String.valueOf(message), CouponKafkaMessage.class);
            log.info("Receive CouponKafkaMessage: {}", String.valueOf(message));
            CouponStatus status = CouponStatus.of(couponKafkaMessage.getStatus());
            switch (status) {
                case USABLE:
                    break;
                case USED:
                    processUsedCoupons(couponKafkaMessage, status);
                    break;
                case EXPIRED:
                    processExpiredCoupons(couponKafkaMessage, status);
                    break;
            }
        }
    }

    /**
     * 处理已使用的用户优惠券
     *
     * @param kafkaMessage 优惠券kafka信息
     * @param couponStatus 优惠券状态
     */
    private void processUsedCoupons(CouponKafkaMessage kafkaMessage, CouponStatus couponStatus) {
        // TODO 给用户发送消息
        processCouponsByStatus(kafkaMessage, couponStatus);
    }

    /**
     * 处理过期的用户优惠券
     *
     * @param kafkaMessage 优惠券kafka信息
     * @param couponStatus 优惠券状态
     */
    private void processExpiredCoupons(CouponKafkaMessage kafkaMessage, CouponStatus couponStatus) {
        // TODO 给用户发送推送
        processCouponsByStatus(kafkaMessage, couponStatus);
    }

    /**
     * 根据状态处理优惠券信息
     *
     * @param couponKafkaMessage 优惠券kafka信息
     * @param couponStatus       优惠券状态
     */
    private void processCouponsByStatus(CouponKafkaMessage couponKafkaMessage, CouponStatus couponStatus) {
        List<Coupon> coupons = couponDAO.findAllById(couponKafkaMessage.getIds());
        if (CollectionUtils.isEmpty(coupons) || coupons.size() != couponKafkaMessage.getIds().size()) {
            log.error("Can Not Find Right Coupon Info: {}", JSON.toJSONString(couponKafkaMessage));
            // TODO 发送邮件和短信至负责人
            return;
        }
        coupons.forEach(c -> c.setStatus(couponStatus));
        log.info("CouponKafkaMessage Op Coupon Count: {}", couponDAO.saveAll(coupons).size());
    }
}

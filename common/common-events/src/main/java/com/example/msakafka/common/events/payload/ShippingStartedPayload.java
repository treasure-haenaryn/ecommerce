package com.example.msakafka.common.events.payload;

/**
 * 배송 시작 시점의 비즈니스 데이터.
 */
public record ShippingStartedPayload(
        String orderId,
        String trackingNumber
) {
}

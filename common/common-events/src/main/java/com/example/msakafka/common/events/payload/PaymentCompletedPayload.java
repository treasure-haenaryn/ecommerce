package com.example.msakafka.common.events.payload;

/**
 * 결제 성공 시점의 비즈니스 데이터.
 */
public record PaymentCompletedPayload(
        String orderId,
        String paymentId
) {
}

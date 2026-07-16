package io.github.treasurehaenaryn.msa.common.events.payload;

/**
 * 결제 성공 시점의 비즈니스 데이터.
 */
public record PaymentCompletedPayload(
        String orderId,
        String paymentId
) {
}

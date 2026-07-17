package io.github.treasurehaenaryn.msa.common.events.payload;

/**
 * 결제 실패 시점의 비즈니스 데이터.
 * Order Service / Inventory Service가 구독하여 보상 트랜잭션을 수행.
 */
public record PaymentFailedPayload(
        String orderId,
        String reason
) {
}

package io.github.treasurehaenaryn.msa.common.events.payload;

/**
 * 결제 성공 시점의 비즈니스 데이터.
 * productId/quantity는 Inventory Service가 재고를 차감하는 데 필요해서, Order 단계에서 받은 정보를
 * Payment Service가 그대로 실어나른다 (Event-Carried State Transfer).
 */
public record PaymentCompletedPayload(
        String orderId,
        String paymentId,
        String productId,
        int quantity
) {
}

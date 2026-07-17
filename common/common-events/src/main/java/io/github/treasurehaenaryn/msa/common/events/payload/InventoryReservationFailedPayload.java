package io.github.treasurehaenaryn.msa.common.events.payload;

/**
 * 재고 예약 실패 시점의 비즈니스 데이터.
 * Order Service가 구독하여 주문 취소 보상 트랜잭션의 트리거로 사용.
 */
public record InventoryReservationFailedPayload(
        String orderId,
        String reason
) {
}

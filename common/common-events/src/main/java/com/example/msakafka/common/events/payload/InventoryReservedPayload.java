package com.example.msakafka.common.events.payload;

/**
 * 재고 예약(차감) 성공 시점의 비즈니스 데이터.
 */
public record InventoryReservedPayload(
        String orderId
) {
}

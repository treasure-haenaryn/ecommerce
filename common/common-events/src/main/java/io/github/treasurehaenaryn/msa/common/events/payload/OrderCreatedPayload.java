package io.github.treasurehaenaryn.msa.common.events.payload;

import java.math.BigDecimal;

/**
 * 주문 생성 시점의 비즈니스 데이터.
 *
 * @param orderId    주문 ID (Kafka 파티션 키로 사용 — 같은 주문의 이벤트는 순서 보장)
 * @param customerId 주문한 고객 ID
 * @param amount     주문 총액
 * @param productId  주문한 상품 ID
 * @param quantity   주문 수량
 */
public record OrderCreatedPayload(
        String orderId,
        String customerId,
        BigDecimal amount,
        String productId,
        int quantity
) {
}

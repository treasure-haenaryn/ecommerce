package io.github.treasurehaenaryn.msa.common.events.payload;

/**
 * 주문 처리 Saga가 정상 종료되었을 때의 비즈니스 데이터.
 * Notification Service가 구독하여 완료 알림을 발송.
 */
public record OrderCompletedPayload(
        String orderId
) {
}

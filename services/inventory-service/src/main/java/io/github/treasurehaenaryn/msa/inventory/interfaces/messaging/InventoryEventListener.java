package io.github.treasurehaenaryn.msa.inventory.interfaces.messaging;

import io.github.treasurehaenaryn.msa.common.events.EventEnvelope;
import io.github.treasurehaenaryn.msa.common.events.KafkaTopics;
import io.github.treasurehaenaryn.msa.common.events.payload.PaymentCompletedPayload;
import io.github.treasurehaenaryn.msa.inventory.application.InventoryService;
import io.opentelemetry.api.trace.Span;
import tools.jackson.databind.ObjectMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * 다른 서비스가 발행한 이벤트를 구독해서 재고 차감을 트리거하는 인바운드 어댑터.
 * 결제 성공 이후에만 재고를 건드리는 구조라 PaymentCompleted만 구독한다
 * (PaymentFailed 시점엔 애초에 재고를 안 건드렸으므로 보상 대상이 아님).
 */
@Component
public class InventoryEventListener {

    private final InventoryService inventoryService;
    private final ObjectMapper objectMapper;

    public InventoryEventListener(InventoryService inventoryService, ObjectMapper objectMapper) {
        this.inventoryService = inventoryService;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = KafkaTopics.PAYMENT_COMPLETED)
    public void onPaymentCompleted(EventEnvelope<?> envelope) {
        PaymentCompletedPayload payload = objectMapper.convertValue(envelope.payload(), PaymentCompletedPayload.class);
        Span.current().setAttribute("orderId", payload.orderId());
        inventoryService.handlePaymentCompleted(
                envelope.eventId(), payload.orderId(), payload.productId(), payload.quantity());
    }
}

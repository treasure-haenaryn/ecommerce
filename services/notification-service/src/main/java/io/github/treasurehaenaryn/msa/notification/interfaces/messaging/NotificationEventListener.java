package io.github.treasurehaenaryn.msa.notification.interfaces.messaging;

import io.github.treasurehaenaryn.msa.common.events.EventEnvelope;
import io.github.treasurehaenaryn.msa.common.events.KafkaTopics;
import io.github.treasurehaenaryn.msa.common.events.payload.InventoryReservationFailedPayload;
import io.github.treasurehaenaryn.msa.common.events.payload.OrderCompletedPayload;
import io.github.treasurehaenaryn.msa.common.events.payload.OrderCreatedPayload;
import io.github.treasurehaenaryn.msa.common.events.payload.PaymentFailedPayload;
import io.github.treasurehaenaryn.msa.notification.application.NotificationService;
import io.opentelemetry.api.trace.Span;
import tools.jackson.databind.ObjectMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * 여러 서비스가 발행한 이벤트를 구독해서 알림 발송을 트리거하는 인바운드 어댑터.
 */
@Component
public class NotificationEventListener {

    private final NotificationService notificationService;
    private final ObjectMapper objectMapper;

    public NotificationEventListener(NotificationService notificationService, ObjectMapper objectMapper) {
        this.notificationService = notificationService;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = KafkaTopics.ORDER_CREATED)
    public void onOrderCreated(EventEnvelope<?> envelope) {
        OrderCreatedPayload payload = objectMapper.convertValue(envelope.payload(), OrderCreatedPayload.class);
        Span.current().setAttribute("orderId", payload.orderId());
        notificationService.notifyOrderCreated(envelope.eventId(), payload.orderId());
    }

    @KafkaListener(topics = KafkaTopics.PAYMENT_FAILED)
    public void onPaymentFailed(EventEnvelope<?> envelope) {
        PaymentFailedPayload payload = objectMapper.convertValue(envelope.payload(), PaymentFailedPayload.class);
        Span.current().setAttribute("orderId", payload.orderId());
        notificationService.notifyPaymentFailed(envelope.eventId(), payload.orderId(), payload.reason());
    }

    @KafkaListener(topics = KafkaTopics.INVENTORY_RESERVATION_FAILED)
    public void onInventoryReservationFailed(EventEnvelope<?> envelope) {
        InventoryReservationFailedPayload payload =
                objectMapper.convertValue(envelope.payload(), InventoryReservationFailedPayload.class);
        Span.current().setAttribute("orderId", payload.orderId());
        notificationService.notifyInventoryReservationFailed(envelope.eventId(), payload.orderId(), payload.reason());
    }

    @KafkaListener(topics = KafkaTopics.ORDER_COMPLETED)
    public void onOrderCompleted(EventEnvelope<?> envelope) {
        OrderCompletedPayload payload = objectMapper.convertValue(envelope.payload(), OrderCompletedPayload.class);
        Span.current().setAttribute("orderId", payload.orderId());
        notificationService.notifyOrderCompleted(envelope.eventId(), payload.orderId());
    }
}

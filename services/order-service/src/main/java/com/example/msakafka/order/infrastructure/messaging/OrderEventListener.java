package com.example.msakafka.order.infrastructure.messaging;

import com.example.msakafka.common.events.EventEnvelope;
import com.example.msakafka.common.events.KafkaTopics;
import com.example.msakafka.common.events.payload.InventoryReservationFailedPayload;
import com.example.msakafka.common.events.payload.PaymentFailedPayload;
import com.example.msakafka.common.events.payload.ShippingStartedPayload;
import com.example.msakafka.order.application.OrderService;
import tools.jackson.databind.ObjectMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * 다른 서비스가 발행한 이벤트를 구독해서 주문 상태를 전이시키는 컨슈머.
 * EventEnvelope는 제네릭 타입 정보가 런타임에 지워지므로, payload를 Map으로 받은 뒤
 * ObjectMapper#convertValue로 구체 타입으로 변환한다.
 */
@Component
public class OrderEventListener {

    private final OrderService orderService;
    private final ObjectMapper objectMapper;

    public OrderEventListener(OrderService orderService, ObjectMapper objectMapper) {
        this.orderService = orderService;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = KafkaTopics.PAYMENT_FAILED)
    public void onPaymentFailed(EventEnvelope<?> envelope) {
        PaymentFailedPayload payload = objectMapper.convertValue(envelope.payload(), PaymentFailedPayload.class);
        orderService.handlePaymentFailed(envelope.eventId(), payload.orderId());
    }

    @KafkaListener(topics = KafkaTopics.INVENTORY_RESERVATION_FAILED)
    public void onInventoryReservationFailed(EventEnvelope<?> envelope) {
        InventoryReservationFailedPayload payload =
                objectMapper.convertValue(envelope.payload(), InventoryReservationFailedPayload.class);
        orderService.handleInventoryReservationFailed(envelope.eventId(), payload.orderId());
    }

    @KafkaListener(topics = KafkaTopics.SHIPPING_STARTED)
    public void onShippingStarted(EventEnvelope<?> envelope) {
        ShippingStartedPayload payload = objectMapper.convertValue(envelope.payload(), ShippingStartedPayload.class);
        orderService.handleShippingStarted(envelope.eventId(), payload.orderId());
    }
}

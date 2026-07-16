package io.github.treasurehaenaryn.msa.shipping.interfaces.messaging;

import io.github.treasurehaenaryn.msa.common.events.EventEnvelope;
import io.github.treasurehaenaryn.msa.common.events.KafkaTopics;
import io.github.treasurehaenaryn.msa.common.events.payload.InventoryReservedPayload;
import io.github.treasurehaenaryn.msa.shipping.application.ShippingService;
import tools.jackson.databind.ObjectMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * 다른 서비스가 발행한 이벤트를 구독해서 배송 시작을 트리거하는 인바운드 어댑터.
 */
@Component
public class ShippingEventListener {

    private final ShippingService shippingService;
    private final ObjectMapper objectMapper;

    public ShippingEventListener(ShippingService shippingService, ObjectMapper objectMapper) {
        this.shippingService = shippingService;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = KafkaTopics.INVENTORY_RESERVED)
    public void onInventoryReserved(EventEnvelope<?> envelope) {
        InventoryReservedPayload payload = objectMapper.convertValue(envelope.payload(), InventoryReservedPayload.class);
        shippingService.handleInventoryReserved(envelope.eventId(), payload.orderId());
    }
}

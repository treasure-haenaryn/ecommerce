package io.github.treasurehaenaryn.msa.shipping.application;

import io.github.treasurehaenaryn.msa.common.events.EventTypes;
import io.github.treasurehaenaryn.msa.common.events.payload.ShippingStartedPayload;
import io.github.treasurehaenaryn.msa.shipping.domain.Shipment;
import io.github.treasurehaenaryn.msa.shipping.infrastructure.persistence.OutboxEvent;
import io.github.treasurehaenaryn.msa.shipping.infrastructure.persistence.OutboxEventRepository;
import io.github.treasurehaenaryn.msa.shipping.infrastructure.persistence.ProcessedEvent;
import io.github.treasurehaenaryn.msa.shipping.infrastructure.persistence.ProcessedEventRepository;
import io.github.treasurehaenaryn.msa.shipping.infrastructure.persistence.ShipmentRepository;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * InventoryReserved 이벤트를 받아 배송을 시작하는 애플리케이션 서비스.
 * 배송은 재고처럼 "부족"이라는 실패 개념이 없어서, 받으면 항상 성공 처리한다.
 */
@Service
public class ShippingService {

    private final ShipmentRepository shipmentRepository;
    private final OutboxEventRepository outboxEventRepository;
    private final ProcessedEventRepository processedEventRepository;
    private final ObjectMapper objectMapper;

    public ShippingService(ShipmentRepository shipmentRepository,
                            OutboxEventRepository outboxEventRepository,
                            ProcessedEventRepository processedEventRepository,
                            ObjectMapper objectMapper) {
        this.shipmentRepository = shipmentRepository;
        this.outboxEventRepository = outboxEventRepository;
        this.processedEventRepository = processedEventRepository;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public void handleInventoryReserved(String eventId, String orderId) {
        if (processedEventRepository.existsById(eventId)) {
            return;
        }

        Shipment shipment = Shipment.start(orderId);
        shipmentRepository.save(shipment);

        saveOutboxEvent("Shipment", orderId, EventTypes.SHIPPING_STARTED,
                new ShippingStartedPayload(orderId, shipment.getTrackingNumber()));

        processedEventRepository.save(new ProcessedEvent(eventId));
    }

    private void saveOutboxEvent(String aggregateType, String aggregateId, String eventType, Object payload) {
        try {
            String payloadJson = objectMapper.writeValueAsString(payload);
            outboxEventRepository.save(OutboxEvent.create(aggregateType, aggregateId, eventType, payloadJson));
        } catch (JacksonException e) {
            throw new IllegalStateException("Outbox payload 직렬화 실패: " + eventType, e);
        }
    }
}

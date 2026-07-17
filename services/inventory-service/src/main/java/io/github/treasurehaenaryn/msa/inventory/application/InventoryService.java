package io.github.treasurehaenaryn.msa.inventory.application;

import io.github.treasurehaenaryn.msa.common.events.EventTypes;
import io.github.treasurehaenaryn.msa.common.events.payload.InventoryReservationFailedPayload;
import io.github.treasurehaenaryn.msa.common.events.payload.InventoryReservedPayload;
import io.github.treasurehaenaryn.msa.inventory.domain.Product;
import io.github.treasurehaenaryn.msa.inventory.infrastructure.persistence.OutboxEvent;
import io.github.treasurehaenaryn.msa.inventory.infrastructure.persistence.OutboxEventRepository;
import io.github.treasurehaenaryn.msa.inventory.infrastructure.persistence.ProcessedEvent;
import io.github.treasurehaenaryn.msa.inventory.infrastructure.persistence.ProcessedEventRepository;
import io.github.treasurehaenaryn.msa.inventory.infrastructure.persistence.ProductRepository;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * PaymentCompleted 이벤트를 받아 재고 차감을 수행하는 애플리케이션 서비스.
 * 재고 부족은 비즈니스 실패(InventoryReservationFailed)로 처리하고,
 * 동시 수정 충돌(OptimisticLockingFailureException)은 그대로 던져서
 * Kafka 컨슈머의 재시도 정책(1초 간격 3회 후 DLT)이 자동으로 재처리하게 한다.
 */
@Service
public class InventoryService {

    private final ProductRepository productRepository;
    private final OutboxEventRepository outboxEventRepository;
    private final ProcessedEventRepository processedEventRepository;
    private final ObjectMapper objectMapper;

    public InventoryService(ProductRepository productRepository,
                             OutboxEventRepository outboxEventRepository,
                             ProcessedEventRepository processedEventRepository,
                             ObjectMapper objectMapper) {
        this.productRepository = productRepository;
        this.outboxEventRepository = outboxEventRepository;
        this.processedEventRepository = processedEventRepository;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public void handlePaymentCompleted(String eventId, String orderId, String productId, int quantity) {
        if (processedEventRepository.existsById(eventId)) {
            return;
        }

        Product product = productRepository.findById(productId).orElse(null);

        if (product == null || !product.hasEnoughStock(quantity)) {
            String reason = product == null ? "상품을 찾을 수 없음: " + productId : "재고 부족: " + productId;
            saveOutboxEvent("Inventory", orderId, EventTypes.INVENTORY_RESERVATION_FAILED,
                    new InventoryReservationFailedPayload(orderId, reason));
        } else {
            product.decreaseStock(quantity);
            productRepository.save(product);
            saveOutboxEvent("Inventory", orderId, EventTypes.INVENTORY_RESERVED,
                    new InventoryReservedPayload(orderId));
        }

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

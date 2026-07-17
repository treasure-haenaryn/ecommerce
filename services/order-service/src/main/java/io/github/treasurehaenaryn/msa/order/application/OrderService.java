package io.github.treasurehaenaryn.msa.order.application;

import io.github.treasurehaenaryn.msa.common.events.EventTypes;
import io.github.treasurehaenaryn.msa.common.events.payload.OrderCompletedPayload;
import io.github.treasurehaenaryn.msa.common.events.payload.OrderCreatedPayload;
import io.github.treasurehaenaryn.msa.order.domain.Order;
import io.github.treasurehaenaryn.msa.order.infrastructure.persistence.OrderRepository;
import io.github.treasurehaenaryn.msa.order.infrastructure.persistence.OutboxEvent;
import io.github.treasurehaenaryn.msa.order.infrastructure.persistence.OutboxEventRepository;
import io.github.treasurehaenaryn.msa.order.infrastructure.persistence.ProcessedEvent;
import io.github.treasurehaenaryn.msa.order.infrastructure.persistence.ProcessedEventRepository;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/**
 * 주문 생성, 그리고 Saga 진행에 따른 주문 상태 전이를 담당하는 애플리케이션 서비스.
 */
@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OutboxEventRepository outboxEventRepository;
    private final ProcessedEventRepository processedEventRepository;
    private final ObjectMapper objectMapper;

    public OrderService(OrderRepository orderRepository,
                         OutboxEventRepository outboxEventRepository,
                         ProcessedEventRepository processedEventRepository,
                         ObjectMapper objectMapper) {
        this.orderRepository = orderRepository;
        this.outboxEventRepository = outboxEventRepository;
        this.processedEventRepository = processedEventRepository;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public Order createOrder(String customerId, String productId, int quantity, BigDecimal amount) {
        Order order = Order.create(customerId, productId, quantity, amount);
        orderRepository.save(order);

        OrderCreatedPayload payload =
                new OrderCreatedPayload(order.getId(), customerId, amount, productId, quantity);
        saveOutboxEvent("Order", order.getId(), EventTypes.ORDER_CREATED, payload);

        return order;
    }

    @Transactional
    public void handlePaymentFailed(String eventId, String orderId) {
        if (alreadyProcessed(eventId)) {
            return;
        }
        orderRepository.findById(orderId).ifPresent(order -> {
            order.cancel();
            orderRepository.save(order);
        });
        markProcessed(eventId);
    }

    @Transactional
    public void handleInventoryReservationFailed(String eventId, String orderId) {
        if (alreadyProcessed(eventId)) {
            return;
        }
        orderRepository.findById(orderId).ifPresent(order -> {
            order.cancel();
            orderRepository.save(order);
        });
        markProcessed(eventId);
    }

    @Transactional
    public void handleShippingStarted(String eventId, String orderId) {
        if (alreadyProcessed(eventId)) {
            return;
        }
        orderRepository.findById(orderId).ifPresent(order -> {
            order.complete();
            orderRepository.save(order);
            saveOutboxEvent("Order", order.getId(), EventTypes.ORDER_COMPLETED, new OrderCompletedPayload(order.getId()));
        });
        markProcessed(eventId);
    }

    private boolean alreadyProcessed(String eventId) {
        return processedEventRepository.existsById(eventId);
    }

    private void markProcessed(String eventId) {
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

package io.github.treasurehaenaryn.msa.payment.application;

import io.github.treasurehaenaryn.msa.common.events.EventTypes;
import io.github.treasurehaenaryn.msa.common.events.payload.PaymentCompletedPayload;
import io.github.treasurehaenaryn.msa.common.events.payload.PaymentFailedPayload;
import io.github.treasurehaenaryn.msa.payment.domain.Payment;
import io.github.treasurehaenaryn.msa.payment.infrastructure.persistence.OutboxEvent;
import io.github.treasurehaenaryn.msa.payment.infrastructure.persistence.OutboxEventRepository;
import io.github.treasurehaenaryn.msa.payment.infrastructure.persistence.PaymentRepository;
import io.github.treasurehaenaryn.msa.payment.infrastructure.persistence.ProcessedEvent;
import io.github.treasurehaenaryn.msa.payment.infrastructure.persistence.ProcessedEventRepository;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/**
 * OrderCreated 이벤트를 받아 결제 처리를 수행하는 애플리케이션 서비스.
 */
@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OutboxEventRepository outboxEventRepository;
    private final ProcessedEventRepository processedEventRepository;
    private final ObjectMapper objectMapper;

    public PaymentService(PaymentRepository paymentRepository,
                           OutboxEventRepository outboxEventRepository,
                           ProcessedEventRepository processedEventRepository,
                           ObjectMapper objectMapper) {
        this.paymentRepository = paymentRepository;
        this.outboxEventRepository = outboxEventRepository;
        this.processedEventRepository = processedEventRepository;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public void handleOrderCreated(String eventId, String orderId, BigDecimal amount, String productId, int quantity) {
        if (processedEventRepository.existsById(eventId)) {
            return;
        }

        Payment payment = Payment.process(orderId, amount);
        paymentRepository.save(payment);

        if (payment.isFailed()) {
            saveOutboxEvent("Payment", orderId, EventTypes.PAYMENT_FAILED,
                    new PaymentFailedPayload(orderId, payment.getFailureReason()));
        } else {
            // productId/quantity는 Inventory Service가 재고 차감에 필요해서 그대로 실어나른다.
            saveOutboxEvent("Payment", orderId, EventTypes.PAYMENT_COMPLETED,
                    new PaymentCompletedPayload(orderId, payment.getId(), productId, quantity));
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

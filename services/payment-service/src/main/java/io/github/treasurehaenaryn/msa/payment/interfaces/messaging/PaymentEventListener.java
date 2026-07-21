package io.github.treasurehaenaryn.msa.payment.interfaces.messaging;

import io.github.treasurehaenaryn.msa.common.events.EventEnvelope;
import io.github.treasurehaenaryn.msa.common.events.KafkaTopics;
import io.github.treasurehaenaryn.msa.common.events.payload.OrderCreatedPayload;
import io.github.treasurehaenaryn.msa.payment.application.PaymentService;
import io.opentelemetry.api.trace.Span;
import tools.jackson.databind.ObjectMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * 다른 서비스가 발행한 이벤트를 구독해서 결제 처리를 트리거하는 인바운드 어댑터.
 */
@Component
public class PaymentEventListener {

    private final PaymentService paymentService;
    private final ObjectMapper objectMapper;

    public PaymentEventListener(PaymentService paymentService, ObjectMapper objectMapper) {
        this.paymentService = paymentService;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = KafkaTopics.ORDER_CREATED)
    public void onOrderCreated(EventEnvelope<?> envelope) {
        OrderCreatedPayload payload = objectMapper.convertValue(envelope.payload(), OrderCreatedPayload.class);
        Span.current().setAttribute("orderId", payload.orderId());
        paymentService.handleOrderCreated(
                envelope.eventId(), payload.orderId(), payload.amount(), payload.productId(), payload.quantity());
    }
}

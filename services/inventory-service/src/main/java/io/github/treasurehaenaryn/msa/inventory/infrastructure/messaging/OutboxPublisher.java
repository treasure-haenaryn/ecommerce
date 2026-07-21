package io.github.treasurehaenaryn.msa.inventory.infrastructure.messaging;

import io.github.treasurehaenaryn.msa.common.events.EventEnvelope;
import io.github.treasurehaenaryn.msa.common.events.TopicResolver;
import io.github.treasurehaenaryn.msa.common.kafka.TraceContextCarrier;
import io.github.treasurehaenaryn.msa.inventory.infrastructure.persistence.OutboxEvent;
import io.github.treasurehaenaryn.msa.inventory.infrastructure.persistence.OutboxEventRepository;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.api.trace.StatusCode;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.Scope;
import org.apache.kafka.clients.producer.ProducerRecord;
import tools.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Outbox 테이블을 폴링해서 미발행 이벤트를 Kafka로 발행하는 아웃바운드 어댑터.
 */
@Component
public class OutboxPublisher {

    private static final Logger log = LoggerFactory.getLogger(OutboxPublisher.class);

    private final OutboxEventRepository outboxEventRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final OutboxEventUpdater outboxEventUpdater;
    private final TraceContextCarrier traceContextCarrier;

    public OutboxPublisher(OutboxEventRepository outboxEventRepository,
                            KafkaTemplate<String, Object> kafkaTemplate,
                            ObjectMapper objectMapper,
                            OutboxEventUpdater outboxEventUpdater,
                            TraceContextCarrier traceContextCarrier) {
        this.outboxEventRepository = outboxEventRepository;
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
        this.outboxEventUpdater = outboxEventUpdater;
        this.traceContextCarrier = traceContextCarrier;
    }

    @Scheduled(fixedDelay = 3000)
    @Transactional(readOnly = true)
    public void publishPendingEvents() {
        List<OutboxEvent> pending = outboxEventRepository.findByPublishedFalseOrderByCreatedAtAsc();
        for (OutboxEvent event : pending) {
            publishOne(event);
        }
    }

    private void publishOne(OutboxEvent event) {
        String eventId = event.getId();
        String eventType = event.getEventType();
        try {
            Object payload = objectMapper.readValue(event.getPayload(), Object.class);
            EventEnvelope<Object> envelope = EventEnvelope.of(eventId, eventType, payload);
            String topic = TopicResolver.resolve(eventType);

            Context parentContext = traceContextCarrier.extractParentContext(event.getTraceparent());
            Span producerSpan = traceContextCarrier.tracer("outbox-publisher")
                    .spanBuilder(topic + " send")
                    .setParent(parentContext)
                    .setSpanKind(SpanKind.PRODUCER)
                    .startSpan();

            ProducerRecord<String, Object> record = new ProducerRecord<>(topic, event.getAggregateId(), envelope);
            try (Scope scope = producerSpan.makeCurrent()) {
                traceContextCarrier.injectContext(Context.current(), record.headers());
            }

            kafkaTemplate.send(record).whenComplete((result, ex) -> {
                if (ex != null) {
                    producerSpan.recordException(ex);
                    producerSpan.setStatus(StatusCode.ERROR);
                    producerSpan.end();
                    log.error("Outbox 이벤트 발행 실패: id={}, eventType={}", eventId, eventType, ex);
                    return;
                }
                producerSpan.setStatus(StatusCode.OK);
                producerSpan.end();
                outboxEventUpdater.markPublished(eventId);
            });
        } catch (Exception e) {
            log.error("Outbox 이벤트 발행 준비 실패: id={}, eventType={}", eventId, eventType, e);
        }
    }
}

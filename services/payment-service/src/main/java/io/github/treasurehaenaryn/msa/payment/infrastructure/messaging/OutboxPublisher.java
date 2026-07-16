package io.github.treasurehaenaryn.msa.payment.infrastructure.messaging;

import io.github.treasurehaenaryn.msa.common.events.EventEnvelope;
import io.github.treasurehaenaryn.msa.common.events.TopicResolver;
import io.github.treasurehaenaryn.msa.payment.infrastructure.persistence.OutboxEvent;
import io.github.treasurehaenaryn.msa.payment.infrastructure.persistence.OutboxEventRepository;
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
 * send()는 비동기이므로, 발행 성공 콜백에서만 published 처리를 확정한다 (블로킹 대기 없음).
 */
@Component
public class OutboxPublisher {

    private static final Logger log = LoggerFactory.getLogger(OutboxPublisher.class);

    private final OutboxEventRepository outboxEventRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final OutboxEventUpdater outboxEventUpdater;

    public OutboxPublisher(OutboxEventRepository outboxEventRepository,
                            KafkaTemplate<String, Object> kafkaTemplate,
                            ObjectMapper objectMapper,
                            OutboxEventUpdater outboxEventUpdater) {
        this.outboxEventRepository = outboxEventRepository;
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
        this.outboxEventUpdater = outboxEventUpdater;
    }

    @Scheduled(fixedDelay = 1000)
    @Transactional(readOnly = true)
    public void publishPendingEvents() {
        List<OutboxEvent> pending = outboxEventRepository.findByPublishedFalseOrderByCreatedAtAsc();
        for (OutboxEvent event : pending) {
            try {
                Object payload = objectMapper.readValue(event.getPayload(), Object.class);
                EventEnvelope<Object> envelope = EventEnvelope.of(event.getId(), event.getEventType(), payload);
                String topic = TopicResolver.resolve(event.getEventType());
                String eventId = event.getId();
                String eventType = event.getEventType();

                kafkaTemplate.send(topic, event.getAggregateId(), envelope).whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Outbox 이벤트 발행 실패: id={}, eventType={}", eventId, eventType, ex);
                        return;
                    }
                    outboxEventUpdater.markPublished(eventId);
                });
            } catch (Exception e) {
                log.error("Outbox 이벤트 발행 준비 실패: id={}, eventType={}", event.getId(), event.getEventType(), e);
            }
        }
    }
}

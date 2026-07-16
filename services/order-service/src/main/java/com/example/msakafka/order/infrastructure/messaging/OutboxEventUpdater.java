package com.example.msakafka.order.infrastructure.messaging;

import com.example.msakafka.order.infrastructure.persistence.OutboxEventRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * OutboxEvent의 발행 완료 표시를 별도 트랜잭션으로 처리하는 컴포넌트.
 * OutboxPublisher의 Kafka 콜백 스레드에서 호출되므로, 같은 클래스 내부 self-invocation으로
 * @Transactional이 무시되는 걸 피하기 위해 별도 빈으로 분리했다.
 */
@Component
public class OutboxEventUpdater {

    private final OutboxEventRepository outboxEventRepository;

    public OutboxEventUpdater(OutboxEventRepository outboxEventRepository) {
        this.outboxEventRepository = outboxEventRepository;
    }

    @Transactional
    public void markPublished(String eventId) {
        outboxEventRepository.findById(eventId).ifPresent(event -> {
            event.markPublished();
            outboxEventRepository.save(event);
        });
    }
}

package io.github.treasurehaenaryn.msa.shipping.infrastructure.messaging;

import io.github.treasurehaenaryn.msa.shipping.infrastructure.persistence.OutboxEventRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * OutboxEvent의 발행 완료 표시를 별도 트랜잭션으로 처리하는 컴포넌트.
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

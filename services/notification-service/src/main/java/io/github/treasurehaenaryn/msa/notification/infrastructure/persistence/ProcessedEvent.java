package io.github.treasurehaenaryn.msa.notification.infrastructure.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;

/**
 * 멱등성 처리를 위해 이미 처리한 이벤트 ID를 기록하는 엔티티.
 * 이 서비스는 이벤트 종류가 여러 개(OrderCreated 등 4종)라도 eventId 자체는 전역에서 유일하므로
 * 테이블 하나로 전부 공유해서 관리한다.
 */
@Entity
@Table(name = "processed_event")
public class ProcessedEvent {

    @Id
    private String eventId;

    @Column(nullable = false)
    private Instant processedAt;

    protected ProcessedEvent() {
    }

    public ProcessedEvent(String eventId) {
        this.eventId = eventId;
        this.processedAt = Instant.now();
    }

    public String getEventId() {
        return eventId;
    }

    public Instant getProcessedAt() {
        return processedAt;
    }
}

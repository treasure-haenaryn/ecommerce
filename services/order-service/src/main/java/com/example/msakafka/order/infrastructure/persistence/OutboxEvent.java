package com.example.msakafka.order.infrastructure.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

/**
 * Transactional Outbox 레코드. 도메인 테이블과 같은 트랜잭션으로 저장되고,
 * OutboxPublisher가 별도로 폴링해서 Kafka로 발행한다.
 */
@Entity
@Table(name = "outbox_event")
public class OutboxEvent {

    @Id
    private String id;

    @Column(nullable = false)
    private String aggregateType;

    @Column(nullable = false)
    private String aggregateId;

    @Column(nullable = false)
    private String eventType;

    @Lob
    @Column(nullable = false)
    private String payload;

    @Column(nullable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private boolean published;

    private Instant publishedAt;

    protected OutboxEvent() {
    }

    private OutboxEvent(String id, String aggregateType, String aggregateId, String eventType, String payload) {
        this.id = id;
        this.aggregateType = aggregateType;
        this.aggregateId = aggregateId;
        this.eventType = eventType;
        this.payload = payload;
        this.createdAt = Instant.now();
        this.published = false;
    }

    public static OutboxEvent create(String aggregateType, String aggregateId, String eventType, String payload) {
        return new OutboxEvent(UUID.randomUUID().toString(), aggregateType, aggregateId, eventType, payload);
    }

    public void markPublished() {
        this.published = true;
        this.publishedAt = Instant.now();
    }

    public String getId() {
        return id;
    }

    public String getAggregateId() {
        return aggregateId;
    }

    public String getEventType() {
        return eventType;
    }

    public String getPayload() {
        return payload;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public boolean isPublished() {
        return published;
    }
}

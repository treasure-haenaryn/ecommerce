package com.example.msakafka.order.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * ProcessedEvent 저장소.
 */
public interface ProcessedEventRepository extends JpaRepository<ProcessedEvent, String> {
}

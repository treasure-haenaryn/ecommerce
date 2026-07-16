package io.github.treasurehaenaryn.msa.payment.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * ProcessedEvent 저장소.
 */
public interface ProcessedEventRepository extends JpaRepository<ProcessedEvent, String> {
}

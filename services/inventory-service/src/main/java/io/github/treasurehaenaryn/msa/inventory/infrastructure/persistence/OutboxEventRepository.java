package io.github.treasurehaenaryn.msa.inventory.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * OutboxEvent 저장소.
 */
public interface OutboxEventRepository extends JpaRepository<OutboxEvent, String> {

    List<OutboxEvent> findByPublishedFalseOrderByCreatedAtAsc();
}

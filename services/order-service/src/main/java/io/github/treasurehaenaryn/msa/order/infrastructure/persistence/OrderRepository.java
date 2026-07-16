package io.github.treasurehaenaryn.msa.order.infrastructure.persistence;

import io.github.treasurehaenaryn.msa.order.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Order 엔티티 저장소.
 */
public interface OrderRepository extends JpaRepository<Order, String> {
}

package com.example.msakafka.order.infrastructure.persistence;

import com.example.msakafka.order.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Order 엔티티 저장소.
 */
public interface OrderRepository extends JpaRepository<Order, String> {
}

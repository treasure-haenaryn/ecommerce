package io.github.treasurehaenaryn.msa.shipping.infrastructure.persistence;

import io.github.treasurehaenaryn.msa.shipping.domain.Shipment;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Shipment 엔티티 저장소.
 */
public interface ShipmentRepository extends JpaRepository<Shipment, String> {
}

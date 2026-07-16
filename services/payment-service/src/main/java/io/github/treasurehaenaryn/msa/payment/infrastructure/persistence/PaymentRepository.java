package io.github.treasurehaenaryn.msa.payment.infrastructure.persistence;

import io.github.treasurehaenaryn.msa.payment.domain.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Payment 엔티티 저장소.
 */
public interface PaymentRepository extends JpaRepository<Payment, String> {
}

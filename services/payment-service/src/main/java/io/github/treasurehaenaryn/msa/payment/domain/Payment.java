package io.github.treasurehaenaryn.msa.payment.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * 결제 엔티티.
 */
@Entity
@Table(name = "payments")
public class Payment {

    /**
     * Mock PG 판정 기준: 이 금액이면 항상 결제 실패 (재현 가능한 실패 시나리오, 실제 PG사 테스트 금액 방식과 동일한 취지).
     */
    private static final BigDecimal FAILURE_TRIGGER_AMOUNT = new BigDecimal("13000");

    @Id
    private String id;

    @Column(nullable = false)
    private String orderId;

    @Column(nullable = false)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;

    private String failureReason;

    @Column(nullable = false)
    private Instant createdAt;

    protected Payment() {
    }

    private Payment(String id, String orderId, BigDecimal amount, PaymentStatus status, String failureReason) {
        this.id = id;
        this.orderId = orderId;
        this.amount = amount;
        this.status = status;
        this.failureReason = failureReason;
        this.createdAt = Instant.now();
    }

    /**
     * Mock PG 결제 처리. amount가 FAILURE_TRIGGER_AMOUNT와 같으면 결제 실패로 시뮬레이션한다.
     */
    public static Payment process(String orderId, BigDecimal amount) {
        String id = UUID.randomUUID().toString();
        if (FAILURE_TRIGGER_AMOUNT.compareTo(amount) == 0) {
            return new Payment(id, orderId, amount, PaymentStatus.FAILED, "결제 한도 초과 (Mock PG)");
        }
        return new Payment(id, orderId, amount, PaymentStatus.COMPLETED, null);
    }

    public boolean isFailed() {
        return status == PaymentStatus.FAILED;
    }

    public String getId() {
        return id;
    }

    public String getOrderId() {
        return orderId;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public String getFailureReason() {
        return failureReason;
    }
}

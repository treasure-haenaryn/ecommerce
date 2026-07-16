package io.github.treasurehaenaryn.msa.order.domain;

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
 * 주문 엔티티.
 */
@Entity
@Table(name = "orders")
public class Order {

    @Id
    private String id;

    @Column(nullable = false)
    private String customerId;

    @Column(nullable = false)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @Column(nullable = false)
    private Instant createdAt;

    private Instant updatedAt;

    protected Order() {
    }

    private Order(String id, String customerId, BigDecimal amount, OrderStatus status, Instant createdAt) {
        this.id = id;
        this.customerId = customerId;
        this.amount = amount;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = createdAt;
    }

    public static Order create(String customerId, BigDecimal amount) {
        return new Order(UUID.randomUUID().toString(), customerId, amount, OrderStatus.CREATED, Instant.now());
    }

    public void cancel() {
        this.status = OrderStatus.CANCELLED;
        this.updatedAt = Instant.now();
    }

    public void complete() {
        this.status = OrderStatus.COMPLETED;
        this.updatedAt = Instant.now();
    }

    public String getId() {
        return id;
    }

    public String getCustomerId() {
        return customerId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}

package io.github.treasurehaenaryn.msa.inventory.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

/**
 * 상품 엔티티. 재고를 관리하며, 낙관적 락(@Version)으로 동시 차감 시 충돌을 감지한다.
 */
@Entity
@Table(name = "products")
public class Product {

    @Id
    private String id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private int stock;

    @Version
    private Long version;

    protected Product() {
    }

    public Product(String id, String name, int stock) {
        this.id = id;
        this.name = name;
        this.stock = stock;
    }

    public boolean hasEnoughStock(int quantity) {
        return stock >= quantity;
    }

    /**
     * 재고 차감.
     * 동시 수정 충돌은 @Version에 의해 저장 시점에 OptimisticLockingFailureException으로 감지
     * (Kafka 리스너에서 그대로 던지면 컨슈머 재시도 정책에 의해 자동 재처리됨).
     */
    public void decreaseStock(int quantity) {
        if (!hasEnoughStock(quantity)) {
            throw new IllegalStateException("재고 부족: " + id);
        }
        this.stock -= quantity;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getStock() {
        return stock;
    }
}

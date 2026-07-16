package io.github.treasurehaenaryn.msa.shipping.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

/**
 * 배송 엔티티.
 */
@Entity
@Table(name = "shipments")
public class Shipment {

    @Id
    private String id;

    @Column(nullable = false)
    private String orderId;

    @Column(nullable = false)
    private String trackingNumber;

    @Column(nullable = false)
    private Instant createdAt;

    protected Shipment() {
    }

    private Shipment(String id, String orderId, String trackingNumber) {
        this.id = id;
        this.orderId = orderId;
        this.trackingNumber = trackingNumber;
        this.createdAt = Instant.now();
    }

    public static Shipment start(String orderId) {
        return new Shipment(UUID.randomUUID().toString(), orderId, TrackingNumberGenerator.generate());
    }

    public String getId() {
        return id;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getTrackingNumber() {
        return trackingNumber;
    }
}

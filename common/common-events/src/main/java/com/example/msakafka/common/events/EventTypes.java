package com.example.msakafka.common.events;

/**
 * EventEnvelope#eventType 필드에 들어갈 문자열 상수.
 * 문자열을 여기저기서 직접 하드코딩하지 않도록 중앙 관리.
 */
public final class EventTypes {

    public static final String ORDER_CREATED = "OrderCreated";
    public static final String PAYMENT_COMPLETED = "PaymentCompleted";
    public static final String PAYMENT_FAILED = "PaymentFailed";
    public static final String INVENTORY_RESERVED = "InventoryReserved";
    public static final String INVENTORY_RESERVATION_FAILED = "InventoryReservationFailed";
    public static final String SHIPPING_STARTED = "ShippingStarted";
    public static final String ORDER_COMPLETED = "OrderCompleted";

    private EventTypes() {
    }
}

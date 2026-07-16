package io.github.treasurehaenaryn.msa.common.events;

/**
 * EventEnvelope#eventType 필드에 들어갈 문자열 상수.
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

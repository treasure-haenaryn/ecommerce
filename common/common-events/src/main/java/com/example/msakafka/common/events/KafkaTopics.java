package com.example.msakafka.common.events;

/**
 * 서비스 전역에서 공유하는 Kafka 토픽 이름 상수.
 * 실제 토픽 생성/파티션 수 설정은 Phase 1(Docker Compose) / Phase 5(Strimzi)에서 진행합니다.
 */
public final class KafkaTopics {

    public static final String ORDER_CREATED = "order.created";
    public static final String PAYMENT_COMPLETED = "payment.completed";
    public static final String PAYMENT_FAILED = "payment.failed";
    public static final String INVENTORY_RESERVED = "inventory.reserved";
    public static final String INVENTORY_RESERVATION_FAILED = "inventory.reservation-failed";
    public static final String SHIPPING_STARTED = "shipping.started";
    public static final String ORDER_COMPLETED = "order.completed";

    private KafkaTopics() {
    }
}

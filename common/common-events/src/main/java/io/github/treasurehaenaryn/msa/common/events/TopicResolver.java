package io.github.treasurehaenaryn.msa.common.events;

/**
 * EventTypes 상수를 KafkaTopics 상수로 매핑. Outbox Publisher가 서비스 전반에서 재사용.
 */
public final class TopicResolver {

    public static String resolve(String eventType) {
        return switch (eventType) {
            case EventTypes.ORDER_CREATED -> KafkaTopics.ORDER_CREATED;
            case EventTypes.PAYMENT_COMPLETED -> KafkaTopics.PAYMENT_COMPLETED;
            case EventTypes.PAYMENT_FAILED -> KafkaTopics.PAYMENT_FAILED;
            case EventTypes.INVENTORY_RESERVED -> KafkaTopics.INVENTORY_RESERVED;
            case EventTypes.INVENTORY_RESERVATION_FAILED -> KafkaTopics.INVENTORY_RESERVATION_FAILED;
            case EventTypes.SHIPPING_STARTED -> KafkaTopics.SHIPPING_STARTED;
            case EventTypes.ORDER_COMPLETED -> KafkaTopics.ORDER_COMPLETED;
            default -> throw new IllegalArgumentException("알 수 없는 eventType: " + eventType);
        };
    }

    private TopicResolver() {
    }
}

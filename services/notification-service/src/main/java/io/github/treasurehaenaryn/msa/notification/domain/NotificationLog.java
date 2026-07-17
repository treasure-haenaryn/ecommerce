package io.github.treasurehaenaryn.msa.notification.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

/**
 * 발송된 알림 이력. 실제 이메일/SMS 발송 대신 로그로 대체하되, 무엇을 언제 알렸는지는 남겨둔다.
 */
@Entity
@Table(name = "notification_logs")
public class NotificationLog {

    @Id
    private String id;

    @Column(nullable = false)
    private String orderId;

    @Column(nullable = false)
    private String eventType;

    @Column(nullable = false)
    private String message;

    @Column(nullable = false)
    private Instant sentAt;

    protected NotificationLog() {
    }

    private NotificationLog(String id, String orderId, String eventType, String message) {
        this.id = id;
        this.orderId = orderId;
        this.eventType = eventType;
        this.message = message;
        this.sentAt = Instant.now();
    }

    public static NotificationLog of(String orderId, String eventType, String message) {
        return new NotificationLog(UUID.randomUUID().toString(), orderId, eventType, message);
    }

    public String getOrderId() {
        return orderId;
    }

    public String getMessage() {
        return message;
    }
}

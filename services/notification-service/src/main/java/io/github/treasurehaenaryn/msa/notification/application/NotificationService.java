package io.github.treasurehaenaryn.msa.notification.application;

import io.github.treasurehaenaryn.msa.notification.domain.NotificationLog;
import io.github.treasurehaenaryn.msa.notification.infrastructure.persistence.NotificationLogRepository;
import io.github.treasurehaenaryn.msa.notification.infrastructure.persistence.ProcessedEvent;
import io.github.treasurehaenaryn.msa.notification.infrastructure.persistence.ProcessedEventRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 여러 이벤트를 구독해서 알림을 발송하는 애플리케이션 서비스.
 * 실제 이메일/SMS 발송 대신 로그 출력으로 대체 (토이 프로젝트 범위).
 * 다른 서비스로 더 이상 이벤트를 발행하지 않는 종단(sink) 서비스라 Outbox가 필요 없다.
 */
@Service
public class NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);

    private final NotificationLogRepository notificationLogRepository;
    private final ProcessedEventRepository processedEventRepository;

    public NotificationService(NotificationLogRepository notificationLogRepository,
                                ProcessedEventRepository processedEventRepository) {
        this.notificationLogRepository = notificationLogRepository;
        this.processedEventRepository = processedEventRepository;
    }

    @Transactional
    public void notifyOrderCreated(String eventId, String orderId) {
        notify(eventId, orderId, "OrderCreated", "[알림] 주문이 접수되었습니다. 주문번호: " + orderId);
    }

    @Transactional
    public void notifyPaymentFailed(String eventId, String orderId, String reason) {
        notify(eventId, orderId, "PaymentFailed", "[알림] 결제에 실패했습니다. 주문번호: " + orderId + ", 사유: " + reason);
    }

    @Transactional
    public void notifyInventoryReservationFailed(String eventId, String orderId, String reason) {
        notify(eventId, orderId, "InventoryReservationFailed",
                "[알림] 재고 부족으로 주문이 취소되었습니다. 주문번호: " + orderId + ", 사유: " + reason);
    }

    @Transactional
    public void notifyOrderCompleted(String eventId, String orderId) {
        notify(eventId, orderId, "OrderCompleted", "[알림] 주문이 완료되었습니다. 주문번호: " + orderId);
    }

    private void notify(String eventId, String orderId, String eventType, String message) {
        if (processedEventRepository.existsById(eventId)) {
            return;
        }
        log.info(message);
        notificationLogRepository.save(NotificationLog.of(orderId, eventType, message));
        processedEventRepository.save(new ProcessedEvent(eventId));
    }
}

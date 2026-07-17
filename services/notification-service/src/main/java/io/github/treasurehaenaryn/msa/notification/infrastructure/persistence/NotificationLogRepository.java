package io.github.treasurehaenaryn.msa.notification.infrastructure.persistence;

import io.github.treasurehaenaryn.msa.notification.domain.NotificationLog;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * NotificationLog 저장소.
 */
public interface NotificationLogRepository extends JpaRepository<NotificationLog, String> {
}

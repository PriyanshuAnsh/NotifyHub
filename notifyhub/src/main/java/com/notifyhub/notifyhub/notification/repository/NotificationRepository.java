package com.notifyhub.notifyhub.notification.repository;

import com.notifyhub.notifyhub.notification.domain.Notification;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, UUID> {
}

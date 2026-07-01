package com.notifyhub.notifyhub.notification.repository;

import com.notifyhub.notifyhub.notification.domain.OutboxEvent;
import com.notifyhub.notifyhub.notification.domain.OutboxStatus;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OutboxEventRepository extends JpaRepository<OutboxEvent, UUID> {

    List<OutboxEvent> findByStatusOrderByCreatedAtAsc(OutboxStatus status, Pageable pageable);
}

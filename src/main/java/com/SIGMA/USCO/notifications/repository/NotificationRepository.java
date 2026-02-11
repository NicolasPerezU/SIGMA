package com.SIGMA.USCO.notifications.repository;

import com.SIGMA.USCO.notifications.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByRecipient_IdOrderByCreatedAtDesc(Long userId);

    List<Notification> findByRecipientIdOrderByCreatedAtDesc(Long recipientId);

    long countByRecipient_IdAndReadFalse(Long recipientId);

    Optional<Notification> findByIdAndRecipient_Id(Long id, Long recipientId);


}

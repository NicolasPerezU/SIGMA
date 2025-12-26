package com.SIGMA.USCO.notifications.service;

import com.SIGMA.USCO.Users.Entity.User;
import com.SIGMA.USCO.Users.repository.UserRepository;
import com.SIGMA.USCO.Users.service.AuthService;
import com.SIGMA.USCO.notifications.dto.NotificationDTO;
import com.SIGMA.USCO.notifications.entity.Notification;
import com.SIGMA.USCO.notifications.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no autenticado"));
    }

    public ResponseEntity<?> getMyNotifications() {

        User user = getCurrentUser();

        List<Notification> notifications =
                notificationRepository.findByRecipient_IdOrderByCreatedAtDesc(user.getId());

        List<Map<String, Object>> response = notifications.stream()
                .map(n -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", n.getId());
                    map.put("type", n.getType());
                    map.put("subject", n.getSubject());
                    map.put("message", n.getMessage());
                    map.put("createdAt", n.getCreatedAt());
                    map.put("read", n.isRead());
                    map.put(
                            "studentModalityId",
                            n.getStudentModality() != null
                                    ? n.getStudentModality().getId()
                                    : null
                    );
                    return map;
                })
                .toList();

        return ResponseEntity.ok(response);
    }

    public ResponseEntity<?> getUnreadCount() {

        User user = getCurrentUser();

        long count = notificationRepository.countByRecipientIdAndReadFalse(user.getId());

        return ResponseEntity.ok(
                Map.of("unreadCount", count)
        );
    }

    public ResponseEntity<?> getNotificationDetail(Long notificationId) {

        User user = getCurrentUser();
        Notification notification = (Notification) notificationRepository.findByIdAndRecipientId(notificationId, user.getId())
                        .orElseThrow(() ->
                                new RuntimeException("Notificación no encontrada")
                        );

        Map<String, Object> response = Map.of(
                "id", notification.getId(),
                "type", notification.getType(),
                "subject", notification.getSubject(),
                "message", notification.getMessage(),
                "createdAt", notification.getCreatedAt(),
                "read", notification.isRead(),
                "studentModalityId",
                notification.getStudentModality() != null
                        ? notification.getStudentModality().getId()
                        : null
        );

        return ResponseEntity.ok(response);
    }

    public ResponseEntity<?> markAsRead(Long notificationId) {

        User user = getCurrentUser();

        Notification notification = (Notification) notificationRepository.findByIdAndRecipientId(notificationId, user.getId())
                        .orElseThrow(() ->
                                new RuntimeException("Notificación no encontrada")
                        );

        if (!notification.isRead()) {
            notification.setRead(true);
            notification.setReadAt(LocalDateTime.now());
            notificationRepository.save(notification);
        }

        return ResponseEntity.ok(
                Map.of(
                        "success", true,
                        "message", "Notificación marcada como leída"
                )
        );
    }
}

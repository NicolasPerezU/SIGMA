package com.SIGMA.USCO.notifications.controller;

import com.SIGMA.USCO.notifications.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<?> getMyNotifications() {
        return notificationService.getMyNotifications();
    }
    @GetMapping("/unread-count")
    public ResponseEntity<?> getUnreadCount() {
        return notificationService.getUnreadCount();
    }
    @GetMapping("/{notificationId}")
    public ResponseEntity<?> getNotificationDetail(@PathVariable Long notificationId) {
        return notificationService.getNotificationDetail(notificationId);
    }
    @PatchMapping("/{notificationId}/read")
    public ResponseEntity<?> markAsRead(@PathVariable Long notificationId) {
        return notificationService.markAsRead(notificationId);
    }
}




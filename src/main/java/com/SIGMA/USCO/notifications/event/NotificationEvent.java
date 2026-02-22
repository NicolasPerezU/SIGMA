package com.SIGMA.USCO.notifications.event;

import com.SIGMA.USCO.notifications.entity.enums.NotificationRecipientType;
import com.SIGMA.USCO.notifications.entity.enums.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationEvent {
    private NotificationType type;
    private NotificationRecipientType recipientType;
    private Long recipientId;
    private Long triggeredById;
    private Long studentModalityId;
    private String subject;
    private String message;
}


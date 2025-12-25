package com.SIGMA.USCO.notifications.dto;

import com.SIGMA.USCO.Modalities.Entity.StudentModality;
import com.SIGMA.USCO.Users.Entity.User;
import com.SIGMA.USCO.notifications.entity.enums.NotificationRecipientType;
import com.SIGMA.USCO.notifications.entity.enums.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NotificationDTO {

    private NotificationType type;
    private NotificationRecipientType recipientType;
    private User recipient;
    private User triggeredBy;
    private StudentModality studentModality;
    private String subject;
    private String message;
    private Map<String, Object> variables;

}

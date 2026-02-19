package com.SIGMA.USCO.notifications.entity;

import com.SIGMA.USCO.Modalities.Entity.StudentModality;
import com.SIGMA.USCO.Users.Entity.User;
import com.SIGMA.USCO.notifications.entity.enums.NotificationRecipientType;
import com.SIGMA.USCO.notifications.entity.enums.NotificationType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private NotificationType type;

    @Enumerated(EnumType.STRING)
    private NotificationRecipientType recipientType;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipient_user_id")
    private User recipient;


    @ManyToOne
    @JoinColumn(name = "triggered_by_user_id")
    private User triggeredBy;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_modality_id")
    private StudentModality studentModality;

    @Column(name = "invitation_id")
    private Long invitationId;

    private String subject;


    @Column(columnDefinition = "TEXT")
    private String message;

    private LocalDateTime createdAt;
    private LocalDateTime sentAt;

    private boolean emailSent = false;
    private boolean inAppDelivered = false;

    @Column(name = "is_read", nullable = false)
    private boolean read = false;
    private LocalDateTime readAt;

}

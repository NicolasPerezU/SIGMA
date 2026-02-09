package com.SIGMA.USCO.notifications.service;

import com.SIGMA.USCO.config.EmailService;
import com.SIGMA.USCO.notifications.entity.Notification;
import com.SIGMA.USCO.notifications.repository.NotificationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class NotificationDispatcherService {

    private final NotificationRepository notificationRepository;
    private final EmailService emailService;

    private boolean shouldSendEmail(Notification notification) {
        return switch (notification.getRecipientType()) {
            case STUDENT, PROJECT_DIRECTOR -> true;
            case PROGRAM_HEAD -> false; // ESTO SIRVE PARA QUE NO SE ENVÍEN CORREOS A LOS JEFES DE PROGRAMA
            case PROGRAM_CURRICULUM_COMMITTEE -> false; //  ESTO SIRVE PARA QUE NO SE ENVÍEN CORREOS A LOS COMITÉS DE CURRÍCULO DE PROGRAMA
            default -> false;
        };
    }
    @Transactional
    public void dispatch(Notification notification) {

        if (shouldSendEmail(notification)) {

            emailService.sendEmail(
                    notification.getRecipient().getEmail(),
                    notification.getSubject(),
                    notification.getMessage()
            );

            notification.setEmailSent(true);
            notification.setSentAt(LocalDateTime.now());
        }

        notification.setInAppDelivered(true);

        notificationRepository.save(notification);
    }

    @Transactional
    public void dispatchWithAttachment(Notification notification, Path attachmentPath, String attachmentName) {

        if (shouldSendEmail(notification)) {

            emailService.sendEmailWithAttachment(
                    notification.getRecipient().getEmail(),
                    notification.getSubject(),
                    notification.getMessage(),
                    attachmentPath.toFile(),
                    attachmentName
            );

            notification.setEmailSent(true);
            notification.setSentAt(LocalDateTime.now());
        }

        notification.setInAppDelivered(true);

        notificationRepository.save(notification);
    }
}

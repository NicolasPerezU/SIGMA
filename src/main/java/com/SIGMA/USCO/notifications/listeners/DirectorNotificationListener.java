package com.SIGMA.USCO.notifications.listeners;

import com.SIGMA.USCO.Modalities.Entity.StudentModality;
import com.SIGMA.USCO.Modalities.Repository.StudentModalityRepository;
import com.SIGMA.USCO.notifications.entity.Notification;
import com.SIGMA.USCO.notifications.entity.enums.NotificationRecipientType;
import com.SIGMA.USCO.notifications.entity.enums.NotificationType;
import com.SIGMA.USCO.notifications.event.CancellationApprovedEvent;
import com.SIGMA.USCO.notifications.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Component
public class DirectorNotificationListener {

    private final StudentModalityRepository studentModalityRepository;
    private final NotificationRepository notificationRepository;

    @EventListener
    public void onCancellationApproved(CancellationApprovedEvent event) {

        StudentModality sm = studentModalityRepository.findById(event.getStudentModalityId()).orElseThrow();

        if (sm.getProjectDirector() == null) {
            return;
        }

        String subject = "Cancelación de modalidad APROBADA";
        String message = """
                Hola %s,

                La cancelación de la modalidad:

                "%s"

                del estudiante %s ha sido APROBADA por el concejo.

                Sistema SIGMA
                """.formatted(
                sm.getProjectDirector().getName(),
                sm.getModality().getName(),
                sm.getStudent().getName() + " " + sm.getStudent().getLastName()
        );

        Notification notification = Notification.builder()
                .type(NotificationType.MODALITY_CANCELLATION_APPROVED)
                .recipientType(NotificationRecipientType.PROJECT_DIRECTOR)
                .recipient(sm.getProjectDirector())
                .triggeredBy(null)
                .studentModality(sm)
                .subject(subject)
                .message(message)
                .createdAt(LocalDateTime.now())
                .build();

        notificationRepository.save(notification);
    }
}

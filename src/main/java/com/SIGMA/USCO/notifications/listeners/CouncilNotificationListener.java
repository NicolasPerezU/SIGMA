package com.SIGMA.USCO.notifications.listeners;

import com.SIGMA.USCO.Modalities.Entity.StudentModality;
import com.SIGMA.USCO.Modalities.Repository.StudentModalityRepository;
import com.SIGMA.USCO.Users.Entity.User;
import com.SIGMA.USCO.Users.repository.UserRepository;
import com.SIGMA.USCO.notifications.entity.Notification;
import com.SIGMA.USCO.notifications.entity.enums.NotificationRecipientType;
import com.SIGMA.USCO.notifications.entity.enums.NotificationType;
import com.SIGMA.USCO.notifications.event.CancellationRequestedEvent;
import com.SIGMA.USCO.notifications.event.ModalityApprovedBySecretary;
import com.SIGMA.USCO.notifications.repository.NotificationRepository;
import com.SIGMA.USCO.notifications.service.NotificationDispatcherService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Component
public class CouncilNotificationListener {

    private final StudentModalityRepository studentModalityRepository;
    private final NotificationRepository notificationRepository;
    private final NotificationDispatcherService dispatcher;
    private final UserRepository userRepository;

    @EventListener
    public void onCancellationRequested(CancellationRequestedEvent event){

        StudentModality studentModality = studentModalityRepository.findById(event.getStudentModalityId())
                .orElseThrow();

        List<User> councilMembers =
                userRepository.findAllByRoles_Name("COUNCIL");

        String subject = "Solicitud de cancelación de modalidad";

        String message = """
                Se ha recibido una solicitud de cancelación
                de modalidad de grado por parte del estudiante:

                "%s"

                Por favor, revise la solicitud y tome las
                acciones necesarias.

                Sistema SIGMA
                """.formatted(
                studentModality.getStudent().getName() + " " + studentModality.getStudent().getLastName()
        );

        for (User councilMember : councilMembers) {

            Notification notification = Notification.builder()
                    .type(NotificationType.MODALITY_CANCELLATION_REQUESTED)
                    .recipientType(NotificationRecipientType.COUNCIL)
                    .recipient(councilMember)
                    .triggeredBy(studentModality.getStudent())
                    .studentModality(studentModality)
                    .subject(subject)
                    .message(message)
                    .createdAt(LocalDateTime.now())
                    .build();

            notificationRepository.save(notification);
            dispatcher.dispatch(notification);
        }

    }

    public void onModalityApprovedBySecretary(ModalityApprovedBySecretary event) {
        StudentModality modality = studentModalityRepository.findById(event.getStudentModalityId()).orElseThrow();

        List<User> councilMembers =
                userRepository.findAllByRoles_Name("COUNCIL");

        String subject = "Modalidad de grado aprobada por Secretaría";

        String message = """
                La modalidad de grado del estudiante:

                "%s"

                ha sido aprobada por Secretaría. Por favor,
                proceda con las siguientes etapas del proceso.

                Sistema SIGMA
                """.formatted(
                modality.getStudent().getName() + " " + modality.getStudent().getLastName()
        );

        for (User councilMember : councilMembers) {

            Notification notification = Notification.builder()
                    .type(NotificationType.MODALITY_APPROVED_BY_SECRETARY)
                    .recipientType(NotificationRecipientType.COUNCIL)
                    .recipient(councilMember)
                    .triggeredBy(modality.getStudent())
                    .studentModality(modality)
                    .subject(subject)
                    .message(message)
                    .createdAt(LocalDateTime.now())
                    .build();

            notificationRepository.save(notification);
            dispatcher.dispatch(notification);
        }




    }

}

package com.SIGMA.USCO.notifications.listeners;

import com.SIGMA.USCO.Modalities.Entity.StudentModality;
import com.SIGMA.USCO.Modalities.Repository.StudentModalityRepository;
import com.SIGMA.USCO.Users.Entity.User;
import com.SIGMA.USCO.Users.repository.UserRepository;
import com.SIGMA.USCO.config.EmailService;
import com.SIGMA.USCO.notifications.entity.Notification;
import com.SIGMA.USCO.notifications.entity.enums.NotificationRecipientType;
import com.SIGMA.USCO.notifications.entity.enums.NotificationType;
import com.SIGMA.USCO.notifications.event.*;
import com.SIGMA.USCO.notifications.repository.NotificationRepository;
import com.SIGMA.USCO.notifications.service.NotificationDispatcherService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Component
public class DirectorNotificationListener {

    private final StudentModalityRepository studentModalityRepository;
    private final NotificationRepository notificationRepository;
    private final NotificationDispatcherService dispatcher;
    private final EmailService emailService;
    private final UserRepository userRepository;

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
        dispatcher.dispatch(notification);

    }

    @EventListener
    public void onCancellationRejected(CancellationApprovedEvent event) {

        StudentModality sm = studentModalityRepository.findById(event.getStudentModalityId()).orElseThrow();

        if (sm.getProjectDirector() == null) {
            return;
        }

        String subject = "Cancelación de modalidad RECHAZADA";
        String message = """
                Hola %s,

                La cancelación de la modalidad:

                "%s"

                del estudiante %s ha sido RECHAZADA por el concejo.

                Sistema SIGMA
                """.formatted(
                sm.getProjectDirector().getName(),
                sm.getModality().getName(),
                sm.getStudent().getName() + " " + sm.getStudent().getLastName()
        );


        Notification notification = Notification.builder()
                .type(NotificationType.MODALITY_CANCELLATION_REJECTED)
                .recipientType(NotificationRecipientType.PROJECT_DIRECTOR)
                .recipient(sm.getProjectDirector())
                .triggeredBy(null)
                .studentModality(sm)
                .subject(subject)
                .message(message)
                .createdAt(LocalDateTime.now())
                .build();

        notificationRepository.save(notification);
        dispatcher.dispatch(notification);
    }

    @EventListener
    public void onCancellationRequested(CancellationRequestedEvent event){
        StudentModality sm = studentModalityRepository.findById(event.getStudentModalityId()).orElseThrow();

        if (sm.getProjectDirector() == null) {
            return;
        }

        String subject = "Solicitud de cancelación de modalidad";
        String message = """
                Hola %s,

                El estudiante %s ha solicitado la cancelación de la modalidad:

                "%s"

                Esta solicitud será evaluada por el concejo Académico.

                Sistema SIGMA
                """.formatted(
                sm.getProjectDirector().getName(),
                sm.getStudent().getName() + " " + sm.getStudent().getLastName(),
                sm.getModality().getName()
        );



        Notification notification = Notification.builder()
                .type(NotificationType.MODALITY_CANCELLATION_REQUESTED)
                .recipientType(NotificationRecipientType.PROJECT_DIRECTOR)
                .recipient(sm.getProjectDirector())
                .triggeredBy(null)
                .studentModality(sm)
                .subject(subject)
                .message(message)
                .createdAt(LocalDateTime.now())
                .build();

        notificationRepository.save(notification);
        dispatcher.dispatch(notification);
    }

    @EventListener
    public void handleDefenseScheduled(DefenseScheduledEvent event) {

        StudentModality modality = studentModalityRepository.findById(event.getStudentModalityId()).orElseThrow();

        User director = modality.getProjectDirector();

        if (director == null) {
            return;
        }

        String directorSubject =
                "Sustentación programada – Estudiante asignado";

        String directorMessage = """
                Hola %s,

                Se ha programado la sustentación de una modalidad
                de grado bajo tu dirección:

                Estudiante:
                %s (%s)

                Modalidad:
                "%s"

                Fecha y hora:
                %s

                Lugar:
                %s

                Por favor asegúrate de cumplir con los
                lineamientos académicos establecidos.

                Sistema SIGMA
                """.formatted(
                director.getName(),
                modality.getStudent().getName(),
                modality.getStudent().getEmail(),
                modality.getModality().getName(),
                event.getDefenseDate(),
                event.getDefenseLocation()
        );



        Notification notification = Notification.builder()
                .type(NotificationType.DEFENSE_SCHEDULED)
                .recipientType(NotificationRecipientType.PROJECT_DIRECTOR)
                .recipient(director)
                .triggeredBy(null)
                .studentModality(modality)
                .subject(directorSubject)
                .message(directorMessage)
                .createdAt(LocalDateTime.now())
                .build();
        notificationRepository.save(notification);
        dispatcher.dispatch(notification);
    }

    @EventListener
    public void DirectorAssigned(DirectorAssignedEvent event){

        StudentModality modality =
                studentModalityRepository.findById(event.getStudentModalityId())
                        .orElseThrow();


        User student = modality.getStudent();
        User director = userRepository.findById(event.getDirectorId())
                .orElseThrow();

        String directorSubject = "Nueva modalidad asignada como Director – SIGMA";

        String directorMessage = """
                Hola %s,

                Has sido asignado como Director de Proyecto
                para la siguiente modalidad de grado:

                Estudiante:
                %s (%s)

                Modalidad:
                "%s"

                Fecha de asignación:
                %s

                Por favor ingresa al sistema SIGMA para realizar
                el seguimiento correspondiente.

                Sistema SIGMA
                """.formatted(
                director.getName(),
                student.getName() + " " + student.getLastName(),
                student.getEmail(),
                modality.getModality().getName(),
                modality.getUpdatedAt()
        );



        Notification notification = Notification.builder()
                .type(NotificationType.DIRECTOR_ASSIGNED)
                .recipientType(NotificationRecipientType.PROJECT_DIRECTOR)
                .recipient(director)
                .triggeredBy(null)
                .studentModality(modality)
                .subject(directorSubject)
                .message(directorMessage)
                .createdAt(LocalDateTime.now())
                .build();
        notificationRepository.save(notification);
        dispatcher.dispatch(notification);
    }

    @EventListener
    public void FinalDefenseResultEvent(FinalDefenseResultEvent event) {

        StudentModality modality =
                studentModalityRepository.findById(event.getStudentModalityId())
                        .orElseThrow();
        User director = modality.getProjectDirector();
        if (director == null) {
            return;
        }
        String subject = "Resultado de la sustentación final – Estudiante asignado";

        String message = """
                Hola %s,

                La sustentación final de la modalidad de grado
                bajo tu dirección ha concluido con el siguiente resultado:

                Estudiante:
                %s (%s)

                Modalidad:
                "%s"

                Resultado final:
                %s

                Distinción académica:
                %s

                Observaciones:
                %s

                Sistema SIGMA
                """.formatted(
                director.getName(),
                modality.getStudent().getName(),
                modality.getStudent().getEmail(),
                modality.getModality().getName(),
                event.getFinalStatus().toString(),
                event.getAcademicDistinction().toString(),
                event.getObservations() != null ? event.getObservations() : "N/A"
        );

        Notification notification = Notification.builder()
                .type(NotificationType.DEFENSE_COMPLETED)
                .recipientType(NotificationRecipientType.PROJECT_DIRECTOR)
                .recipient(director)
                .triggeredBy(null)
                .studentModality(modality)
                .subject(subject)
                .message(message)
                .createdAt(LocalDateTime.now())
                .build();
        notificationRepository.save(notification);
        dispatcher.dispatch(notification);

    }
}

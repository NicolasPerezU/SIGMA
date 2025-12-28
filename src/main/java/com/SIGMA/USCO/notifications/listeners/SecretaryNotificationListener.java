package com.SIGMA.USCO.notifications.listeners;

import com.SIGMA.USCO.Modalities.Entity.StudentModality;
import com.SIGMA.USCO.Modalities.Repository.StudentModalityRepository;
import com.SIGMA.USCO.Users.Entity.User;
import com.SIGMA.USCO.Users.repository.UserRepository;
import com.SIGMA.USCO.documents.entity.StudentDocument;
import com.SIGMA.USCO.documents.repository.StudentDocumentRepository;
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
import java.util.List;

import static com.SIGMA.USCO.notifications.entity.enums.NotificationRecipientType.SECRETARY;

@RequiredArgsConstructor
@Component
public class SecretaryNotificationListener {

    private final StudentModalityRepository studentModalityRepository;
    private final NotificationRepository notificationRepository;
    private final NotificationDispatcherService dispatcher;
    private final UserRepository userRepository;
    private final StudentDocumentRepository studentDocumentRepository;

    @EventListener
    public void handleModalityStartedEvent(StudentModalityStarted event){

        StudentModality studentModality = studentModalityRepository.findById(event.getStudentModalityId()).orElseThrow();
        List<User> secretaries =
                userRepository.findAllByRoles_Name("SECRETARY");
        String subject = "Nueva modalidad iniciada - Estudiante: " + studentModality.getStudent().getName() + " " + studentModality.getStudent().getLastName();

        String message = """
                Hola Secretaría,
                
                Se ha iniciado una nueva modalidad de grado:
                
                "%s"
                
                para el estudiante %s.
                
                Por favor, proceda a revisar y validar la información correspondiente.
                
                Sistema SIGMA
                """.formatted(
                studentModality.getModality().getName(),
                studentModality.getStudent().getName() + " " + studentModality.getStudent().getLastName()
        );
        for (User secretary : secretaries) {
            Notification notification = Notification.builder()
                    .type(NotificationType.MODALITY_STARTED)
                    .recipientType(NotificationRecipientType.SECRETARY)
                    .recipient(secretary)
                    .triggeredBy(null)
                    .studentModality(studentModality)
                    .subject(subject)
                    .message(message)
                    .createdAt(LocalDateTime.now())
                    .build();

            notificationRepository.save(notification);
            dispatcher.dispatch(notification);
        }

    }

    @EventListener
    public void onStudentDocumentUpdated(StudentDocumentUpdatedEvent event) {

        StudentModality modality =
                studentModalityRepository.findById(event.getStudentModalityId())
                        .orElseThrow();

        StudentDocument document =
                studentDocumentRepository.findById(event.getStudentDocumentId())
                        .orElseThrow();

        User student = modality.getStudent();

        List<User> secretaries =
                userRepository.findAllByRoles_Name("SECRETARY");

        String subject = "Documento actualizado por estudiante";

        String message = """
            El estudiante %s ha actualizado o resubido
            un documento solicitado por Secretaría.

            Estudiante:
            %s (%s)

            Modalidad:
            "%s"

            Documento:
            "%s"

            Estado actual:
            %s

            Por favor, ingrese al sistema SIGMA para
            revisar el documento actualizado.

            Sistema SIGMA
            """.formatted(
                student.getName(),
                student.getName() + " " + student.getLastName(),
                student.getEmail(),
                modality.getModality().getName(),
                document.getDocumentConfig().getDocumentName(),
                document.getStatus()
        );



        for (User secretary : secretaries) {

            Notification notification = Notification.builder()
                    .type(NotificationType.DOCUMENT_UPLOADED)
                    .recipientType(NotificationRecipientType.SECRETARY)
                    .recipient(secretary)
                    .triggeredBy(student)
                    .studentModality(modality)
                    .subject(subject)
                    .message(message)
                    .createdAt(LocalDateTime.now())
                    .build();

            notificationRepository.save(notification);
            dispatcher.dispatch(notification);
        }
    }

    @EventListener
    public void handleDefenseScheduledEvent(DefenseScheduledEvent event){
        StudentModality studentModality = studentModalityRepository.findById(event.getStudentModalityId()).orElseThrow();


        List<User> secretaries =
                userRepository.findAllByRoles_Name("SECRETARY");

        String subject = "Sustentación programada - Estudiante: " + studentModality.getStudent().getName() + " " + studentModality.getStudent().getLastName();

        String message = """
                Hola Secretaría,
                
                Se ha programado una sustentación para la modalidad:
                
                "%s"
                
                del estudiante %s.
                
                Fecha y hora de la sustentación: %s
                Lugar de la sustentación: %s
                
                Por favor, tome las medidas necesarias para coordinar los aspectos logísticos.
                
                Sistema SIGMA
                """.formatted(
                studentModality.getModality().getName(),
                studentModality.getStudent().getName() + " " + studentModality.getStudent().getLastName(),
                event.getDefenseDate().toString(),
                event.getDefenseLocation()
        );
        for (User secretary : secretaries) {

            Notification notification = Notification.builder()
                    .type(NotificationType.DEFENSE_SCHEDULED)
                    .recipientType(NotificationRecipientType.SECRETARY)
                    .recipient(secretary)
                    .triggeredBy(null)
                    .studentModality(studentModality)
                    .subject(subject)
                    .message(message)
                    .createdAt(LocalDateTime.now())
                    .build();

            notificationRepository.save(notification);
            dispatcher.dispatch(notification);
        }
    }

    @EventListener
    public void onDirectorAssigned(DirectorAssignedEvent event){

        StudentModality studentModality = studentModalityRepository.findById(event.getStudentModalityId()).orElseThrow();

        List<User> secretaries =
                userRepository.findAllByRoles_Name("SECRETARY");

        String subject = "Nuevo director asignado - Estudiante: " + studentModality.getStudent().getName() + " " + studentModality.getStudent().getLastName();

        String message = """
                Hola Secretaría,
                
                Se ha asignado un nuevo director para la modalidad:
                
                "%s"
                
                del estudiante %s.
                
                """.formatted(
                studentModality.getModality().getName(),
                studentModality.getStudent().getName() + " " + studentModality.getStudent().getLastName()
        );
        for (User secretary : secretaries) {

            Notification notification = Notification.builder()
                    .type(NotificationType.DIRECTOR_ASSIGNED)
                    .recipientType(NotificationRecipientType.SECRETARY)
                    .recipient(secretary)
                    .triggeredBy(null)
                    .studentModality(studentModality)
                    .subject(subject)
                    .message(message)
                    .createdAt(LocalDateTime.now())
                    .build();

            notificationRepository.save(notification);
            dispatcher.dispatch(notification);
        }

    }

    @EventListener
    public void FinalDefenseResult(FinalDefenseResultEvent event){

        StudentModality modality =
                studentModalityRepository.findById(event.getStudentModalityId())
                        .orElseThrow();
        User director = modality.getProjectDirector();
        if (director == null) {
            return;
        }

        List<User> secretaries =
                userRepository.findAllByRoles_Name("SECRETARY");



        String subject = "Resultado de la defensa final - Estudiante: " + modality.getStudent().getName() + " " + modality.getStudent().getLastName();

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

        for (User secretary : secretaries) {

            Notification notification = Notification.builder()
                    .type(NotificationType.DEFENSE_COMPLETED)
                    .recipientType(NotificationRecipientType.SECRETARY)
                    .recipient(secretary)
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

    @EventListener
    public void ModalityApproved(ModalityApprovedByCouncilEvent event){
        StudentModality modality =
                studentModalityRepository.findById(event.getStudentModalityId())
                        .orElseThrow();

        List<User> secretaries =
                userRepository.findAllByRoles_Name("SECRETARY");

        String subject = "Modalidad aprobada por el consejo - Estudiante: " + modality.getStudent().getName() + " " + modality.getStudent().getLastName();

        String message = """
                Hola Secretaría,

                La modalidad de grado ha sido aprobada por el consejo:

                Estudiante:
                %s (%s)

                Modalidad:
                "%s"

                Fecha de aprobación:
                %s

                Sistema SIGMA
                """.formatted(
                modality.getStudent().getName(),
                modality.getStudent().getEmail(),
                modality.getModality().getName(),
                modality.getSelectionDate()
        );

        for (User secretary : secretaries) {

            Notification notification = Notification.builder()
                    .type(NotificationType.MODALITY_APPROVED_BY_COUNCIL)
                    .recipientType(NotificationRecipientType.SECRETARY)
                    .recipient(secretary)
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

}

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


@RequiredArgsConstructor
@Component
public class ProgramHeadNotificationListener {

    private final StudentModalityRepository studentModalityRepository;
    private final NotificationRepository notificationRepository;
    private final NotificationDispatcherService dispatcher;
    private final UserRepository userRepository;
    private final StudentDocumentRepository studentDocumentRepository;

    @EventListener
    public void handleModalityStartedEvent(StudentModalityStarted event){

        StudentModality studentModality = studentModalityRepository.findById(event.getStudentModalityId()).orElseThrow();
        List<User> programHeads = userRepository.findAllByRoles_Name("PROGRAM_HEAD");
        String subject = "Nueva modalidad iniciada - Estudiante: " + studentModality.getLeader().getName() + " " + studentModality.getLeader().getLastName();

        String message = """
                Hola Jefatura de Programa,
                
                Se ha iniciado una nueva modalidad de grado:
                
                "%s"
                
                para el estudiante %s.
                
                Por favor, proceda a revisar y validar la información correspondiente.
                
                Sistema SIGMA
                """.formatted(
                studentModality.getProgramDegreeModality().getDegreeModality().getName(),
                studentModality.getLeader().getName() + " " + studentModality.getLeader().getLastName()
        );
        for (User programHead : programHeads) {
            Notification notification = Notification.builder()
                    .type(NotificationType.MODALITY_STARTED)
                    .recipientType(NotificationRecipientType.PROGRAM_HEAD)
                    .recipient(programHead)
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

        StudentModality modality = studentModalityRepository.findById(event.getStudentModalityId())
                        .orElseThrow();

        StudentDocument document = studentDocumentRepository.findById(event.getStudentDocumentId())
                        .orElseThrow();

        User student = modality.getLeader();

        List<User> programHeads =
                userRepository.findAllByRoles_Name("PROGRAM_HEAD");

        String subject = "Documento actualizado por estudiante";

        String message = """
            El estudiante %s ha actualizado o resubido
            un documento solicitado por jefatura del programa.

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
                modality.getProgramDegreeModality().getDegreeModality().getName(),
                document.getDocumentConfig().getDocumentName(),
                document.getStatus()
        );



        for (User programHead : programHeads) {

            Notification notification = Notification.builder()
                    .type(NotificationType.DOCUMENT_UPLOADED)
                    .recipientType(NotificationRecipientType.PROGRAM_HEAD)
                    .recipient(programHead)
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


        List<User> programHeads = userRepository.findAllByRoles_Name("PROGRAM_HEAD");

        String subject = "Sustentación programada - Estudiante: " + studentModality.getLeader().getName() + " " + studentModality.getLeader().getLastName();

        String message = """
                Hola jefatura del programa,
                
                Se ha programado una sustentación para la modalidad:
                
                "%s"
                
                del estudiante %s.
                
                Fecha y hora de la sustentación: %s
                Lugar de la sustentación: %s
                
                Por favor, tome las medidas necesarias para coordinar los aspectos logísticos.
                
                Sistema SIGMA
                """.formatted(
                studentModality.getProgramDegreeModality().getDegreeModality().getName(),
                studentModality.getLeader().getName() + " " + studentModality.getLeader().getLastName(),
                event.getDefenseDate().toString(),
                event.getDefenseLocation()
        );
        for (User programHead : programHeads) {

            Notification notification = Notification.builder()
                    .type(NotificationType.DEFENSE_SCHEDULED)
                    .recipientType(NotificationRecipientType.PROGRAM_HEAD)
                    .recipient(programHead)
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

        List<User> programHeads =
                userRepository.findAllByRoles_Name("PROGRAM_HEAD");

        String subject = "Nuevo director asignado - Estudiante: " + studentModality.getLeader().getName() + " " + studentModality.getLeader().getLastName();

        String message = """
                Hola jefatura del programa,
                
                Se ha asignado un nuevo director para la modalidad:
                
                "%s"
                
                del estudiante %s.
                
                """.formatted(
                studentModality.getProgramDegreeModality().getDegreeModality().getName(),
                studentModality.getLeader().getName() + " " + studentModality.getLeader().getLastName()
        );
        for (User programHead : programHeads) {

            Notification notification = Notification.builder()
                    .type(NotificationType.DIRECTOR_ASSIGNED)
                    .recipientType(NotificationRecipientType.PROGRAM_HEAD)
                    .recipient(programHead)
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

        List<User> programHeads =
                userRepository.findAllByRoles_Name("PROGRAM_HEAD");



        String subject = "Resultado de la defensa final - Estudiante: " + modality.getLeader().getName() + " " + modality.getLeader().getLastName();

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
                modality.getLeader().getName(),
                modality.getLeader().getEmail(),
                modality.getProgramDegreeModality().getDegreeModality().getName(),
                event.getFinalStatus().toString(),
                event.getAcademicDistinction().toString(),
                event.getObservations() != null ? event.getObservations() : "N/A"
        );

        for (User programHead : programHeads) {

            Notification notification = Notification.builder()
                    .type(NotificationType.DEFENSE_COMPLETED)
                    .recipientType(NotificationRecipientType.PROGRAM_HEAD)
                    .recipient(programHead)
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
    public void ModalityApproved(ModalityApprovedByCommitteeEvent event){
        StudentModality modality =
                studentModalityRepository.findById(event.getStudentModalityId())
                        .orElseThrow();

        List<User> programHeads = userRepository.findAllByRoles_Name("PROGRAM_HEAD");

        String subject = "Modalidad aprobada por el comité de currículo de programa - Estudiante: " + modality.getLeader().getName() + " " + modality.getLeader().getLastName();

        String message = """
                Hola Jefatura de programa,

                La modalidad de grado ha sido aprobada por el comité de currículo de programa.

                Estudiante:
                %s (%s)

                Modalidad:
                "%s"

                Fecha de aprobación:
                %s

                Sistema SIGMA
                """.formatted(
                modality.getLeader().getName(),
                modality.getLeader().getEmail(),
                modality.getProgramDegreeModality().getDegreeModality().getName(),
                modality.getSelectionDate()
        );

        for (User programHead : programHeads) {

            Notification notification = Notification.builder()
                    .type(NotificationType.MODALITY_APPROVED_BY_PROGRAM_CURRICULUM_COMMITTEE)
                    .recipientType(NotificationRecipientType.PROGRAM_HEAD)
                    .recipient(programHead)
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

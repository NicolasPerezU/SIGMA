package com.SIGMA.USCO.notifications.listeners;

import com.SIGMA.USCO.Modalities.Entity.StudentModality;
import com.SIGMA.USCO.Modalities.Entity.enums.ModalityProcessStatus;
import com.SIGMA.USCO.Modalities.Repository.StudentModalityRepository;
import com.SIGMA.USCO.Users.Entity.User;
import com.SIGMA.USCO.Users.repository.UserRepository;
import com.SIGMA.USCO.documents.entity.StudentDocument;
import com.SIGMA.USCO.documents.repository.StudentDocumentRepository;
import com.SIGMA.USCO.notifications.entity.Notification;
import com.SIGMA.USCO.notifications.entity.enums.NotificationRecipientType;
import com.SIGMA.USCO.notifications.entity.enums.NotificationType;
import com.SIGMA.USCO.notifications.event.CancellationRequestedEvent;
import com.SIGMA.USCO.notifications.event.ModalityApprovedByProgramHead;
import com.SIGMA.USCO.notifications.event.StudentDocumentUpdatedEvent;
import com.SIGMA.USCO.notifications.repository.NotificationRepository;
import com.SIGMA.USCO.notifications.service.NotificationDispatcherService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.List;

@RequiredArgsConstructor
@Component
public class CommitteeNotificationListener {

    private final StudentModalityRepository studentModalityRepository;
    private final NotificationRepository notificationRepository;
    private final NotificationDispatcherService dispatcher;
    private final UserRepository userRepository;
    private final StudentDocumentRepository studentDocumentRepository;

    @EventListener
    public void onCancellationRequested(CancellationRequestedEvent event){

        StudentModality studentModality = studentModalityRepository.findById(event.getStudentModalityId()).orElseThrow();

        List<User> committeeMembers = userRepository.findAllByRoles_Name("PROGRAM_CURRICULUM_COMMITTEE");

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

        for (User committeeMember : committeeMembers) {

            Notification notification = Notification.builder()
                    .type(NotificationType.MODALITY_CANCELLATION_REQUESTED)
                    .recipientType(NotificationRecipientType.PROGRAM_CURRICULUM_COMMITTEE)
                    .recipient(committeeMember)
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

    @EventListener
    public void onModalityApprovedByProgramHead(ModalityApprovedByProgramHead event) {
        StudentModality modality = studentModalityRepository.findById(event.getStudentModalityId()).orElseThrow();

        List<User> committeeMembers =
                userRepository.findAllByRoles_Name("PROGRAM_CURRICULUM_COMMITTEE");

        String subject = "Modalidad de grado aprobada por Jefatura de Programa";

        String message = """
                La modalidad de grado del estudiante:

                "%s"

                ha sido aprobada por jefatura del programa. Por favor,
                proceda con las siguientes etapas del proceso.

                Sistema SIGMA
                """.formatted(
                modality.getStudent().getName() + " " + modality.getStudent().getLastName()
        );

        for (User committeMember : committeeMembers) {

            Notification notification = Notification.builder()
                    .type(NotificationType.MODALITY_APPROVED_BY_PROGRAM_HEAD)
                    .recipientType(NotificationRecipientType.PROGRAM_CURRICULUM_COMMITTEE)
                    .recipient(committeMember)
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

    private static final EnumSet<ModalityProcessStatus> VALID_STATES =
            EnumSet.of(
                    ModalityProcessStatus.READY_FOR_PROGRAM_CURRICULUM_COMMITTEE,
                    ModalityProcessStatus.UNDER_REVIEW_PROGRAM_CURRICULUM_COMMITTEE,
                    ModalityProcessStatus.PROPOSAL_APPROVED,
                    ModalityProcessStatus.DEFENSE_SCHEDULED
            );

    @EventListener
    public void onStudentDocumentUpdated(StudentDocumentUpdatedEvent event) {

        StudentModality modality =
                studentModalityRepository.findById(event.getStudentModalityId())
                        .orElseThrow();

        if (!VALID_STATES.contains(modality.getStatus())) {
            return;
        }

        StudentDocument document = studentDocumentRepository.findById(event.getStudentDocumentId())
                        .orElseThrow();

        User student = modality.getStudent();

        String subject = "Documento actualizado – Modalidad en revisión";

        String message = """
                El estudiante %s ha actualizado un documento
                asociado a una modalidad en revisión.

                Modalidad:
                "%s"

                Documento:
                "%s"

                Estado:
                %s

                Sistema SIGMA
                """.formatted(
                student.getName() + " " + student.getLastName(),
                modality.getProgramDegreeModality().getAcademicProgram().getName(),
                document.getDocumentConfig().getDocumentName(),
                document.getStatus()
        );

        List<User> committeeMembers =
                userRepository.findAllByRoles_Name("PROGRAM_CURRICULUM_COMMITTEE");

        for (User committee : committeeMembers) {

            Notification notification = Notification.builder()
                    .type(NotificationType.DOCUMENT_UPLOADED)
                    .recipientType(NotificationRecipientType.PROGRAM_CURRICULUM_COMMITTEE)
                    .recipient(committee)
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


}

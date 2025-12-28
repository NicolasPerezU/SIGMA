package com.SIGMA.USCO.notifications.listeners;

import com.SIGMA.USCO.Modalities.Entity.StudentModality;
import com.SIGMA.USCO.Modalities.Entity.enums.ModalityProcessStatus;
import com.SIGMA.USCO.Modalities.Repository.StudentModalityRepository;
import com.SIGMA.USCO.Users.Entity.User;
import com.SIGMA.USCO.Users.repository.UserRepository;
import com.SIGMA.USCO.config.EmailService;
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

@Component
@RequiredArgsConstructor
public class StudentNotificationListener {

    private final StudentModalityRepository studentModalityRepository;
    private final NotificationRepository notificationRepository;
    private final NotificationDispatcherService dispatcher;
    private final UserRepository userRepository;
    private final StudentDocumentRepository studentDocumentRepository;


    @EventListener
    public void ModalityStarted(StudentModalityStarted event){

        StudentModality modality = studentModalityRepository.findById(event.getStudentModalityId()).orElseThrow();

        User student = modality.getStudent();

        String subject =
                "Modalidad iniciada – SIGMA";

        String message = """
                Hola %s,

                Te informamos que tu modalidad de grado:

                "%s"

                ha sido oficialmente iniciada.

                Estado actual:
                EN PROGRESO: A la espera de aprobación por secretaría y concejo académico.

            
                Sistema SIGMA
                """.formatted(
                student.getName(),
                modality.getModality().getName()
        );



        Notification notification = Notification.builder()
                .type(NotificationType.MODALITY_STARTED)
                .recipientType(NotificationRecipientType.STUDENT)
                .recipient(student)
                .triggeredBy(null)
                .studentModality(modality)
                .subject(subject)
                .message(message)
                .createdAt(LocalDateTime.now())
                .build();
        notificationRepository.save(notification);
        dispatcher.dispatch(notification);
    }

    @EventListener
    public void onDocumentCorrectionsRequested(DocumentCorrectionsRequestedEvent event) {

        StudentDocument document = studentDocumentRepository.findById(event.getStudentDocumentId())
                .orElseThrow();

        User student = document.getStudentModality().getStudent();

        String subject = "Correcciones solicitadas en un documento";

        String message = """
                Hola %s,

                %s ha solicitado correcciones en el documento:

                "%s"

                Observaciones:
                %s

                Por favor, ingresa a SIGMA y corrige el documento para continuar con tu proceso.

                Sistema SIGMA
                """.formatted(
                student.getName(),
                event.getRequestedBy() == NotificationRecipientType.SECRETARY
                        ? "La Secretaría"
                        : "El Concejo Académico",
                document.getDocumentConfig().getDocumentName(),
                event.getObservations()
        );

        Notification notification = Notification.builder()
                .type(NotificationType.DOCUMENT_CORRECTIONS_REQUESTED)
                .recipientType(NotificationRecipientType.STUDENT)
                .recipient(student)
                .triggeredBy(null)
                .studentModality(document.getStudentModality())
                .subject(subject)
                .message(message)
                .createdAt(LocalDateTime.now())
                .build();

        notificationRepository.save(notification);
        dispatcher.dispatch(notification);
    }

    @EventListener
    public void onCancellationRequested(CancellationRequestedEvent event){
        StudentModality sm = studentModalityRepository.findById(event.getStudentModalityId())
                .orElseThrow();

        User student = sm.getStudent();

        String subject = "Solicitud de cancelación registrada";

        String message = """
                Hola %s,

                Tu solicitud de cancelación de la modalidad de grado:

                "%s"

                ha sido registrada correctamente y será evaluada
                por el concejo Académico.

                Sistema SIGMA
                """.formatted(
                student.getName(),
                sm.getModality().getName()
        );

        Notification notification = Notification.builder()
                .type(NotificationType.MODALITY_CANCELLATION_REQUESTED)
                .recipientType(NotificationRecipientType.STUDENT)
                .recipient(sm.getStudent())
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
    public void onCancellationApproved(CancellationApprovedEvent event) {

        StudentModality sm = studentModalityRepository.findById(event.getStudentModalityId())
                        .orElseThrow();

        User student = sm.getStudent();

        String subject = "Cancelación de modalidad APROBADA";

        String message = """
                Hola %s,

                El concejo académico ha APROBADO la cancelación
                de tu modalidad de grado:

                "%s"

                La modalidad queda cerrada oficialmente.

                Sistema SIGMA
                """.formatted(
                student.getName(),
                sm.getModality().getName()
        );


        Notification notification = Notification.builder()
                .type(NotificationType.MODALITY_CANCELLATION_APPROVED)
                .recipientType(NotificationRecipientType.STUDENT)
                .recipient(sm.getStudent())
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
    public void onCancellationRejected(CancellationRejectedEvent event){
        StudentModality sm = studentModalityRepository.findById(event.getStudentModalityId())
                .orElseThrow();

        User student = sm.getStudent();

        String subject = "Solicitud de cancelación RECHAZADA";

        String message = """
                Hola %s,

                El concejo Académico ha RECHAZADO tu solicitud
                de cancelación de la modalidad:

                "%s"

                Motivo:
                %s

                Para mayor información, comunícate con Secretaría.

                Sistema SIGMA
                """.formatted(
                student.getName(),
                sm.getModality().getName(),
                event.getReason()
        );



        Notification notification = Notification.builder()
                .type(NotificationType.MODALITY_CANCELLATION_REJECTED)
                .recipientType(NotificationRecipientType.STUDENT)
                .recipient(sm.getStudent())
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

        User student = modality.getStudent();
        User director = modality.getProjectDirector();

        String studentSubject =
                "Sustentación programada – Modalidad de Grado";

        String studentMessage = """
                Hola %s,

                Te informamos que la sustentación de tu modalidad de grado
                ha sido programada con la siguiente información:

                Modalidad:
                "%s"

                Fecha y hora:
                %s

                Lugar:
                %s

                Director asignado:
                %s

                Por favor preséntate con antelación y cumple
                con los lineamientos académicos establecidos.

                Sistema SIGMA
                """.formatted(
                student.getName(),
                modality.getModality().getName(),
                event.getDefenseDate(),
                event.getDefenseLocation(),
                director != null
                        ? director.getName() + " " + director.getLastName()
                        : "No asignado"
        );



        Notification notification = Notification.builder()
                .type(NotificationType.DEFENSE_SCHEDULED)
                .recipientType(NotificationRecipientType.STUDENT)
                .recipient(student)
                .triggeredBy(null)
                .studentModality(modality)
                .subject(studentSubject)
                .message(studentMessage)
                .createdAt(LocalDateTime.now())
                .build();
        notificationRepository.save(notification);
        dispatcher.dispatch(notification);

    }

    @EventListener
    public void DirectorAssigned(DirectorAssignedEvent event){

        StudentModality modality = studentModalityRepository.findById(event.getStudentModalityId())
                        .orElseThrow();

        User student = modality.getStudent();
        User director = userRepository.findById(event.getDirectorId())
                .orElseThrow();

        String studentSubject =
                "Director asignado a tu modalidad – SIGMA";

        String studentMessage = """
                Hola %s,

                Te informamos que se ha asignado un Director de Proyecto
                a tu modalidad de grado:

                Modalidad:
                "%s"

                Director asignado:
                %s (%s)

                A partir de este momento podrás continuar el proceso académico
                bajo su acompañamiento.

                Sistema SIGMA
                """.formatted(
                student.getName(),
                modality.getModality().getName(),
                director.getName() + " " + director.getLastName(),
                director.getEmail()
        );


        Notification notification = Notification.builder()
                .type(NotificationType.DIRECTOR_ASSIGNED)
                .recipientType(NotificationRecipientType.STUDENT)
                .recipient(student)
                .triggeredBy(null)
                .studentModality(modality)
                .subject(studentSubject)
                .message(studentMessage)
                .createdAt(LocalDateTime.now())
                .build();
        notificationRepository.save(notification);
        dispatcher.dispatch(notification);

    }

    @EventListener
    public void handleDefenseResult(FinalDefenseResultEvent event){

        StudentModality modality = studentModalityRepository.findById(event.getStudentModalityId())
                        .orElseThrow();

        User student = modality.getStudent();

        boolean approved = event.getFinalStatus() == ModalityProcessStatus.GRADED_APPROVED;

        String studentSubject = approved
                ? "Resultado final de sustentación – Modalidad aprobada"
                : "Resultado final de sustentación – Modalidad no aprobada";

        String studentMessage = approved
                ? buildApprovedStudentMessage(student, modality, event)
                : buildRejectedStudentMessage(student, modality, event);



        Notification notification = Notification.builder()
                .type(NotificationType.DEFENSE_COMPLETED)
                .recipientType(NotificationRecipientType.STUDENT)
                .recipient(student)
                .triggeredBy(null)
                .studentModality(modality)
                .subject(studentSubject)
                .message(studentMessage)
                .createdAt(LocalDateTime.now())
                .build();
        notificationRepository.save(notification);
        dispatcher.dispatch(notification);
    }

    private String buildApprovedStudentMessage(User student, StudentModality modality, FinalDefenseResultEvent event) {
        return """
                Hola %s,

                Felicitaciones! Has aprobado la sustentación
                de tu modalidad de grado:

                "%s"

                Mención académica obtenida:
                %s

                Observaciones:
                %s

                Próximos pasos:
                Por favor comunícate con Secretaría para
                completar los trámites finales.

                Sistema SIGMA
                """.formatted(
                student.getName(),
                modality.getModality().getName(),
                event.getAcademicDistinction() != null
                        ? event.getAcademicDistinction().name()
                        : "Ninguna",
                event.getObservations() != null && !event.getObservations().isBlank()
                        ? event.getObservations()
                        : "Ninguna"
        );
    }

    private String buildRejectedStudentMessage(User student, StudentModality modality, FinalDefenseResultEvent event) {
        return """
                Hola %s,

                Lamentamos informarte que no has aprobado
                la sustentación de tu modalidad de grado:

                "%s"

                Observaciones:
                %s

                Te recomendamos revisar las observaciones
                y comunicarte con tu director de proyecto
                para definir los próximos pasos a seguir.

                Sistema SIGMA
                """.formatted(
                student.getName(),
                modality.getModality().getName(),
                event.getObservations() != null && !event.getObservations().isBlank()
                        ? event.getObservations()
                        : "Ninguna"
        );
    }

    @EventListener
    public void ModalityApprovedByCouncil(ModalityApprovedByCouncilEvent event) {

        StudentModality modality = studentModalityRepository.findById(event.getStudentModalityId()).orElseThrow();

        User student = modality.getStudent();

        String subject =
                "Modalidad APROBADA por el Concejo académico – SIGMA";

        String message = """
                Hola %s,

                Nos complace informarte que tu modalidad de grado:

                "%s"

                ha sido APROBADA por el Concejo del programa.

                Estado actual:
                PROPUESTA APROBADA

                Director asignado:
                %s

                Usted puede empezar a trabajar en su proyecto de grado bajo la dirección asignada.

                Fecha de aprobación:
                %s

                Sistema SIGMA
                """.formatted(
                student.getName(),
                modality.getModality().getName(),
                modality.getProjectDirector() != null
                        ? modality.getProjectDirector().getName() + " " +
                        modality.getProjectDirector().getLastName()
                        : "Aún no asignado",
                modality.getUpdatedAt()
        );



        Notification notification = Notification.builder()
                .type(NotificationType.MODALITY_APPROVED_BY_COUNCIL)
                .recipientType(NotificationRecipientType.STUDENT)
                .recipient(student)
                .triggeredBy(null)
                .studentModality(modality)
                .subject(subject)
                .message(message)
                .createdAt(LocalDateTime.now())
                .build();
        notificationRepository.save(notification);
        dispatcher.dispatch(notification);
    }

    @EventListener
    public void ModalityApprovedBySecretary(ModalityApprovedBySecretary event) {

        StudentModality modality = studentModalityRepository.findById(event.getStudentModalityId()).orElseThrow();

        User student = modality.getStudent();

        String subject =
                "Modalidad APROBADA por Secretaría – SIGMA";

        String message = """
                Hola %s,

                Nos complace informarte que tu modalidad de grado:

                "%s"

                ha sido APROBADA por Secretaría.

                Próximos pasos:
                Por favor espera la aprobación por parte del concejo académico para continuar con el proceso académico.

                Sistema SIGMA
                """.formatted(
                student.getName(),
                modality.getModality().getName()
        );



        Notification notification = Notification.builder()
                .type(NotificationType.MODALITY_APPROVED_BY_SECRETARY)
                .recipientType(NotificationRecipientType.STUDENT)
                .recipient(student)
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
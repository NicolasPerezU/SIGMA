package com.SIGMA.USCO.notifications.listeners;

import com.SIGMA.USCO.Modalities.Entity.AcademicCertificate;
import com.SIGMA.USCO.Modalities.Entity.StudentModality;
import com.SIGMA.USCO.Modalities.Entity.enums.CertificateStatus;
import com.SIGMA.USCO.Modalities.Entity.enums.ModalityProcessStatus;
import com.SIGMA.USCO.Modalities.Repository.StudentModalityMemberRepository;
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
import com.SIGMA.USCO.notifications.service.AcademicCertificatePdfService;
import com.SIGMA.USCO.notifications.service.NotificationDispatcherService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class StudentNotificationListener {

    private final StudentModalityRepository studentModalityRepository;
    private final StudentModalityMemberRepository studentModalityMemberRepository;
    private final NotificationRepository notificationRepository;
    private final NotificationDispatcherService dispatcher;
    private final UserRepository userRepository;
    private final StudentDocumentRepository studentDocumentRepository;
    private final AcademicCertificatePdfService certificatePdfService;


    @EventListener
    public void ModalityStarted(StudentModalityStarted event){

        StudentModality modality = studentModalityRepository.findById(event.getStudentModalityId()).orElseThrow();

        User student = modality.getLeader();

        String subject =
                "Modalidad iniciada – SIGMA";

        String message = """
        Estimado/a %s,

        Nos permitimos informarte que tu modalidad de grado:

        "%s"

        ha sido **iniciada oficialmente** en el sistema.

        **Estado actual del proceso:**
        En progreso. La modalidad se encuentra pendiente de revisión y
        aprobación por parte de la Jefatura de Programa y el Comité de
        Currículo del programa académico correspondiente.

        Te recomendamos estar atento/a a las notificaciones del sistema,
        ya que a través de este medio se te informará cualquier actualización
        o requerimiento adicional.

        Cordialmente,

        Sistema interno de gestión académica de la universidad Surcolombiana.
        """.formatted(
                student.getName(),
                modality.getProgramDegreeModality().getDegreeModality().getName()
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

        User student = document.getStudentModality().getLeader();

        String subject = "Solicitud de correcciones en documento académico";

        String message = """
                Estimado/a %s,
                
                Te informamos que %s ha solicitado la realización de correcciones en el siguiente documento asociado a tu modalidad de grado:
                
                “%s”
                
                Observaciones realizadas:
                %s
                
                Te solicitamos ingresar a la plataforma institucional y atender las observaciones indicadas, con el fin de continuar oportunamente con tu proceso académico.
                
                Cordialmente,
                Sistema de Gestión Académica
                """.formatted(
                student.getName(),
                event.getRequestedBy() == NotificationRecipientType.PROGRAM_HEAD
                        ? "La jefatura de programa"
                        : "El Comité de currículo de programa",
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

        User student = sm.getLeader();

        String subject = "Solicitud de cancelación de modalidad de grado registrada";

        String message = """
                Estimado/a %s,
                
                Te informamos que tu solicitud de cancelación de la modalidad de grado:
                
                “%s”
                
                ha sido registrada correctamente.
                
                Dicha solicitud será evaluada por tu director de proyecto y posteriormente por el Comité de Currículo del programa académico correspondiente. Una vez se emita una decisión, serás notificado/a oportunamente a través de este medio.
                
                Cordialmente,
                Sistema de Gestión Académica
                """.formatted(
                student.getName(),
                sm.getProgramDegreeModality().getDegreeModality().getName()
        );

        Notification notification = Notification.builder()
                .type(NotificationType.MODALITY_CANCELLATION_REQUESTED)
                .recipientType(NotificationRecipientType.STUDENT)
                .recipient(sm.getLeader())
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

        User student = sm.getLeader();

        String subject = "Cancelación de modalidad de grado aprobada";

        String message = """
                Estimado/a %s,
                
                Te informamos que el Comité de Currículo del programa académico ha aprobado la solicitud de cancelación de la modalidad de grado:
                
                “%s”
                
                En consecuencia, la modalidad queda cerrada de manera oficial y el proceso académico correspondiente finaliza a partir de esta decisión.
                
                Si requieres información adicional o tienes inquietudes relacionadas con tu situación académica, te recomendamos comunicarte con la jefatura de tu programa.
                
                Cordialmente,
                Sistema de Gestión Académica
                """.formatted(
                student.getName(),
                sm.getProgramDegreeModality().getDegreeModality().getName()
        );


        Notification notification = Notification.builder()
                .type(NotificationType.MODALITY_CANCELLATION_APPROVED)
                .recipientType(NotificationRecipientType.STUDENT)
                .recipient(sm.getLeader())
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

        User student = sm.getLeader();

        String subject = "Solicitud de cancelación de modalidad de grado rechazada";

        String message = """
                Estimado/a %s,
                
                Te informamos que el Comité de Currículo del programa académico ha rechazado tu solicitud de cancelación de la modalidad de grado:
                
                “%s”
                
                Motivo de la decisión:
                %s
                
                Tu modalidad de grado continuará activa bajo las condiciones previamente establecidas. Para mayor claridad sobre esta decisión o para recibir orientación adicional, te sugerimos comunicarte con la jefatura de tu programa académico.
                
                Cordialmente,
                Sistema de Gestión Académica
                """.formatted(
                student.getName(),
                sm.getProgramDegreeModality().getDegreeModality().getName(),
                event.getReason()
        );



        Notification notification = Notification.builder()
                .type(NotificationType.MODALITY_CANCELLATION_REJECTED)
                .recipientType(NotificationRecipientType.STUDENT)
                .recipient(sm.getLeader())
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

        User student = modality.getLeader();
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

        Recuerda que **con al menos tres (3) días hábiles de anticipación**
        a la fecha de la sustentación, debes realizar la **divulgación de tu
        proyecto** en **lugares visibles y de acceso público**, conforme a
        los lineamientos establecidos por el programa académico.

        Por favor preséntate con antelación y cumple
        con los lineamientos académicos establecidos.

        Sistema SIGMA
        """.formatted(
                student.getName(),
                modality.getProgramDegreeModality().getDegreeModality().getName(),
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

        User student = modality.getLeader();
        User director = userRepository.findById(event.getDirectorId())
                .orElseThrow();

        String studentSubject =
                "Asignación de Director de Proyecto a modalidad de grado";

        String studentMessage = """
                Estimado/a %s,
                
                Te informamos que ha sido asignado oficialmente un Director de Proyecto para tu modalidad de grado, conforme a los lineamientos académicos establecidos.
                
                Modalidad de grado:
                “%s”
                
                Director asignado:
                %s
                Correo electrónico: %s
                
                A partir de este momento, podrás continuar el desarrollo de tu modalidad de grado bajo el acompañamiento académico del director asignado, quien será tu principal orientador durante el proceso.
                
                Te recomendamos establecer contacto oportuno para coordinar las actividades iniciales y definir el plan de trabajo correspondiente.
                
                Cordialmente,
                Sistema de Gestión Académica
                """.formatted(
                student.getName(),
                modality.getProgramDegreeModality().getDegreeModality().getName(),
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

        User student = modality.getLeader();

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


        if (approved) {
            try {
                log.info("Generando acta de aprobación para la modalidad ID: {}", modality.getId());


                AcademicCertificate certificate = certificatePdfService.generateCertificate(modality);


                Path pdfPath = certificatePdfService.getCertificatePath(modality.getId());


                dispatcher.dispatchWithAttachment(
                        notification,
                        pdfPath,
                        "ACTA_DE_APROBACION" + ".pdf"
                );


                certificatePdfService.updateCertificateStatus(certificate.getId(), CertificateStatus.SENT);

                log.info("Acta PDF generada y enviada exitosamente para la modalidad ID: {}", modality.getId());

            } catch (IOException e) {
                log.error("Error generando o enviando acta PDF para modalidad ID {}: {}",
                        modality.getId(), e.getMessage(), e);


                dispatcher.dispatch(notification);
            }
        } else {

            dispatcher.dispatch(notification);
        }
    }

    private String buildApprovedStudentMessage(User student, StudentModality modality, FinalDefenseResultEvent event) {
        return """
                Estimado/a %s,
                
                Recibe un cordial saludo.
                
                Nos permitimos informarte que, una vez realizada la sustentación y evaluados los resultados por los jurados designados, has aprobado la sustentación de tu modalidad de grado:
                
                "%s"
                
                Mención académica obtenida:
                %s
                
                Observaciones registradas:
                %s
                
                Adjunto a este correo encontrarás el ACTA DE APROBACIÓN en formato PDF, documento oficial que certifica la culminación exitosa de tu modalidad de grado conforme a la normatividad académica vigente.
                
                Próximos pasos:
                Para la culminación del proceso académico, te solicitamos comunicarte con la jefatura de tu programa, con el fin de adelantar los trámites finales correspondientes.
                
                Felicitaciones por este importante logro académico.
                
                Cordialmente,
                Sistema de Gestión Académica
                Universidad Surcolombiana
                """.formatted(
                student.getName(),
                modality.getProgramDegreeModality().getDegreeModality().getName(),
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
                Estimado/a %s,
                
                Recibe un cordial saludo.
                
                Una vez realizada la sustentación y efectuado el proceso de evaluación por parte de los jurados, te informamos que tu modalidad de grado:
                
                “%s”
                
                no ha sido aprobada en esta oportunidad.
                
                Observaciones registradas:
                %s
                
                Te recomendamos revisar cuidadosamente las observaciones consignadas y comunicarte con tu Director de Proyecto, así como con la jefatura del programa académico, para definir los pasos a seguir conforme a la normatividad vigente.
                
                Cordialmente,
                Sistema de Gestión Académica
                """.formatted(
                student.getName(),
                modality.getProgramDegreeModality().getDegreeModality().getName(),
                event.getObservations() != null && !event.getObservations().isBlank()
                        ? event.getObservations()
                        : "Ninguna"
        );
    }

    @EventListener
    public void ModalityApprovedByCommittee(ModalityApprovedByCommitteeEvent event) {

        StudentModality modality = studentModalityRepository.findById(event.getStudentModalityId()).orElseThrow();

        User student = modality.getLeader();

        String subject =
                "Modalidad de grado aprobada por el Comité de Currículo del programa académico";

        String message = """
                Estimado/a %s,
                
                Recibe un cordial saludo.
                
                Nos complace informarte que tu modalidad de grado:
                
                “%s”
                
                ha sido aprobada por el comité de currículo del programa, tras la revisión realizada por las instancias académicas competentes.
                
                Estado actual del proceso:
                Propuesta aprobada
                
                Director de Proyecto:
                %s
                
                Actualmente, tu modalidad ha pasado a la etapa de aprobación por parte del jurado asignado por el comité. Debes estar atento a las notificaciones y a las indicaciones del sistema para continuar con el proceso.
                
                Fecha de aprobación por el comité:
                %s
                
                Te recomendamos mantener comunicación constante con tu Director de Proyecto y con la jefatura del programa para el adecuado seguimiento del proceso.
                
                Cordialmente,
                Sistema de Gestión Académica
                """.formatted(
                student.getName(),
                modality.getProgramDegreeModality().getDegreeModality().getName(),
                modality.getProjectDirector() != null
                        ? modality.getProjectDirector().getName() + " " +
                        modality.getProjectDirector().getLastName()
                        : "Aún no asignado",
                modality.getUpdatedAt()
        );



        Notification notification = Notification.builder()
                .type(NotificationType.MODALITY_APPROVED_BY_PROGRAM_CURRICULUM_COMMITTEE)
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
    public void ModalityApprovedByProgramHead(ModalityApprovedByProgramHead event) {

        StudentModality modality = studentModalityRepository.findById(event.getStudentModalityId()).orElseThrow();

        User student = modality.getLeader();

        String subject =
                "Modalidad de grado aprobada por Jefatura de programa";

        String message = """
                Estimado/a %s,
                
                Recibe un cordial saludo.
                
                Nos complace informarte que tu modalidad de grado:
                
                “%s”
                
                ha sido aprobada por la Jefatura del programa académico.
                
                Próximos pasos:
                Por favor, espera la aprobación por parte del Comité de Currículo del programa para continuar con el proceso académico.
                
                Te recomendamos estar atento/a a futuras notificaciones y mantener comunicación con la jefatura de tu programa para cualquier consulta.
                
                Cordialmente,
                Sistema de Gestión Académica
                """.formatted(
                student.getName(),
                modality.getProgramDegreeModality().getDegreeModality().getName()
        );



        Notification notification = Notification.builder()
                .type(NotificationType.MODALITY_APPROVED_BY_PROGRAM_HEAD)
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

    // Listeners para gestión de correcciones

    @EventListener
    public void handleCorrectionDeadlineReminder(CorrectionDeadlineReminderEvent event) {
        StudentModality modality = studentModalityRepository.findById(event.getStudentModalityId())
                .orElseThrow();

        User student = modality.getLeader();

        String subject = "Recordatorio: Plazo de correcciones – " + event.getDaysRemaining() + " días restantes";

        String message = """
                Estimado/a %s,
                
                Recibe un cordial saludo.
                
                Te recordamos que tienes correcciones solicitadas pendientes para tu modalidad de grado:
                
                "%s"
                
                ⚠️ IMPORTANTE - PLAZO LÍMITE:
                Días restantes: %d días
                Fecha límite: %s
                
                Debes resubir el documento corregido antes de la fecha límite. Si no lo haces, tu modalidad será CANCELADA AUTOMÁTICAMENTE.
                
                ¿Qué hacer?
                1. Realiza las correcciones solicitadas en tu documento
                2. Ingresa al sistema SIGMA
                3. Ve a "Mis Modalidades"
                4. Selecciona tu modalidad y resubir el documento corregido
                
                Si tienes alguna duda o inconveniente, comunícate urgentemente con la jefatura de tu programa académico.
                
                Cordialmente,
                Sistema de Gestión Académica
                """.formatted(
                student.getName(),
                modality.getProgramDegreeModality().getDegreeModality().getName(),
                event.getDaysRemaining(),
                event.getDeadline().toLocalDate()
        );

        Notification notification = Notification.builder()
                .type(NotificationType.CORRECTION_DEADLINE_REMINDER)
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

        log.info("Recordatorio de plazo de corrección enviado al estudiante {}", student.getId());
    }

    @EventListener
    public void handleCorrectionDeadlineExpired(CorrectionDeadlineExpiredEvent event) {
        StudentModality modality = studentModalityRepository.findById(event.getStudentModalityId())
                .orElseThrow();

        User student = modality.getLeader();

        String subject = "MODALIDAD CANCELADA - Vencimiento de plazo de correcciones";

        String message = """
                Estimado/a %s,
                
                Recibe un cordial saludo.
                
                Lamentamos informarte que tu modalidad de grado:
                
                "%s"
                
                ha sido CANCELADA AUTOMÁTICAMENTE por vencimiento del plazo establecido para entregar las correcciones solicitadas.
                
                Detalles:
                - Fecha de solicitud de correcciones: %s
                - Plazo límite: 30 días
                - Estado final: CANCELADA
                
                Próximos pasos:
                Para continuar con tu proceso de grado, deberás:
                1. Seleccionar una nueva modalidad de grado
                2. Iniciar el proceso desde el principio
                3. Cumplir con todos los requisitos establecidos
                
                Te recomendamos comunicarte con la jefatura de tu programa académico para recibir orientación sobre cómo proceder.
                
                Cordialmente,
                Sistema de Gestión Académica
                """.formatted(
                student.getName(),
                modality.getProgramDegreeModality().getDegreeModality().getName(),
                event.getRequestDate().toLocalDate()
        );

        Notification notification = Notification.builder()
                .type(NotificationType.CORRECTION_DEADLINE_EXPIRED)
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

        log.info("Notificación de cancelación por vencimiento enviada al estudiante {}", student.getId());
    }

    @EventListener
    public void handleCorrectionResubmitted(CorrectionResubmittedEvent event) {
        StudentModality modality = studentModalityRepository.findById(event.getStudentModalityId())
                .orElseThrow();

        User student = modality.getLeader();

        String subject = "Documento corregido enviado exitosamente";

        String message = """
                Estimado/a %s,
                
                Recibe un cordial saludo.
                
                Confirmamos que hemos recibido tu documento corregido:
                
                Documento: %s
                Modalidad: "%s"
                Fecha de envío: %s
                
                Tu documento será revisado por el jurado correspondiente. Te notificaremos el resultado de la revisión a la brevedad posible.
                
                Estado actual: CORRECCIONES ENVIADAS - PENDIENTE DE REVISIÓN
                
                Gracias por tu compromiso con tu proceso académico.
                
                Cordialmente,
                Sistema de Gestión Académica
                """.formatted(
                student.getName(),
                event.getDocumentName(),
                modality.getProgramDegreeModality().getDegreeModality().getName(),
                LocalDateTime.now().toLocalDate()
        );

        Notification notification = Notification.builder()
                .type(NotificationType.CORRECTION_RESUBMITTED)
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

        log.info("Notificación de resubmisión de corrección enviada al estudiante {}", student.getId());
    }

    @EventListener
    public void handleCorrectionApproved(CorrectionApprovedEvent event) {
        StudentModality modality = studentModalityRepository.findById(event.getStudentModalityId())
                .orElseThrow();

        User student = modality.getLeader();

        String subject = "¡Felicitaciones! Correcciones aprobadas";

        String message = """
                Estimado/a %s,
                
                Recibe un cordial saludo.
                
                Nos complace informarte que las correcciones enviadas han sido APROBADAS.
                
                Documento: %s
                Modalidad: "%s"
                
                ✅ Estado: CORRECCIONES APROBADAS
                
                Tu modalidad de grado continúa su proceso normal. Te notificaremos sobre los siguientes pasos a seguir.
                
                Felicitaciones por tu compromiso y dedicación en este proceso académico.
                
                Cordialmente,
                Sistema de Gestión Académica
                """.formatted(
                student.getName(),
                event.getDocumentName(),
                modality.getProgramDegreeModality().getDegreeModality().getName()
        );

        Notification notification = Notification.builder()
                .type(NotificationType.CORRECTION_APPROVED)
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

        log.info("Notificación de aprobación de corrección enviada al estudiante {}", student.getId());
    }

    @EventListener
    public void handleCorrectionRejectedFinal(CorrectionRejectedFinalEvent event) {
        StudentModality modality = studentModalityRepository.findById(event.getStudentModalityId())
                .orElseThrow();

        User student = modality.getLeader();

        String subject = "MODALIDAD CANCELADA - Correcciones rechazadas";

        String message = """
                Estimado/a %s,
                
                Recibe un cordial saludo.
                
                Lamentamos informarte que tu modalidad de grado:
                
                "%s"
                
                ha sido CANCELADA después de la revisión de las correcciones enviadas.
                
                Documento: %s
                Estado final: RECHAZADO - MODALIDAD CANCELADA
                
                Motivo del rechazo:
                %s
                
                Próximos pasos:
                Para continuar con tu proceso de grado, deberás:
                1. Seleccionar una nueva modalidad de grado
                2. Iniciar el proceso desde el principio
                3. Cumplir con todos los requisitos establecidos
                
                Te recomendamos comunicarte con la jefatura de tu programa académico para recibir retroalimentación detallada y orientación sobre cómo proceder.
                
                Cordialmente,
                Sistema de Gestión Académica
                """.formatted(
                student.getName(),
                modality.getProgramDegreeModality().getDegreeModality().getName(),
                event.getDocumentName(),
                event.getReason()
        );

        Notification notification = Notification.builder()
                .type(NotificationType.CORRECTION_REJECTED_FINAL)
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

        log.info("Notificación de rechazo final de corrección enviada al estudiante {}", student.getId());
    }

    @EventListener
    public void handleModalityClosedByCommittee(ModalityClosedByCommitteeEvent event) {
        StudentModality modality = studentModalityRepository.findById(event.getStudentModalityId())
                .orElseThrow();

        User student = modality.getLeader();
        User committeeMember = userRepository.findById(event.getCommitteeMemberId())
                .orElseThrow();

        String subject = "MODALIDAD CERRADA - Decisión del Comité de Currículo";

        String message = """
                Estimado/a %s,
                
                Recibe un cordial saludo.
                
                Te informamos que tu modalidad de grado:
                
                "%s"
                
                ha sido CERRADA por decisión del Comité de Currículo del Programa.
                
                Programa académico: %s
                Estado: MODALIDAD CERRADA
                Decisión tomada por: %s %s
                Fecha: %s
                
                Motivo del cierre:
                %s
                
                Próximos pasos:
                Para continuar con tu proceso de grado, te recomendamos:
                1. Comunicarte con la jefatura de tu programa académico para obtener más detalles
                2. Recibir asesoría sobre las opciones disponibles
                3. En caso de ser necesario, iniciar una nueva modalidad de grado
                
                Si tienes dudas o deseas recibir retroalimentación adicional, por favor comunícate con la coordinación de tu programa académico.
                
                Cordialmente,
                Sistema de Gestión Académica
                Universidad Surcolombiana
                """.formatted(
                student.getName(),
                modality.getProgramDegreeModality().getDegreeModality().getName(),
                modality.getAcademicProgram().getName(),
                committeeMember.getName(),
                committeeMember.getLastName(),
                LocalDateTime.now().toString(),
                event.getReason()
        );

        Notification notification = Notification.builder()
                .type(NotificationType.MODALITY_CLOSED_BY_COMMITTEE)
                .recipientType(NotificationRecipientType.STUDENT)
                .recipient(student)
                .triggeredBy(committeeMember)
                .studentModality(modality)
                .subject(subject)
                .message(message)
                .createdAt(LocalDateTime.now())
                .build();
        notificationRepository.save(notification);
        dispatcher.dispatch(notification);

        log.info("Notificación de cierre de modalidad por comité enviada al estudiante {}", student.getId());
    }

    @EventListener
    public void onModalityInvitationSent(ModalityInvitationSentEvent event) {

        StudentModality modality = studentModalityRepository.findById(event.getStudentModalityId())
                .orElseThrow(() -> new RuntimeException("Modalidad no encontrada"));

        User invitee = userRepository.findById(event.getInviteeId())
                .orElseThrow(() -> new RuntimeException("Estudiante invitado no encontrado"));

        User inviter = userRepository.findById(event.getInviterId())
                .orElseThrow(() -> new RuntimeException("Estudiante que invita no encontrado"));

        String subject = "Invitación para unirte a una modalidad de grado grupal – SIGMA";

        String message = """
                Estimado/a %s,
                
                Has recibido una invitación para unirte a una modalidad de grado grupal.
                
                **Detalles de la invitación:**
                
                - **Modalidad:** %s
                - **Programa académico:** %s
                - **Invitado por:** %s
                - **Fecha de invitación:** %s
                
                **¿Qué significa esto?**
                
                %s te ha invitado a formar parte de su grupo para desarrollar la modalidad de grado de manera colaborativa. 
                Si aceptas esta invitación, formarás parte del equipo y podrás trabajar en conjunto en todos los documentos 
                y actividades requeridas para completar la modalidad.
                
                **Consideraciones importantes:**
                
                - Solo puedes pertenecer a una modalidad de grado a la vez.
                - Al aceptar la invitación, te comprometes a trabajar de forma colaborativa con el grupo.
                - Puedes aceptar o rechazar la invitación desde la plataforma.
                
                
                **¿Cómo responder?**
                
                1. Ingresa a la plataforma SIGMA
                2. Dirígete a la sección de "Mis Invitaciones" o "Notificaciones"
                3. Revisa los detalles de la invitación
                4. Acepta o rechaza según tu decisión
                
                Te recomendamos coordinar con %s antes de tomar una decisión, para asegurar 
                que todos los miembros del grupo estén alineados con los objetivos y compromisos del proyecto.
                
                Cordialmente,
                
                Sistema Interno de Gestión Académica
                Universidad Surcolombiana - SIGMA
                """.formatted(
                invitee.getName(),
                modality.getProgramDegreeModality().getDegreeModality().getName(),
                modality.getAcademicProgram().getName(),
                inviter.getName() + " " + inviter.getLastName(),
                LocalDateTime.now().toString(),
                inviter.getName() + " " + inviter.getLastName(),
                inviter.getName()
        );

        Notification notification = Notification.builder()
                .type(NotificationType.MODALITY_INVITATION_RECEIVED)
                .recipientType(NotificationRecipientType.STUDENT)
                .recipient(invitee)
                .triggeredBy(inviter)
                .studentModality(modality)
                .invitationId(event.getInvitationId())
                .subject(subject)
                .message(message)
                .createdAt(LocalDateTime.now())
                .build();

        notificationRepository.save(notification);
        dispatcher.dispatch(notification);

        log.info("Notificación de invitación a modalidad grupal enviada al estudiante {} por el estudiante {}",
                invitee.getId(), inviter.getId());
    }


    @EventListener
    public void onModalityInvitationAccepted(ModalityInvitationAcceptedEvent event) {

        StudentModality modality = studentModalityRepository.findById(event.getStudentModalityId())
                .orElseThrow(() -> new RuntimeException("Modalidad no encontrada"));

        User acceptedBy = userRepository.findById(event.getAcceptedById())
                .orElseThrow(() -> new RuntimeException("Estudiante que aceptó no encontrado"));

        User leader = userRepository.findById(event.getLeaderId())
                .orElseThrow(() -> new RuntimeException("Líder del grupo no encontrado"));

        String subject = "Un estudiante aceptó tu invitación a la modalidad grupal – SIGMA";

        String message = """
                Estimado/a %s,
                
                ¡Buenas noticias! Un estudiante ha aceptado tu invitación para unirse a tu modalidad de grado grupal.
                
                **Detalles de la aceptación:**
                
                - **Estudiante:** %s
                - **Modalidad:** %s
                - **Programa académico:** %s
                - **Fecha de aceptación:** %s
                
                **¿Qué sigue ahora?**
                
                %s ahora es parte oficial de tu grupo de modalidad. Pueden trabajar juntos en:
                
                - Subir y actualizar documentos compartidos.
                - Coordinar actividades y entregas.
                - Preparar presentaciones y sustentaciones en equipo.
                
                **Próximos pasos:**
                
                1. Coordina con tu equipo los roles y responsabilidades
                2. Establece canales de comunicación efectivos
                3. Planifica el desarrollo del proyecto o actividad
                4. Comienza a trabajar en los documentos requeridos
                
                Recuerda que todos los miembros del grupo tienen los mismos derechos y responsabilidades 
                dentro de la modalidad, y todos pueden subir o actualizar los documentos necesarios.
                
                ¡Mucho éxito en su trabajo colaborativo!
                
                Cordialmente,
                
                Sistema Interno de Gestión Académica
                Universidad Surcolombiana - SIGMA
                """.formatted(
                leader.getName(),
                acceptedBy.getName() + " " + acceptedBy.getLastName(),
                modality.getProgramDegreeModality().getDegreeModality().getName(),
                modality.getAcademicProgram().getName(),
                LocalDateTime.now().toString(),
                acceptedBy.getName()
        );

        Notification notification = Notification.builder()
                .type(NotificationType.MODALITY_INVITATION_ACCEPTED)
                .recipientType(NotificationRecipientType.STUDENT)
                .recipient(leader)
                .triggeredBy(acceptedBy)
                .studentModality(modality)
                .subject(subject)
                .message(message)
                .createdAt(LocalDateTime.now())
                .build();

        notificationRepository.save(notification);
        dispatcher.dispatch(notification);

        log.info("Notificación de aceptación de invitación enviada al líder {} por el estudiante {}",
                leader.getId(), acceptedBy.getId());
    }


    @EventListener
    public void onModalityInvitationRejected(ModalityInvitationRejectedEvent event) {

        StudentModality modality = studentModalityRepository.findById(event.getStudentModalityId())
                .orElseThrow(() -> new RuntimeException("Modalidad no encontrada"));

        User rejectedBy = userRepository.findById(event.getRejectedById())
                .orElseThrow(() -> new RuntimeException("Estudiante que rechazó no encontrado"));

        User leader = userRepository.findById(event.getLeaderId())
                .orElseThrow(() -> new RuntimeException("Líder del grupo no encontrado"));

        String subject = "Un estudiante rechazó tu invitación a la modalidad grupal – SIGMA";

        String message = """
                Estimado/a %s,
                
                Te informamos que un estudiante ha rechazado tu invitación para unirse a tu modalidad de grado grupal.
                
                **Detalles del rechazo:**
                
                - **Estudiante:** %s
                - **Modalidad:** %s
                - **Programa académico:** %s
                - **Fecha de rechazo:** %s
                
                **¿Qué significa esto?**
                
                %s decidió no formar parte de tu grupo para esta modalidad. Esto puede deberse a diversas razones:
                
                - Ya tiene compromisos con otros grupos o proyectos
                - Prefiere realizar la modalidad de forma individual
                - No puede cumplir con los requisitos o tiempos del proyecto
                - Tiene otros planes académicos
                
                **¿Qué puedes hacer ahora?**
                
                1. **Invitar a otro estudiante:** Puedes enviar una nueva invitación a otro compañero que esté disponible
                2. **Continuar con el grupo actual:** Si ya tienes otros miembros, pueden continuar con la modalidad
                3. **Realizar la modalidad de forma individual:** Si prefieres, puedes continuar solo
                
                Recuerda que tienes hasta **%d** miembros máximo (incluyéndote) para formar el grupo. 
                Actualmente tienes %d miembro(s) activo(s) en tu modalidad.
                
                **Próximos pasos:**
                
                - Revisa la lista de estudiantes elegibles para invitar
                - Coordina con los miembros actuales del grupo (si los hay)
                - Asegúrate de que todos estén alineados con los objetivos del proyecto
                
                No te desanimes, puedes invitar a otros compañeros que estén interesados en trabajar contigo.
                
                Cordialmente,
                
                Sistema Interno de Gestión Académica
                Universidad Surcolombiana - SIGMA
                """.formatted(
                leader.getName(),
                rejectedBy.getName() + " " + rejectedBy.getLastName(),
                modality.getProgramDegreeModality().getDegreeModality().getName(),
                modality.getAcademicProgram().getName(),
                LocalDateTime.now().toString(),
                rejectedBy.getName(),
                3, // MAX_GROUP_SIZE
                studentModalityMemberRepository.countByStudentModalityIdAndStatus(
                        modality.getId(),
                        com.SIGMA.USCO.Modalities.Entity.enums.MemberStatus.ACTIVE
                )
        );

        Notification notification = Notification.builder()
                .type(NotificationType.MODALITY_INVITATION_REJECTED)
                .recipientType(NotificationRecipientType.STUDENT)
                .recipient(leader)
                .triggeredBy(rejectedBy)
                .studentModality(modality)
                .subject(subject)
                .message(message)
                .createdAt(LocalDateTime.now())
                .build();

        notificationRepository.save(notification);
        dispatcher.dispatch(notification);

        log.info("Notificación de rechazo de invitación enviada al líder {} por el estudiante {}",
                leader.getId(), rejectedBy.getId());
    }


    @EventListener
    public void handleModalityFinalApprovedByCommittee(ModalityFinalApprovedByCommitteeEvent event) {
        StudentModality modality = studentModalityRepository.findById(event.getStudentModalityId())
                .orElseThrow(() -> new RuntimeException("Modalidad no encontrada"));

        User student = userRepository.findById(event.getStudentId())
                .orElseThrow(() -> new RuntimeException("Estudiante no encontrado"));

        User committeeMember = userRepository.findById(event.getCommitteeMemberId())
                .orElseThrow(() -> new RuntimeException("Miembro del comité no encontrado"));

        String subject = "¡FELICITACIONES! - Modalidad de Grado APROBADA";

        String message = """
                Estimado/a %s,
                
                Recibe un cordial y afectuoso saludo.
                
                ¡FELICITACIONES! Es un placer informarte que tu modalidad de grado:
                
                📚 "%s"
                
                Ha sido APROBADA DEFINITIVAMENTE por el Comité de Currículo del Programa Académico.
                
                📋 INFORMACIÓN DEL PROCESO:
                
                • Programa académico: %s
                • Estado final: APROBADO 
                • Aprobado por: %s %s
                • Fecha de aprobación: %s
                
                %s
                
               
                📌 IMPORTANTE:
                
                Este logro representa la culminación exitosa de tu proceso de formación profesional. 
                Tu dedicación y esfuerzo han sido reconocidos por las instancias académicas competentes.
                
                Te recomendamos estar atento a tu correo electrónico institucional para recibir 
                información adicional sobre los procedimientos administrativos finales.
                
                Una vez más, ¡MUCHAS FELICITACIONES por este importante logro académico!
                
                Cordialmente,
                
                Comité de Currículo del Programa
                Sistema de Gestión Académica - SIGMA
                Universidad Surcolombiana
                """.formatted(
                student.getName(),
                modality.getProgramDegreeModality().getDegreeModality().getName(),
                modality.getAcademicProgram().getName(),
                committeeMember.getName(),
                committeeMember.getLastName(),
                LocalDateTime.now().toString(),
                event.getObservations() != null && !event.getObservations().isBlank()
                        ? "📝 OBSERVACIONES DEL COMITÉ:\n" + event.getObservations() + "\n"
                        : ""
        );

        Notification notification = Notification.builder()
                .type(NotificationType.MODALITY_FINAL_APPROVED_BY_COMMITTEE)
                .recipientType(NotificationRecipientType.STUDENT)
                .recipient(student)
                .triggeredBy(committeeMember)
                .studentModality(modality)
                .subject(subject)
                .message(message)
                .createdAt(LocalDateTime.now())
                .build();

        notificationRepository.save(notification);
        dispatcher.dispatch(notification);

        log.info("Notificación de aprobación final de modalidad enviada al estudiante {} por el comité",
                student.getId());
    }


    @EventListener
    public void handleModalityRejectedByCommittee(ModalityRejectedByCommitteeEvent event) {
        StudentModality modality = studentModalityRepository.findById(event.getStudentModalityId())
                .orElseThrow(() -> new RuntimeException("Modalidad no encontrada"));

        User student = userRepository.findById(event.getStudentId())
                .orElseThrow(() -> new RuntimeException("Estudiante no encontrado"));

        User committeeMember = userRepository.findById(event.getCommitteeMemberId())
                .orElseThrow(() -> new RuntimeException("Miembro del comité no encontrado"));

        String subject = "IMPORTANTE: Modalidad de Grado NO APROBADA - Decisión del Comité";

        String message = """
                Estimado/a %s,
                
                Recibe un cordial saludo.
                
                Te informamos que después de la evaluación realizada por el Comité de Currículo del Programa, 
                tu modalidad de grado:
                
                "%s"
                
                NO ha sido aprobada.
                
                📋 INFORMACIÓN DEL PROCESO:
                
                • Programa académico: %s
                • Estado: NO APROBADO
                • Fecha: %s
                
                📝 MOTIVO DE LA DECISIÓN:
                
                %s
                
                🔄 OPCIONES DISPONIBLES:
                
                Aunque esta modalidad no fue aprobada, tienes las siguientes alternativas para continuar 
                con tu proceso de grado:
                
                1. **Iniciar una nueva modalidad de grado:** Puedes seleccionar otra modalidad diferente 
                   que se ajuste mejor a tu perfil académico y profesional
                
                2. **Recibir asesoría académica:** Solicita una reunión con la jefatura de tu programa 
                   para recibir orientación sobre las mejores opciones para ti
                
                3. **Revisar requisitos:** Asegúrate de cumplir con todos los requisitos académicos y 
                   administrativos para la nueva modalidad que elijas
                
                📞 PRÓXIMOS PASOS:
                
                • Comunícate con la Jefatura de Programa para recibir asesoría personalizada
                • Solicita retroalimentación detallada sobre los aspectos a mejorar
                • Revisa las diferentes modalidades de grado disponibles en tu programa
                • Evalúa con tu director académico cuál opción se ajusta mejor a tus capacidades
                
                📌 IMPORTANTE:
                
                Este resultado NO afecta tu expediente académico de manera permanente. Es una oportunidad 
                para replantear tu estrategia y elegir una modalidad más adecuada a tus fortalezas.
                
                Te invitamos a no desanimarte y a buscar el apoyo necesario para continuar exitosamente 
                con tu proceso de grado. El equipo académico está disponible para orientarte.
                
                Para cualquier duda o aclaración, por favor comunícate con:
                
                • Jefatura de Programa: %s
                • Comité de Currículo del Programa
                • Secretaría Académica de tu facultad
                
                Recuerda que el objetivo del comité es garantizar la calidad académica y el éxito de 
                nuestros estudiantes en su proceso de graduación.
                
                Cordialmente,
                
                Comité de Currículo del Programa
                Sistema de Gestión Académica - SIGMA
                Universidad Surcolombiana
                """.formatted(
                student.getName(),
                modality.getProgramDegreeModality().getDegreeModality().getName(),
                modality.getAcademicProgram().getName(),
                committeeMember.getName(),
                committeeMember.getLastName(),
                LocalDateTime.now().toString(),
                event.getReason(),
                modality.getAcademicProgram().getName()
        );

        Notification notification = Notification.builder()
                .type(NotificationType.MODALITY_REJECTED_BY_COMMITTEE)
                .recipientType(NotificationRecipientType.STUDENT)
                .recipient(student)
                .triggeredBy(committeeMember)
                .studentModality(modality)
                .subject(subject)
                .message(message)
                .createdAt(LocalDateTime.now())
                .build();

        notificationRepository.save(notification);
        dispatcher.dispatch(notification);
    }

    @EventListener
    public void onSeminarStarted(SeminarStartedEvent event) {
        String subject = "Inicio de Seminario: " + event.getSeminarName();

        String body = String.format("""
                Estimado/a %s,
                
                Le informamos que el seminario "%s" ha iniciado oficialmente.
                
                Detalles del seminario:
                - Nombre: %s
                - Programa: %s
                - Fecha de inicio: %s
                - Intensidad horaria: %d horas
                
                Es importante que esté atento/a a las indicaciones y horarios del seminario.
                Le recordamos que la asistencia es obligatoria (mínimo 80%% de la intensidad horaria).
                
                Cualquier duda o consulta, puede comunicarse con la jefatura del programa.
                
                Cordialmente,
                Sistema de Gestión de Modalidades de Grado - SIGMA
                %s
                Universidad Surcolombiana
                """,
                event.getRecipientName(),
                event.getSeminarName(),
                event.getSeminarName(),
                event.getProgramName(),
                event.getStartDate(),
                event.getTotalHours(),
                event.getProgramName()
        );

        User recipient = userRepository.findByEmail(event.getRecipientEmail()).orElse(null);

        Notification notification = Notification.builder()
                .recipient(recipient)
                .subject(subject)
                .message(body)
                .type(NotificationType.SEMINAR_STARTED)
                .recipientType(NotificationRecipientType.STUDENT)
                .createdAt(LocalDateTime.now())
                .build();

        notificationRepository.save(notification);
        dispatcher.dispatch(notification);
    }

    @EventListener
    public void onSeminarCancelled(SeminarCancelledEvent event) {
        String subject = "Cancelación de Seminario: " + event.getSeminarName();

        String body = String.format("""
                Estimado/a %s,
                
                Le informamos que el seminario "%s" ha sido CANCELADO.
                
                Detalles del seminario:
                - Nombre: %s
                - Programa: %s
                - Fecha de cancelación: %s
                %s
                
                La inscripción al seminario ha sido suspendida automáticamente.
                Podrá inscribirse a otro seminario disponible cuando lo desee.
                
                Lamentamos los inconvenientes que esto pueda causar.
                
                Cordialmente,
                Sistema de Gestión de Modalidades de Grado - SIGMA
                %s
                Universidad Surcolombiana
                """,
                event.getRecipientName(),
                event.getSeminarName(),
                event.getSeminarName(),
                event.getProgramName(),
                event.getCancelledDate(),
                event.getReason() != null ? "\nMotivo: " + event.getReason() : "",
                event.getProgramName()
        );

        User recipient = userRepository.findByEmail(event.getRecipientEmail()).orElse(null);

        Notification notification = Notification.builder()
                .recipient(recipient)
                .subject(subject)
                .message(body)
                .type(NotificationType.SEMINAR_CANCELLED)
                .recipientType(NotificationRecipientType.STUDENT)
                .createdAt(LocalDateTime.now())
                .build();

        notificationRepository.save(notification);
        dispatcher.dispatch(notification);
    }

    @EventListener
    public void handleModalityApprovedByExaminers(ModalityApprovedByExaminers event) {
        StudentModality modality = studentModalityRepository.findById(event.getStudentModalityId())
                .orElseThrow(() -> new RuntimeException("Modalidad no encontrada"));

        User examiner = userRepository.findById(event.getExaminerUserId())
                .orElseThrow(() -> new RuntimeException("Juez no encontrado"));

        // Notificar a todos los miembros activos de la modalidad
        var members = studentModalityMemberRepository.findByStudentModalityIdAndStatus(
                modality.getId(),
                com.SIGMA.USCO.Modalities.Entity.enums.MemberStatus.ACTIVE
        );

        String subject = "¡Modalidad de Grado Aprobada por el jurado!";
        String message = """
                Estimado/a %s,
                \n
                Nos complace informarte que tu modalidad de grado:\n\n
                \"%s\"\n\n
                ha sido **APROBADA** por los jurados asignados.\n\n
                Programa académico: %s\n
                Estado actual: PROPUESTA APROBADA\n
                Fecha de aprobación: %s\n
                Juez aprobador: %s %s (%s)\n\n
                A partir de este momento, puedes continuar con el proceso de grado según los lineamientos establecidos.\n\n
                Cordialmente,\n
                Sistema de Gestión Académica - SIGMA\n
                Universidad Surcolombiana
                """;

        for (var member : members) {
            User student = member.getStudent();
            String personalizedMessage = String.format(
                    message,
                    student.getName(),
                    modality.getProgramDegreeModality().getDegreeModality().getName(),
                    modality.getAcademicProgram().getName(),
                    LocalDateTime.now().toString(),
                    examiner.getName(),
                    examiner.getLastName(),
                    examiner.getEmail()
            );

            Notification notification = Notification.builder()
                    .type(NotificationType.MODALITY_APPROVED_BY_EXAMINERS)
                    .recipientType(NotificationRecipientType.STUDENT)
                    .recipient(student)
                    .triggeredBy(examiner)
                    .studentModality(modality)
                    .subject(subject)
                    .message(personalizedMessage)
                    .createdAt(LocalDateTime.now())
                    .build();
            notificationRepository.save(notification);
            dispatcher.dispatch(notification);
        }
    }

}

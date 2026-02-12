package com.SIGMA.USCO.notifications.listeners;

import com.SIGMA.USCO.Modalities.Entity.AcademicCertificate;
import com.SIGMA.USCO.Modalities.Entity.StudentModality;
import com.SIGMA.USCO.Modalities.Entity.enums.CertificateStatus;
import com.SIGMA.USCO.Modalities.Entity.enums.ModalityProcessStatus;
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
    private final NotificationRepository notificationRepository;
    private final NotificationDispatcherService dispatcher;
    private final UserRepository userRepository;
    private final StudentDocumentRepository studentDocumentRepository;
    private final AcademicCertificatePdfService certificatePdfService;


    @EventListener
    public void ModalityStarted(StudentModalityStarted event){

        StudentModality modality = studentModalityRepository.findById(event.getStudentModalityId()).orElseThrow();

        User student = modality.getStudent();

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

        User student = document.getStudentModality().getStudent();

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

        User student = sm.getStudent();

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

        User student = modality.getStudent();
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

        User student = modality.getStudent();

        String subject =
                "Modalidad de grado aprobada por el Consejo Académico";

        String message = """
                Estimado/a %s,
                
                Recibe un cordial saludo.
                
                Nos complace informarte que tu modalidad de grado:
                
                “%s”
                
                ha sido aprobada por el comité de curriculum del programa, tras la revisión realizada por las instancias académicas competentes.
                
                Estado actual del proceso:
                Propuesta aprobada
                
                Director de Proyecto:
                %s
                
                A partir de esta aprobación, puedes dar inicio formal al desarrollo de tu proyecto de grado bajo la dirección académica asignada, atendiendo los lineamientos y cronogramas establecidos por el programa.
                
                Fecha de aprobación:
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

        User student = modality.getStudent();

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

        User student = modality.getStudent();

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

        User student = modality.getStudent();

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

        User student = modality.getStudent();

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

        User student = modality.getStudent();

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

        User student = modality.getStudent();

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

        User student = modality.getStudent();
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

}
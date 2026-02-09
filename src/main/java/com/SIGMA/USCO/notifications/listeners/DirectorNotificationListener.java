package com.SIGMA.USCO.notifications.listeners;

import com.SIGMA.USCO.Modalities.Entity.StudentModality;
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

@RequiredArgsConstructor
@Component
public class DirectorNotificationListener {

    private final StudentModalityRepository studentModalityRepository;
    private final NotificationRepository notificationRepository;
    private final NotificationDispatcherService dispatcher;
    private final UserRepository userRepository;
    private final StudentDocumentRepository studentDocumentRepository;

    @EventListener
    public void onCancellationApproved(CancellationApprovedEvent event) {

        StudentModality sm = studentModalityRepository.findById(event.getStudentModalityId()).orElseThrow();

        if (sm.getProjectDirector() == null) {
            return;
        }

        String subject = "Cancelación de modalidad APROBADA";
        String message = """
                Estimado/a %s,
                
                Recibe un cordial saludo.
                
                Te informamos que la cancelación de la modalidad de grado:
                
                “%s”
                
                correspondiente al estudiante %s, ha sido aprobada por el Comité de Currículo del programa académico.
                
                Para mayor información o seguimiento, por favor mantente en contacto con la jefatura de programa.
                
                Cordialmente,
                Sistema de Gestión Académica
                """.formatted(
                sm.getProjectDirector().getName(),
                sm.getProgramDegreeModality().getDegreeModality().getName(),
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

        String subject = "Cancelación de modalidad rechazada";
        String message = """
                Estimado/a %s,
                
                Recibe un cordial saludo.
                
                Te informamos que la cancelación de la modalidad de grado:
                
                “%s”
                
                correspondiente al estudiante %s, ha sido rechazada por el Comité de Currículo del programa académico.
                
                Para mayor información o seguimiento, te sugerimos contactar a la jefatura de programa.
                
                Cordialmente,
                Sistema de Gestión Académica
                """.formatted(
                sm.getProjectDirector().getName(),
                sm.getProgramDegreeModality().getDegreeModality().getName(),
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

        String subject = "Solicitud de cancelación de modalidad recibida";
        String message = """
                Estimado/a %s,
                
                Recibe un cordial saludo.
                
                Te informamos que el estudiante %s ha solicitado la cancelación de la modalidad de grado:
                
                “%s”
                
                Dicha solicitud debe ser evaluada por ti y posteriormente será evaluada por el Comité de Currículo del programa académico correspondiente.
                
                
                Cordialmente,
                Sistema de Gestión Académica
                """.formatted(
                sm.getProjectDirector().getName(),
                sm.getStudent().getName() + " " + sm.getStudent().getLastName(),
                sm.getProgramDegreeModality().getDegreeModality().getName()
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
                "Notificación de sustentación programada para estudiante asignado";

        String directorMessage = """
                Estimado/a %s,
                
                Recibe un cordial saludo.
                
                Te informamos que se ha programado la sustentación de una modalidad de grado bajo tu dirección académica.
                
                Estudiante:
                %s (%s)
                
                Modalidad de grado:
                “%s”
                
                Fecha y hora de sustentación:
                %s
                
                Lugar:
                %s
                
                Por favor, asegúrate de cumplir con los lineamientos académicos establecidos y de brindar el acompañamiento necesario durante este proceso.
                
                Cordialmente,
                Sistema de Gestión Académica
                """.formatted(
                director.getName(),
                modality.getStudent().getName(),
                modality.getStudent().getEmail(),
                modality.getProgramDegreeModality().getDegreeModality().getName(),
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

        String directorSubject = "Asignación como Director de Proyecto a modalidad de grado";

        String directorMessage = """
                Estimado/a %s,
                
                Recibe un cordial saludo.
                
                Te informamos que has sido asignado oficialmente como Director de Proyecto para la siguiente modalidad de grado:
                
                Estudiante:
                %s (%s)
                
                Modalidad de grado:
                “%s”
                
                Fecha de asignación:
                %s
                
                Te invitamos a ingresar a la plataforma institucional para realizar el seguimiento y acompañamiento correspondientes durante el desarrollo del proyecto de grado.
                
                Cordialmente,
                Sistema de Gestión Académica
                """.formatted(
                director.getName(),
                student.getName() + " " + student.getLastName(),
                student.getEmail(),
                modality.getProgramDegreeModality().getAcademicProgram().getName(),
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
                Estimado/a %s,
                
                Recibe un cordial saludo.
                
                La sustentación final de la modalidad de grado bajo tu dirección ha concluido con el siguiente resultado:
                
                Estudiante:
                %s (%s)
                
                Modalidad de grado:
                “%s”
                
                Resultado final:
                %s
                
                Distinción académica:
                %s
                
                Observaciones:
                %s
                
                Te invitamos a comunicarte con la jefatura del programa para coordinar los próximos pasos y el cierre administrativo correspondiente.
                
                Cordialmente,
                Sistema de Gestión Académica
                """.formatted(
                director.getName(),
                modality.getStudent().getName(),
                modality.getStudent().getEmail(),
                modality.getProgramDegreeModality().getDegreeModality().getName(),
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

    @EventListener
    public void onStudentDocumentUpdated(StudentDocumentUpdatedEvent event) {

        StudentModality modality = studentModalityRepository.findById(event.getStudentModalityId())
                .orElseThrow();

        if (modality.getProjectDirector() == null) {
            return;
        }

        StudentDocument document = studentDocumentRepository.findById(event.getStudentDocumentId())
                .orElseThrow();

        User student = modality.getStudent();
        User director = modality.getProjectDirector();

        String subject = "Documento actualizado – Estudiante asignado";

        String message = """
            Estimado/a %s,

            Recibe un cordial saludo.

            El estudiante %s ha actualizado un documento
            asociado a la modalidad de grado que diriges.

            Modalidad:
            "%s"

            Documento:
            "%s"

            Estado:
            %s

            Te invitamos a revisar el documento actualizado en la plataforma para continuar con el seguimiento académico correspondiente.

            Cordialmente,  
            Sistema de Gestión Académica
            """.formatted(
                director.getName(),
                student.getName() + " " + student.getLastName(),
                modality.getProgramDegreeModality().getDegreeModality().getName(),
                document.getDocumentConfig().getDocumentName(),
                document.getStatus()
        );

        Notification notification = Notification.builder()
                .type(NotificationType.DOCUMENT_UPLOADED)
                .recipientType(NotificationRecipientType.PROJECT_DIRECTOR)
                .recipient(director)
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

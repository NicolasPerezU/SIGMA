package com.SIGMA.USCO.notifications.listeners;

import com.SIGMA.USCO.Modalities.Entity.StudentModality;
import com.SIGMA.USCO.Modalities.Entity.enums.ModalityProcessStatus;
import com.SIGMA.USCO.Modalities.Repository.StudentModalityRepository;
import com.SIGMA.USCO.Users.Entity.User;
import com.SIGMA.USCO.Users.repository.UserRepository;
import com.SIGMA.USCO.notifications.entity.Notification;
import com.SIGMA.USCO.notifications.entity.enums.NotificationRecipientType;
import com.SIGMA.USCO.notifications.entity.enums.NotificationType;
import com.SIGMA.USCO.notifications.service.NotificationDispatcherService;
import com.SIGMA.USCO.notifications.repository.NotificationRepository;
import com.SIGMA.USCO.Modalities.Entity.DefenseExaminer;
import com.SIGMA.USCO.Modalities.Repository.DefenseExaminerRepository;
import com.SIGMA.USCO.notifications.event.DefenseReadyByDirectorEvent;
import com.SIGMA.USCO.notifications.event.DefenseScheduledEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class ExaminerNotificationListener {

    private final DefenseExaminerRepository defenseExaminerRepository;
    private final NotificationRepository notificationRepository;
    private final NotificationDispatcherService dispatcher;
    private final UserRepository userRepository;
    private final StudentModalityRepository studentModalityRepository;

    public void notifyExaminersAssignment(Long studentModalityId) {
        StudentModality modality = studentModalityRepository.findById(studentModalityId)
                .orElseThrow(() -> new RuntimeException("Modalidad no encontrada"));

        List<DefenseExaminer> examiners = defenseExaminerRepository.findByStudentModalityId(studentModalityId);

        // Obtener todos los estudiantes asociados a la modalidad desde los miembros
        List<String> studentsList = modality.getMembers() != null ?
            modality.getMembers().stream()
                .map(member -> {
                    User student = member.getStudent();
                    return student.getName() + " " + student.getLastName();
                })
                .toList() : List.of();
        String studentsString = studentsList.isEmpty() ? "-" : String.join(", ", studentsList);

        for (DefenseExaminer examinerAssignment : examiners) {
            User examiner = examinerAssignment.getExaminer();
            String subject = "Asignación como Juez en Modalidad de Grado";
            String message = String.format("""
                Estimado/a %s %s,

                Le informamos que ha sido asignado como jurado en la modalidad de grado:

                Modalidad: %s
                Programa académico: %s
                Estudiante(s): %s
                Director de proyecto: %s %s
                Fecha de asignación: %s

                Por favor, recuerde revisar y aprobar los documentos correspondientes a la modalidad en el sistema SIGMA.

                Cordialmente,
                Sistema de Gestión Académica
                """,
                examiner.getName(),
                examiner.getLastName(),
                examinerAssignment.getExaminerType().name().replace('_', ' '),
                modality.getProgramDegreeModality().getDegreeModality().getName(),
                modality.getAcademicProgram().getName(),
                studentsString,
                modality.getProjectDirector() != null ? modality.getProjectDirector().getName() : "-",
                modality.getProjectDirector() != null ? modality.getProjectDirector().getLastName() : "-"
            );

            Notification notification = Notification.builder()
                    .type(NotificationType.EXAMINER_ASSIGNED)
                    .recipientType(NotificationRecipientType.EXAMINER)
                    .recipient(examiner)
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
    public void handleDefenseReadyByDirectorEvent(DefenseReadyByDirectorEvent event) {
        StudentModality modality = studentModalityRepository.findById(event.getStudentModalityId())
                .orElseThrow(() -> new RuntimeException("Modalidad no encontrada"));
        User examiner = userRepository.findById(event.getExaminerId())
                .orElseThrow(() -> new RuntimeException("Juez no encontrado"));

        // Construir el mensaje personalizado aquí
        String subject = "Modalidad lista para defensa";
        String message = String.format(
            """
            Estimado/a %s %s,\n\nLa modalidad de grado \"%s\" del estudiante %s %s ha sido marcada como lista para defensa por el director de proyecto.\n\nPor favor, ingrese al sistema SIGMA para revisar los documentos y continuar con el proceso de sustentación.\n\nCordialmente,\nSistema de Gestión Académica\n""",
            examiner.getName(),
            examiner.getLastName(),
            modality.getProgramDegreeModality().getDegreeModality().getName(),
            modality.getLeader().getName(),
            modality.getLeader().getLastName()
        );

        Notification notification = Notification.builder()
                .type(NotificationType.READY_FOR_DEFENSE_REQUESTED)
                .recipientType(NotificationRecipientType.EXAMINER)
                .recipient(examiner)
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
    public void handleExaminerFinalReviewCompletedEvent(com.SIGMA.USCO.notifications.event.ExaminerFinalReviewCompletedEvent event) {
        StudentModality modality = studentModalityRepository.findById(event.getStudentModalityId())
                .orElseThrow(() -> new RuntimeException("Modalidad no encontrada"));
        User director = userRepository.findById(event.getProjectDirectorId())
                .orElseThrow(() -> new RuntimeException("Director de proyecto no encontrado"));

        String subject = "Documentos aprobados por el jurado - Puede programar la sustentación";
        String message = String.format(
                """
                Estimado/a %s %s,\n\nEl jurado ha aprobado todos los documentos requeridos para la modalidad de grado \"%s\" del estudiante líder %s %s.\n\nAhora puede programar la fecha y lugar de la sustentación en el sistema SIGMA.\n\nCordialmente,\nSistema de Gestión Académica\n""",
                director.getName(),
                director.getLastName(),
                modality.getProgramDegreeModality().getDegreeModality().getName(),
                modality.getLeader().getName(),
                modality.getLeader().getLastName()
        );

        Notification notification = Notification.builder()
                .type(NotificationType.FINAL_APPROVED)
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
    public void handleDefenseScheduled(DefenseScheduledEvent event) {
        StudentModality modality = studentModalityRepository.findById(event.getStudentModalityId())
                .orElseThrow(() -> new RuntimeException("Modalidad no encontrada"));

        List<DefenseExaminer> examiners = defenseExaminerRepository.findByStudentModalityId(event.getStudentModalityId());
        for (DefenseExaminer examinerAssignment : examiners) {
            User examiner = examinerAssignment.getExaminer();
            String subject = "Sustentación programada – Modalidad de Grado";
            String message = String.format(
                """
                Estimado/a %s %s,

                Se ha programado la sustentación de la modalidad de grado:

                Modalidad: "%s"
                Fecha y hora: %s
                Lugar: %s
                Director asignado: %s

                Estudiantes asociados: %s

                Por favor ingrese al sistema SIGMA para revisar los documentos y prepararse para la sustentación.

                Sistema SIGMA
                """,
                examiner.getName(),
                examiner.getLastName(),
                modality.getProgramDegreeModality().getDegreeModality().getName(),
                event.getDefenseDate(),
                event.getDefenseLocation(),
                modality.getProjectDirector() != null ? modality.getProjectDirector().getName() + " " + modality.getProjectDirector().getLastName() : "No asignado",
                modality.getMembers() != null && !modality.getMembers().isEmpty() ? modality.getMembers().stream().map(member -> member.getStudent().getName() + " " + member.getStudent().getLastName()).reduce((a, b) -> a + ", " + b).orElse("") : modality.getLeader().getName() + " " + modality.getLeader().getLastName()
            );

            Notification notification = Notification.builder()
                    .type(NotificationType.DEFENSE_SCHEDULED)
                    .recipientType(NotificationRecipientType.EXAMINER)
                    .recipient(examiner)
                    .triggeredBy(null)
                    .studentModality(modality)
                    .subject(subject)
                    .message(message)
                    .createdAt(LocalDateTime.now())
                    .build();
            notificationRepository.save(notification);
            dispatcher.dispatch(notification);
        }

        // Notificar a todos los estudiantes asociados
        List<User> students;
        if (modality.getMembers() != null && !modality.getMembers().isEmpty()) {
            students = modality.getMembers().stream().map(member -> member.getStudent()).toList();
        } else {
            students = List.of(modality.getLeader());
        }
        for (User student : students) {
            String subject = "Sustentación programada – Modalidad de Grado";
            String message = String.format(
                """
                Estimado/a %s,

                Te informamos que la sustentación de tu modalidad de grado ha sido programada:

                Modalidad: "%s"
                Fecha y hora: %s
                Lugar: %s
                Director asignado: %s

                Por favor preséntate con antelación y cumple con los lineamientos académicos establecidos.

                Sistema SIGMA
                """,
                student.getName(),
                modality.getProgramDegreeModality().getDegreeModality().getName(),
                event.getDefenseDate(),
                event.getDefenseLocation(),
                modality.getProjectDirector() != null ? modality.getProjectDirector().getName() + " " + modality.getProjectDirector().getLastName() : "No asignado"
            );

            Notification notification = Notification.builder()
                    .type(NotificationType.DEFENSE_SCHEDULED)
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
}

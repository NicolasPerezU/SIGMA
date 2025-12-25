package com.SIGMA.USCO.notifications.listeners;

import com.SIGMA.USCO.Modalities.Entity.StudentModality;
import com.SIGMA.USCO.Modalities.Entity.enums.AcademicDistinction;
import com.SIGMA.USCO.Modalities.Entity.enums.ModalityProcessStatus;
import com.SIGMA.USCO.Modalities.Repository.StudentModalityRepository;
import com.SIGMA.USCO.Users.Entity.User;
import com.SIGMA.USCO.config.EmailService;
import com.SIGMA.USCO.notifications.event.FinalDefenseResultEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FinalDefenseResultListener {

    private final StudentModalityRepository studentModalityRepository;
    private final EmailService emailService;

    @EventListener
    public void handleFinalDefenseResult(FinalDefenseResultEvent event) {

        StudentModality modality = studentModalityRepository.findById(event.getStudentModalityId())
                        .orElseThrow();

        User student = modality.getStudent();
        User director = modality.getProjectDirector();

        boolean approved = event.getFinalStatus() == ModalityProcessStatus.GRADED_APPROVED;

        String studentSubject = approved
                ? "Resultado final de sustentación – Modalidad aprobada"
                : "Resultado final de sustentación – Modalidad no aprobada";

        String studentMessage = approved
                ? buildApprovedStudentMessage(student, modality, event)
                : buildRejectedStudentMessage(student, modality, event);

        emailService.sendEmail(student.getEmail(), studentSubject, studentMessage);

        if (director != null) {

            String directorSubject =
                    "Resultado final de sustentación – Estudiante dirigido";

            String directorMessage = """
                    Hola %s,

                    Se ha registrado el resultado final de la sustentación
                    del estudiante bajo tu dirección:

                    Estudiante:
                    %s (%s)

                    Modalidad:
                    "%s"

                    Resultado:
                    %s

                    Mención académica:
                    %s

                    Observaciones:
                    %s

                    Sistema SIGMA
                    """.formatted(
                    director.getName(),
                    student.getName() + " " + student.getLastName(),
                    student.getEmail(),
                    modality.getModality().getName(),
                    approved ? "APROBADA" : "NO APROBADA",
                    event.getAcademicDistinction() != null
                            ? event.getAcademicDistinction().name()
                            : "No aplica",
                    event.getObservations()
            );

            emailService.sendEmail(director.getEmail(), directorSubject, directorMessage);
        }
    }

    private String buildApprovedStudentMessage(
            User student,
            StudentModality modality,
            FinalDefenseResultEvent event
    ) {

        AcademicDistinction distinction =
                event.getAcademicDistinction() != null
                        ? event.getAcademicDistinction()
                        : AcademicDistinction.NO_DISTINCTION;

        return """
                Hola %s,

                Nos complace informarte que tu modalidad de grado
                ha sido APROBADA exitosamente.

                Modalidad:
                "%s"

                Resultado final:
                APROBADA

                Mención académica:
                %s

                Observaciones:
                %s

                Felicitaciones por este logro académico.
                Te deseamos muchos éxitos en tu vida profesional.

                Sistema SIGMA
                """.formatted(
                student.getName(),
                modality.getModality().getName(),
                distinction.name(),
                event.getObservations()
        );
    }

    private String buildRejectedStudentMessage(
            User student,
            StudentModality modality,
            FinalDefenseResultEvent event
    ) {

        return """
                Hola %s,

                Te informamos que el resultado final de tu sustentación
                fue NO APROBADO.

                Modalidad:
                "%s"

                Resultado final:
                NO APROBADA

                Observaciones del Consejo:
                %s

                Para mayor orientación, comunícate con la Secretaría
                del programa académico.

                Sistema SIGMA
                """.formatted(
                student.getName(),
                modality.getModality().getName(),
                event.getObservations()
        );
    }

}

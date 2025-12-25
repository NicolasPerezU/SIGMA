package com.SIGMA.USCO.notifications.listeners;

import com.SIGMA.USCO.Modalities.Entity.StudentModality;
import com.SIGMA.USCO.Modalities.Repository.StudentModalityRepository;
import com.SIGMA.USCO.Users.Entity.User;
import com.SIGMA.USCO.config.EmailService;
import com.SIGMA.USCO.notifications.event.ModalityApprovedByCouncilEvent;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.CodePointLength;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class ModalityCouncilListener {

    private final StudentModalityRepository studentModalityRepository;
    private final EmailService emailService;

    @EventListener
    public void handleModalityApprovedByCouncil(ModalityApprovedByCouncilEvent event) {

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

        emailService.sendEmail(student.getEmail(), subject, message);
    }
}

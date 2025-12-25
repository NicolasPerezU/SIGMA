package com.SIGMA.USCO.notifications.listeners;

import com.SIGMA.USCO.Modalities.Entity.StudentModality;
import com.SIGMA.USCO.Modalities.Repository.StudentModalityRepository;
import com.SIGMA.USCO.Users.Entity.User;
import com.SIGMA.USCO.config.EmailService;
import com.SIGMA.USCO.notifications.event.CancellationRejectedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CancellationRejectedListener {

    private final StudentModalityRepository studentModalityRepository;
    private final EmailService emailService;

    @EventListener
    public void handleCancellationRejected(CancellationRejectedEvent event) {

        StudentModality modality = studentModalityRepository.findById(event.getStudentModalityId())
                        .orElseThrow();

        User student = modality.getStudent();

        emailService.sendEmail(
                student.getEmail(),
                "Solicitud de cancelación RECHAZADA",
                """
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
                        modality.getModality().getName(),
                        event.getReason()
                )
        );
    }


}

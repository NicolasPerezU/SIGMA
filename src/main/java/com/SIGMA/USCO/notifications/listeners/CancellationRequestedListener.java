package com.SIGMA.USCO.notifications.listeners;

import com.SIGMA.USCO.Modalities.Entity.StudentModality;
import com.SIGMA.USCO.Modalities.Repository.StudentModalityRepository;
import com.SIGMA.USCO.Users.Entity.User;
import com.SIGMA.USCO.config.EmailService;
import com.SIGMA.USCO.notifications.event.CancellationRequestedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CancellationRequestedListener {

    private final StudentModalityRepository studentModalityRepository;
    private final EmailService emailService;

    @EventListener
    public void handleCancellationRequested(CancellationRequestedEvent event) {

        StudentModality modality = studentModalityRepository.findById(event.getStudentModalityId())
                        .orElseThrow();

        User student = modality.getStudent();
        User director = modality.getProjectDirector();

        emailService.sendEmail(
                student.getEmail(),
                "Solicitud de cancelación registrada",
                """
                Hola %s,

                Tu solicitud de cancelación de la modalidad de grado:

                "%s"

                ha sido registrada correctamente y será evaluada
                por el concejo Académico.

                Sistema SIGMA
                """.formatted(
                        student.getName(),
                        modality.getModality().getName()
                )
        );

        if (director != null) {
            emailService.sendEmail(
                    director.getEmail(),
                    "Solicitud de cancelación – Estudiante a cargo",
                    """
                    Hola %s,

                    El estudiante %s (%s) ha solicitado la cancelación
                    de la modalidad de grado:

                    "%s"

                    Esta solicitud será evaluada por el concejo Académico.

                    Sistema SIGMA
                    """.formatted(
                            director.getName(),
                            student.getName() + " " + student.getLastName(),
                            student.getEmail(),
                            modality.getModality().getName()
                    )
            );
        }


    }
}

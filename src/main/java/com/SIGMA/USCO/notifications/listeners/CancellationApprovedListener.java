package com.SIGMA.USCO.notifications.listeners;

import com.SIGMA.USCO.Modalities.Entity.StudentModality;
import com.SIGMA.USCO.Modalities.Repository.StudentModalityRepository;
import com.SIGMA.USCO.Users.Entity.User;
import com.SIGMA.USCO.config.EmailService;
import com.SIGMA.USCO.notifications.event.CancellationApprovedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CancellationApprovedListener {

    private final StudentModalityRepository studentModalityRepository;
    private final EmailService emailService;

    @EventListener
    public void handleCancellationApproved(CancellationApprovedEvent event) {

        StudentModality modality = studentModalityRepository.findById(event.getStudentModalityId()).orElseThrow();

        User student = modality.getStudent();
        User director = modality.getProjectDirector();

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
                modality.getModality().getName()
        );

        emailService.sendEmail(student.getEmail(), subject, message);

        if (director != null) {
            emailService.sendEmail(
                    director.getEmail(),
                    subject,
                    """
                    Hola %s,

                    La cancelación de la modalidad:

                    "%s"

                    del estudiante %s ha sido APROBADA por el concejo.

                    Sistema SIGMA
                    """.formatted(
                            director.getName(),
                            modality.getModality().getName(),
                            student.getName() + " " + student.getLastName()
                    )
            );
        }
    }
}

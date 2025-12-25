package com.SIGMA.USCO.notifications.listeners;

import com.SIGMA.USCO.Modalities.Entity.StudentModality;
import com.SIGMA.USCO.Modalities.Repository.StudentModalityRepository;
import com.SIGMA.USCO.Users.Entity.User;
import com.SIGMA.USCO.config.EmailService;
import com.SIGMA.USCO.notifications.event.DefenseScheduledEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DefenseScheduledListener {

    private final StudentModalityRepository studentModalityRepository;
    private final EmailService emailService;

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

        emailService.sendEmail(student.getEmail(), studentSubject, studentMessage);

        if (director != null) {

            String directorSubject =
                    "Sustentación programada – Estudiante asignado";

            String directorMessage = """
                    Hola %s,

                    Se ha programado la sustentación de una modalidad
                    de grado bajo tu dirección:

                    Estudiante:
                    %s (%s)

                    Modalidad:
                    "%s"

                    Fecha y hora:
                    %s

                    Lugar:
                    %s

                    Sistema SIGMA
                    """.formatted(
                    director.getName(),
                    student.getName() + " " + student.getLastName(),
                    student.getEmail(),
                    modality.getModality().getName(),
                    event.getDefenseDate(),
                    event.getDefenseLocation()
            );

            emailService.sendEmail(director.getEmail(), directorSubject, directorMessage);
        }
    }
}

package com.SIGMA.USCO.notifications.listeners;

import com.SIGMA.USCO.Modalities.Entity.StudentModality;
import com.SIGMA.USCO.Modalities.Repository.StudentModalityRepository;
import com.SIGMA.USCO.Users.Entity.User;
import com.SIGMA.USCO.Users.repository.UserRepository;
import com.SIGMA.USCO.config.EmailService;
import com.SIGMA.USCO.notifications.event.DirectorAssignedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DirectorAssignedListener {

    private final StudentModalityRepository studentModalityRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;

    @EventListener
    public void handleProjectDirectorAssigned(DirectorAssignedEvent event) {

        StudentModality modality =
                studentModalityRepository.findById(event.getStudentModalityId())
                        .orElseThrow();


        User student = modality.getStudent();
        User director = userRepository.findById(event.getDirectorId())
                .orElseThrow();

        String studentSubject =
                "Director asignado a tu modalidad – SIGMA";

        String studentMessage = """
                Hola %s,

                Te informamos que se ha asignado un Director de Proyecto
                a tu modalidad de grado:

                Modalidad:
                "%s"

                Director asignado:
                %s (%s)

                A partir de este momento podrás continuar el proceso académico
                bajo su acompañamiento.

                Sistema SIGMA
                """.formatted(
                student.getName(),
                modality.getModality().getName(),
                director.getName() + " " + director.getLastName(),
                director.getEmail()
        );

        emailService.sendEmail(student.getEmail(), studentSubject, studentMessage);


        String directorSubject =
                "Nueva modalidad asignada como Director – SIGMA";

        String directorMessage = """
                Hola %s,

                Has sido asignado como Director de Proyecto
                para la siguiente modalidad de grado:

                Estudiante:
                %s (%s)

                Modalidad:
                "%s"

                Fecha de asignación:
                %s

                Por favor ingresa al sistema SIGMA para realizar
                el seguimiento correspondiente.

                Sistema SIGMA
                """.formatted(
                director.getName(),
                student.getName() + " " + student.getLastName(),
                student.getEmail(),
                modality.getModality().getName(),
                modality.getUpdatedAt()
        );

        emailService.sendEmail(director.getEmail(), directorSubject, directorMessage);
    }
}

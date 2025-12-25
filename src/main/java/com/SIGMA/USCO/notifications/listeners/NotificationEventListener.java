package com.SIGMA.USCO.notifications.listeners;

import com.SIGMA.USCO.Modalities.Entity.StudentModality;
import com.SIGMA.USCO.Modalities.Repository.StudentModalityRepository;
import com.SIGMA.USCO.Users.Entity.User;
import com.SIGMA.USCO.config.EmailService;
import com.SIGMA.USCO.notifications.event.StudentModalityStarted;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationEventListener {

    private final StudentModalityRepository studentModalityRepository;
    private final EmailService emailService;

    @EventListener
    public void handleStudentModalityStarted(StudentModalityStarted event) {

        StudentModality sm = studentModalityRepository
                        .findById(event.getStudentModalityId()).orElseThrow();

        User student = sm.getStudent();

        emailService.sendEmail(
                student.getEmail(),
                "Inicio de Modalidad de Grado",
                buildStudentMessage(sm)
        );


    }

    private String buildStudentMessage(StudentModality sm) {
        return """
                Hola %s,

                Has iniciado correctamente la modalidad de grado:
                %s

                Estado actual: %s

                Recuerda cargar todos los documentos obligatorios
                para continuar con el proceso.

                Sistema SIGMA
                """.formatted(
                sm.getStudent().getName(),
                sm.getModality().getName(),
                sm.getStatus().name()
        );
    }


}

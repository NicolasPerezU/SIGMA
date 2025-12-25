package com.SIGMA.USCO.notifications.listeners;

import com.SIGMA.USCO.Modalities.Entity.StudentModality;
import com.SIGMA.USCO.Modalities.Repository.StudentModalityRepository;
import com.SIGMA.USCO.Users.Entity.User;
import com.SIGMA.USCO.config.EmailService;
import com.SIGMA.USCO.notifications.event.ModalityApprovedBySecretary;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ModalitySecretaryListener {

    private final StudentModalityRepository studentModalityRepository;
    private final EmailService emailService;

    @EventListener
    public void handleModalityApprovedBySecretary(ModalityApprovedBySecretary event) {

        StudentModality modality = studentModalityRepository.findById(event.getStudentModalityId()).orElseThrow();

        User student = modality.getStudent();

        String subject =
                "Modalidad aprobada por Secretaría – SIGMA";

        String message = """
                Hola %s,

                Tu modalidad de grado "%s" ha sido APROBADA por la Secretaría del programa.

                Estado actual:
                LISTA PARA REVISIÓN DEL CONCEJO ACADÉMICO.

                Por favor permanece atento a futuras notificaciones.

                Sistema SIGMA
                """.formatted(
                student.getName(),
                modality.getModality().getName(),
                modality.getUpdatedAt()
        );

        emailService.sendEmail(student.getEmail(), subject, message);
    }

}

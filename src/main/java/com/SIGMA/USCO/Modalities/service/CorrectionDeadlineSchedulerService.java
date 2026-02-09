package com.SIGMA.USCO.Modalities.service;

import com.SIGMA.USCO.Modalities.Entity.ModalityProcessStatusHistory;
import com.SIGMA.USCO.Modalities.Entity.StudentModality;
import com.SIGMA.USCO.Modalities.Entity.enums.ModalityProcessStatus;
import com.SIGMA.USCO.Modalities.Repository.ModalityProcessStatusHistoryRepository;
import com.SIGMA.USCO.Modalities.Repository.StudentModalityRepository;
import com.SIGMA.USCO.notifications.event.CorrectionDeadlineExpiredEvent;
import com.SIGMA.USCO.notifications.event.CorrectionDeadlineReminderEvent;
import com.SIGMA.USCO.notifications.publisher.NotificationEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CorrectionDeadlineSchedulerService {

    private final StudentModalityRepository studentModalityRepository;
    private final ModalityProcessStatusHistoryRepository historyRepository;
    private final NotificationEventPublisher notificationEventPublisher;

    /**
     * Tarea programada que se ejecuta diariamente a las 8:00 AM
     * Verifica plazos de corrección y toma acciones:
     * - A los 20 días: envía recordatorio
     * - A los 30 días: cancela la modalidad automáticamente
     */
    @Scheduled(cron = "0 0 8 * * ?") // Ejecutar todos los días a las 8:00 AM
    @Transactional
    public void checkCorrectionDeadlines() {
        log.info("Iniciando verificación de plazos de corrección...");

        LocalDateTime now = LocalDateTime.now();

        // Buscar modalidades con correcciones solicitadas
        List<StudentModality> modalitiesWithCorrections = studentModalityRepository.findByStatusIn(
                List.of(
                        ModalityProcessStatus.CORRECTIONS_REQUESTED_PROGRAM_HEAD,
                        ModalityProcessStatus.CORRECTIONS_REQUESTED_PROGRAM_CURRICULUM_COMMITTEE
                )
        );

        log.info("Encontradas {} modalidades con correcciones solicitadas", modalitiesWithCorrections.size());

        for (StudentModality modality : modalitiesWithCorrections) {

            if (modality.getCorrectionRequestDate() == null || modality.getCorrectionDeadline() == null) {
                log.warn("Modalidad {} no tiene fechas de corrección configuradas. Saltando...", modality.getId());
                continue;
            }

            long daysSinceRequest = ChronoUnit.DAYS.between(modality.getCorrectionRequestDate(), now);
            long daysUntilDeadline = ChronoUnit.DAYS.between(now, modality.getCorrectionDeadline());

            log.debug("Modalidad {}: {} días desde solicitud, {} días hasta plazo límite",
                     modality.getId(), daysSinceRequest, daysUntilDeadline);

            // Caso 1: Han pasado 30 días o más - Cancelar automáticamente
            if (daysUntilDeadline <= 0) {
                cancelModalityByTimeout(modality);
            }
            // Caso 2: Han pasado 20 días - Enviar recordatorio (solo una vez)
            else if (daysSinceRequest >= 20 && (modality.getCorrectionReminderSent() == null || !modality.getCorrectionReminderSent())) {
                sendDeadlineReminder(modality, (int) daysUntilDeadline);
            }
        }

        log.info("Verificación de plazos de corrección completada");
    }

    /**
     * Envía un recordatorio al estudiante sobre el plazo de corrección
     */
    private void sendDeadlineReminder(StudentModality modality, int daysRemaining) {
        log.info("Enviando recordatorio a estudiante {} para modalidad {}",
                 modality.getStudent().getId(), modality.getId());

        // Publicar evento de recordatorio
        notificationEventPublisher.publish(
                new CorrectionDeadlineReminderEvent(
                        modality.getId(),
                        modality.getStudent().getId(),
                        modality.getCorrectionDeadline(),
                        daysRemaining
                )
        );

        // Marcar que el recordatorio fue enviado
        modality.setCorrectionReminderSent(true);
        studentModalityRepository.save(modality);

        log.info("Recordatorio enviado exitosamente para modalidad {}", modality.getId());
    }

    /**
     * Cancela automáticamente la modalidad por vencimiento del plazo
     */
    private void cancelModalityByTimeout(StudentModality modality) {
        log.info("Cancelando modalidad {} por vencimiento de plazo de corrección", modality.getId());

        // Cambiar estado a cancelado por timeout
        modality.setStatus(ModalityProcessStatus.CANCELLED_BY_CORRECTION_TIMEOUT);
        modality.setUpdatedAt(LocalDateTime.now());
        studentModalityRepository.save(modality);

        // Registrar en historial
        historyRepository.save(
                ModalityProcessStatusHistory.builder()
                        .studentModality(modality)
                        .status(ModalityProcessStatus.CANCELLED_BY_CORRECTION_TIMEOUT)
                        .changeDate(LocalDateTime.now())
                        .responsible(null) // Cancelación automática del sistema
                        .observations(
                                "Modalidad cancelada automáticamente por vencimiento del plazo de 30 días " +
                                "para entregar correcciones solicitadas. " +
                                "Fecha de solicitud: " + modality.getCorrectionRequestDate() +
                                ". Plazo límite: " + modality.getCorrectionDeadline()
                        )
                        .build()
        );

        // Publicar evento de vencimiento
        notificationEventPublisher.publish(
                new CorrectionDeadlineExpiredEvent(
                        modality.getId(),
                        modality.getStudent().getId(),
                        modality.getCorrectionRequestDate()
                )
        );

        log.info("Modalidad {} cancelada exitosamente por timeout", modality.getId());
    }
}


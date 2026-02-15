package com.SIGMA.USCO.notifications.event;

import com.SIGMA.USCO.notifications.entity.enums.NotificationType;
import lombok.Getter;

/**
 * Evento que se dispara cuando el comité aprueba definitivamente una modalidad
 * (sin necesidad de sustentación o evaluación de jueces)
 * Estado final: GRADED_APPROVED con NO_DISTINCTION
 */
@Getter
public class ModalityFinalApprovedByCommitteeEvent extends DomainEvent {

    private final Long studentModalityId;
    private final Long studentId;
    private final String observations;
    private final Long committeeMemberId;

    public ModalityFinalApprovedByCommitteeEvent(Long studentModalityId, Long studentId, String observations, Long committeeMemberId) {
        super(NotificationType.MODALITY_FINAL_APPROVED_BY_COMMITTEE, studentModalityId, studentId);
        this.studentModalityId = studentModalityId;
        this.studentId = studentId;
        this.observations = observations;
        this.committeeMemberId = committeeMemberId;
    }
}


package com.SIGMA.USCO.notifications.event;

import com.SIGMA.USCO.notifications.entity.enums.NotificationType;
import lombok.Getter;

/**
 * Evento que se dispara cuando el comité rechaza definitivamente una modalidad
 * Estado final: GRADED_FAILED
 */
@Getter
public class ModalityRejectedByCommitteeEvent extends DomainEvent {

    private final Long studentModalityId;
    private final Long studentId;
    private final String reason;
    private final Long committeeMemberId;

    public ModalityRejectedByCommitteeEvent(Long studentModalityId, Long studentId, String reason, Long committeeMemberId) {
        super(NotificationType.MODALITY_REJECTED_BY_COMMITTEE, studentModalityId, studentId);
        this.studentModalityId = studentModalityId;
        this.studentId = studentId;
        this.reason = reason;
        this.committeeMemberId = committeeMemberId;
    }
}


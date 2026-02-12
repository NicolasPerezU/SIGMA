package com.SIGMA.USCO.notifications.event;

import com.SIGMA.USCO.notifications.entity.enums.NotificationType;
import lombok.Getter;

@Getter
public class ModalityClosedByCommitteeEvent extends DomainEvent {

    private final Long studentModalityId;
    private final Long studentId;
    private final String reason;
    private final Long committeeMemberId;

    public ModalityClosedByCommitteeEvent(Long studentModalityId, Long studentId, String reason, Long committeeMemberId) {
        super(NotificationType.MODALITY_CLOSED_BY_COMMITTEE, studentModalityId, studentId);
        this.studentModalityId = studentModalityId;
        this.studentId = studentId;
        this.reason = reason;
        this.committeeMemberId = committeeMemberId;
    }
}


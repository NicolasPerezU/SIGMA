package com.SIGMA.USCO.notifications.event;

import com.SIGMA.USCO.notifications.entity.enums.NotificationType;
import lombok.Getter;

@Getter
public class ModalityApprovedByCommitteeEvent extends DomainEvent {

    private final Long studentModalityId;
    private final Long committeeUserId;


    public ModalityApprovedByCommitteeEvent(Long studentModalityId, Long committeeUserId) {
        super( NotificationType.MODALITY_APPROVED_BY_PROGRAM_CURRICULUM_COMMITTEE, studentModalityId, committeeUserId);
        this.studentModalityId = studentModalityId;
        this.committeeUserId = committeeUserId;
    }
}

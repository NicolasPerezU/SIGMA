package com.SIGMA.USCO.notifications.event;

import com.SIGMA.USCO.notifications.entity.enums.NotificationType;
import lombok.Getter;

@Getter
public class ModalityApprovedBySecretary extends DomainEvent {

    private final Long studentModalityId;
    private final Long secretaryId;

    public ModalityApprovedBySecretary(Long studentModalityId, Long secretaryId) {
        super(NotificationType.MODALITY_APPROVED_BY_SECRETARY, studentModalityId, secretaryId);
        this.studentModalityId = studentModalityId;
        this.secretaryId = secretaryId;
    }
}

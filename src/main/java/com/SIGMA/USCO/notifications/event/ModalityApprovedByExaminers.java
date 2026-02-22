package com.SIGMA.USCO.notifications.event;

import com.SIGMA.USCO.notifications.entity.enums.NotificationType;
import lombok.Getter;

@Getter
public class ModalityApprovedByExaminers extends DomainEvent {
    private final Long studentModalityId;
    private final Long examinerUserId;

    public ModalityApprovedByExaminers(Long studentModalityId, Long examinerUserId) {
        super(NotificationType.MODALITY_APPROVED_BY_EXAMINERS, studentModalityId, examinerUserId);
        this.studentModalityId = studentModalityId;
        this.examinerUserId = examinerUserId;
    }
}


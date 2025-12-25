package com.SIGMA.USCO.notifications.event;

import com.SIGMA.USCO.notifications.entity.enums.NotificationType;
import lombok.Getter;

@Getter
public class ModalityApprovedByCouncilEvent extends DomainEvent {

    private final Long studentModalityId;
    private final Long councilUserId;


    public ModalityApprovedByCouncilEvent(Long studentModalityId, Long councilUserId) {
        super( NotificationType.MODALITY_APPROVED_BY_COUNCIL, studentModalityId, councilUserId);
        this.studentModalityId = studentModalityId;
        this.councilUserId = councilUserId;
    }
}

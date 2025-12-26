package com.SIGMA.USCO.notifications.event;

import com.SIGMA.USCO.notifications.entity.enums.NotificationType;
import lombok.Getter;

@Getter
public class CancellationApprovedEvent extends DomainEvent {

    private final Long studentModalityId;
    private final Long councilUserId;

    public CancellationApprovedEvent(Long studentModalityId,Long councilUserId) {
        super(NotificationType.MODALITY_CANCELLATION_APPROVED,studentModalityId, councilUserId);
        this.studentModalityId = studentModalityId;
        this.councilUserId = councilUserId;
    }

}

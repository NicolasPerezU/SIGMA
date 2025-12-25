package com.SIGMA.USCO.notifications.event;

import com.SIGMA.USCO.notifications.entity.enums.NotificationType;
import lombok.Getter;

@Getter
public class CancellationRejectedEvent extends DomainEvent {

    private final Long studentModalityId;
    private final String reason;
    private final Long councilUserId;

    public CancellationRejectedEvent(Long studentModalityId, String reason, Long councilUserId) {
        super(NotificationType.MODALITY_CANCELLATION_REJECTED,studentModalityId,councilUserId);
        this.studentModalityId = studentModalityId;
        this.reason = reason;
        this.councilUserId = councilUserId;
    }

}

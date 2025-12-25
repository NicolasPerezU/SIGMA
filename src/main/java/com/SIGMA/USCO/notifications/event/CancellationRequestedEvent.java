package com.SIGMA.USCO.notifications.event;

import com.SIGMA.USCO.notifications.entity.enums.NotificationType;
import lombok.Getter;

@Getter
public class CancellationRequestedEvent extends DomainEvent {

    private final Long studentModalityId;
    private final Long studentId;

    public CancellationRequestedEvent(Long studentModalityId, Long studentId) {
        super(NotificationType.MODALITY_CANCELLATION_REQUESTED, studentModalityId,studentId);
        this.studentModalityId = studentModalityId;
        this.studentId = studentId;
    }
}

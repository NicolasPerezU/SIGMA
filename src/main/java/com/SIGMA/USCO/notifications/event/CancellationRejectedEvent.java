package com.SIGMA.USCO.notifications.event;

import com.SIGMA.USCO.notifications.entity.enums.NotificationType;
import lombok.Getter;

@Getter
public class CancellationRejectedEvent extends DomainEvent {

    private final Long studentModalityId;
    private final String reason;
    private final Long curriculumUserId;

    public CancellationRejectedEvent(Long studentModalityId, String reason, Long curriculumUserId) {
        super(NotificationType.MODALITY_CANCELLATION_REJECTED,studentModalityId,curriculumUserId);
        this.studentModalityId = studentModalityId;
        this.reason = reason;
        this.curriculumUserId = curriculumUserId;
    }

}

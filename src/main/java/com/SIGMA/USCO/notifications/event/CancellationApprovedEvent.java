package com.SIGMA.USCO.notifications.event;

import com.SIGMA.USCO.notifications.entity.enums.NotificationType;
import lombok.Getter;

@Getter
public class CancellationApprovedEvent extends DomainEvent {

    private final Long studentModalityId;
    private final Long curriculumUserId;

    public CancellationApprovedEvent(Long studentModalityId,Long curriculumUserId) {
        super(NotificationType.MODALITY_CANCELLATION_APPROVED,studentModalityId, curriculumUserId);
        this.studentModalityId = studentModalityId;
        this.curriculumUserId = curriculumUserId;
    }

}

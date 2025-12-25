package com.SIGMA.USCO.notifications.event;

import com.SIGMA.USCO.notifications.entity.enums.NotificationType;
import lombok.Getter;

@Getter
public class DirectorAssignedEvent extends DomainEvent {

    private final Long studentModalityId;
    private final Long directorId;
    private final Long responsibleUserId;

    public DirectorAssignedEvent(Long studentModalityId, Long directorId, Long responsibleUserId) {
        super(NotificationType.DIRECTOR_ASSIGNED, studentModalityId,directorId);
        this.studentModalityId = studentModalityId;
        this.directorId = directorId;
        this.responsibleUserId = responsibleUserId;
    }

}


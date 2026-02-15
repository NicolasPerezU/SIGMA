package com.SIGMA.USCO.notifications.event;

import com.SIGMA.USCO.notifications.entity.enums.NotificationType;
import lombok.Getter;


@Getter
public class DirectorChangedEvent extends DomainEvent {

    private final Long studentModalityId;
    private final Long studentId;
    private final Long previousDirectorId;
    private final Long newDirectorId;
    private final String reason;

    public DirectorChangedEvent(
            Long studentModalityId,
            Long studentId,
            Long previousDirectorId,
            Long newDirectorId,
            String reason
    ) {
        super(NotificationType.DIRECTOR_CHANGED, studentModalityId, studentId);
        this.studentModalityId = studentModalityId;
        this.studentId = studentId;
        this.previousDirectorId = previousDirectorId;
        this.newDirectorId = newDirectorId;
        this.reason = reason;
    }
}


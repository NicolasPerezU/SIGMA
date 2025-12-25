package com.SIGMA.USCO.notifications.event;


import com.SIGMA.USCO.notifications.entity.enums.NotificationType;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public abstract class DomainEvent {

    private final NotificationType eventType;
    private final LocalDateTime occurredAt;
    private final Long studentModalityId;
    private final Long actorUserId;

    public DomainEvent(
            NotificationType eventType,
            Long studentModalityId,
            Long actorUserId
    ) {
        this.eventType = eventType;
        this.studentModalityId = studentModalityId;
        this.actorUserId = actorUserId;
        this.occurredAt = LocalDateTime.now();
    }
}

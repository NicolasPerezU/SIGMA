package com.SIGMA.USCO.notifications.event;

import com.SIGMA.USCO.notifications.entity.enums.NotificationType;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class DefenseScheduledEvent extends DomainEvent {

    private final Long studentModalityId;
    private final LocalDateTime defenseDate;
    private final String defenseLocation;
    private final Long responsibleUserId;


    public DefenseScheduledEvent(Long studentModalityId, LocalDateTime defenseDate, String defenseLocation, Long responsibleUserId) {
        super(NotificationType.DEFENSE_SCHEDULED, studentModalityId, responsibleUserId);
        this.defenseDate = defenseDate;
        this.defenseLocation = defenseLocation;
        this.studentModalityId = studentModalityId;
        this.responsibleUserId = responsibleUserId;
    }
}

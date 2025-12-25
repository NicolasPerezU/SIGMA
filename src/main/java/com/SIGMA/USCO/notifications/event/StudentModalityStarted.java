package com.SIGMA.USCO.notifications.event;

import com.SIGMA.USCO.notifications.entity.enums.NotificationType;

public class StudentModalityStarted extends DomainEvent {


    public StudentModalityStarted(Long studentModalityId, Long studentId) {
        super(NotificationType.MODALITY_STARTED, studentModalityId, studentId);
    }
}

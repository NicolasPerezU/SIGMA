package com.SIGMA.USCO.notifications.event;

import com.SIGMA.USCO.notifications.entity.enums.NotificationType;

public class DefenseReadyByDirectorEvent extends DomainEvent {
    private Long examinerId;
    private String subject;
    private String message;

    public DefenseReadyByDirectorEvent() {
        super(NotificationType.READY_FOR_DEFENSE_REQUESTED, null, null);
    }

    public DefenseReadyByDirectorEvent(Long studentModalityId, Long examinerId, Long actorUserId, String subject, String message) {
        super(NotificationType.READY_FOR_DEFENSE_REQUESTED, studentModalityId, actorUserId);
        this.examinerId = examinerId;
        this.subject = subject;
        this.message = message;
    }

    public Long getExaminerId() {
        return examinerId;
    }
    public String getSubject() {
        return subject;
    }
    public String getMessage() {
        return message;
    }
}

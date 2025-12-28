package com.SIGMA.USCO.notifications.event;

import com.SIGMA.USCO.notifications.entity.enums.NotificationType;
import lombok.Getter;

@Getter
public class StudentDocumentUpdatedEvent extends DomainEvent {

    private final Long studentDocumentId;
    private final Long studentId;

    public StudentDocumentUpdatedEvent(Long studentModalityId, Long studentDocumentId, Long studentId) {
        super(NotificationType.DOCUMENT_UPLOADED, studentModalityId,studentId);
        this.studentDocumentId = studentDocumentId;
        this.studentId = studentId;


    }
}

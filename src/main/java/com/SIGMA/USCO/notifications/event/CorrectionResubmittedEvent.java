package com.SIGMA.USCO.notifications.event;

import com.SIGMA.USCO.notifications.entity.enums.NotificationType;
import lombok.Getter;

@Getter
public class CorrectionResubmittedEvent extends DomainEvent {

    private final Long studentModalityId;
    private final Long documentId;
    private final Long studentId;
    private final String documentName;

    public CorrectionResubmittedEvent(Long studentModalityId, Long documentId, Long studentId, String documentName, Long triggeredByUserId) {
        super(NotificationType.CORRECTION_RESUBMITTED, null, triggeredByUserId);
        this.studentModalityId = studentModalityId;
        this.documentId = documentId;
        this.studentId = studentId;
        this.documentName = documentName;
    }
}


package com.SIGMA.USCO.notifications.event;

import com.SIGMA.USCO.notifications.entity.enums.NotificationType;
import lombok.Getter;

@Getter
public class CorrectionApprovedEvent extends DomainEvent {

    private final Long studentModalityId;
    private final Long documentId;
    private final Long studentId;
    private final String documentName;

    public CorrectionApprovedEvent(Long studentModalityId, Long documentId, Long studentId, String documentName, Long triggeredByUserId) {
        super(NotificationType.CORRECTION_APPROVED, null, triggeredByUserId);
        this.studentModalityId = studentModalityId;
        this.documentId = documentId;
        this.studentId = studentId;
        this.documentName = documentName;
    }
}


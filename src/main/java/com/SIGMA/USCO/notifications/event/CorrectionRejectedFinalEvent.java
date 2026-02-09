package com.SIGMA.USCO.notifications.event;

import com.SIGMA.USCO.notifications.entity.enums.NotificationType;
import lombok.Getter;

@Getter
public class CorrectionRejectedFinalEvent extends DomainEvent {

    private final Long studentModalityId;
    private final Long documentId;
    private final Long studentId;
    private final String documentName;
    private final String reason;

    public CorrectionRejectedFinalEvent(Long studentModalityId, Long documentId, Long studentId, String documentName, String reason, Long triggeredByUserId) {
        super(NotificationType.CORRECTION_REJECTED_FINAL, null, triggeredByUserId);
        this.studentModalityId = studentModalityId;
        this.documentId = documentId;
        this.studentId = studentId;
        this.documentName = documentName;
        this.reason = reason;
    }
}


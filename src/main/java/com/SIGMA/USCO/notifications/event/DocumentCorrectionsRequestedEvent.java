package com.SIGMA.USCO.notifications.event;

import com.SIGMA.USCO.notifications.entity.enums.NotificationRecipientType;
import com.SIGMA.USCO.notifications.entity.enums.NotificationType;
import lombok.Getter;

@Getter
public class DocumentCorrectionsRequestedEvent extends DomainEvent {

    private final Long studentDocumentId;
    private final Long studentId;
    private final String observations;
    private final NotificationRecipientType requestedBy;

    public DocumentCorrectionsRequestedEvent(Long studentDocumentId, Long studentId, String observations, NotificationRecipientType requestedBy, Long triggeredByUserId) {
        super(requestedBy == NotificationRecipientType.SECRETARY
        ? NotificationType.DOCUMENT_CORRECTIONS_REQUESTED
        : NotificationType.DOCUMENT_CORRECTIONS_REQUESTED, null,
                triggeredByUserId);
        this.studentDocumentId = studentDocumentId;
        this.studentId = studentId;
        this.observations = observations;
        this.requestedBy = requestedBy;
    }

}

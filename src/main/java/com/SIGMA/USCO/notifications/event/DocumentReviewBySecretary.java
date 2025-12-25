package com.SIGMA.USCO.notifications.event;

import com.SIGMA.USCO.documents.entity.DocumentStatus;
import com.SIGMA.USCO.notifications.entity.enums.NotificationType;
import lombok.Getter;

@Getter
public class DocumentReviewBySecretary extends DomainEvent {

    private final Long studentDocumentId;
    private final DocumentStatus newStatus;
    private final String observations;

    protected DocumentReviewBySecretary(Long studentModalityId, Long studentDocumentId, DocumentStatus newStatus, String observations, Long secretaryId) {
        super(
                NotificationType.DOCUMENT_REVIEWED_BY_SECRETARY ,studentModalityId, secretaryId);
        this.studentDocumentId = studentDocumentId;
        this.newStatus = newStatus;
        this.observations = observations;
    }
}

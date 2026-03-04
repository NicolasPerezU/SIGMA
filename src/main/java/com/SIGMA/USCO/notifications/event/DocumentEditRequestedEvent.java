package com.SIGMA.USCO.notifications.event;

import com.SIGMA.USCO.notifications.entity.enums.NotificationType;
import lombok.Getter;

/**
 * Evento publicado cuando un estudiante solicita editar un documento previamente aprobado.
 */
@Getter
public class DocumentEditRequestedEvent extends DomainEvent {

    private final Long studentDocumentId;
    private final Long editRequestId;
    private final String reason;
    private final String documentName;

    public DocumentEditRequestedEvent(
            Long studentModalityId,
            Long studentDocumentId,
            Long editRequestId,
            String reason,
            String documentName,
            Long requesterId) {
        super(NotificationType.DOCUMENT_EDIT_REQUESTED, studentModalityId, requesterId);
        this.studentDocumentId = studentDocumentId;
        this.editRequestId = editRequestId;
        this.reason = reason;
        this.documentName = documentName;
    }
}


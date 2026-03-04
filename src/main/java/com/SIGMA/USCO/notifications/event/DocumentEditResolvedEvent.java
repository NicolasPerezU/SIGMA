package com.SIGMA.USCO.notifications.event;

import com.SIGMA.USCO.notifications.entity.enums.NotificationType;
import lombok.Getter;

/**
 * Evento publicado cuando un jurado resuelve (aprueba o rechaza) una solicitud de edición de documento.
 */
@Getter
public class DocumentEditResolvedEvent extends DomainEvent {

    private final Long studentDocumentId;
    private final Long editRequestId;
    private final boolean approved;
    private final String resolutionNotes;
    private final String documentName;

    public DocumentEditResolvedEvent(
            Long studentModalityId,
            Long studentDocumentId,
            Long editRequestId,
            boolean approved,
            String resolutionNotes,
            String documentName,
            Long resolvedByUserId) {
        super(approved ? NotificationType.DOCUMENT_EDIT_APPROVED : NotificationType.DOCUMENT_EDIT_REJECTED,
                studentModalityId, resolvedByUserId);
        this.studentDocumentId = studentDocumentId;
        this.editRequestId = editRequestId;
        this.approved = approved;
        this.resolutionNotes = resolutionNotes;
        this.documentName = documentName;
    }
}


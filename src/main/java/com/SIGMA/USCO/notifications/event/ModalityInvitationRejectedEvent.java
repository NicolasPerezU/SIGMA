package com.SIGMA.USCO.notifications.event;

import com.SIGMA.USCO.notifications.entity.enums.NotificationType;
import lombok.Getter;

@Getter
public class ModalityInvitationRejectedEvent extends DomainEvent {

    private final Long invitationId;
    private final Long rejectedById;
    private final Long leaderId;
    private final String rejectedByName;
    private final String modalityName;

    public ModalityInvitationRejectedEvent(
            Long studentModalityId,
            Long invitationId,
            Long rejectedById,
            Long leaderId,
            String rejectedByName,
            String modalityName
    ) {
        super(NotificationType.MODALITY_INVITATION_REJECTED, studentModalityId, rejectedById);
        this.invitationId = invitationId;
        this.rejectedById = rejectedById;
        this.leaderId = leaderId;
        this.rejectedByName = rejectedByName;
        this.modalityName = modalityName;
    }
}


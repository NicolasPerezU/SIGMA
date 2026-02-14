package com.SIGMA.USCO.notifications.event;

import com.SIGMA.USCO.notifications.entity.enums.NotificationType;
import lombok.Getter;

@Getter
public class ModalityInvitationAcceptedEvent extends DomainEvent {

    private final Long invitationId;
    private final Long acceptedById;
    private final Long leaderId;
    private final String acceptedByName;
    private final String modalityName;

    public ModalityInvitationAcceptedEvent(
            Long studentModalityId,
            Long invitationId,
            Long acceptedById,
            Long leaderId,
            String acceptedByName,
            String modalityName
    ) {
        super(NotificationType.MODALITY_INVITATION_ACCEPTED, studentModalityId, acceptedById);
        this.invitationId = invitationId;
        this.acceptedById = acceptedById;
        this.leaderId = leaderId;
        this.acceptedByName = acceptedByName;
        this.modalityName = modalityName;
    }
}


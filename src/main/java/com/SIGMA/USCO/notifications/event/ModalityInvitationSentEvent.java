package com.SIGMA.USCO.notifications.event;

import com.SIGMA.USCO.notifications.entity.enums.NotificationType;
import lombok.Getter;

@Getter
public class ModalityInvitationSentEvent extends DomainEvent {

    private final Long invitationId;
    private final Long inviteeId;
    private final Long inviterId;
    private final String modalityName;
    private final String inviterName;

    public ModalityInvitationSentEvent(
            Long studentModalityId,
            Long invitationId,
            Long inviteeId,
            Long inviterId,
            String modalityName,
            String inviterName
    ) {
        super(NotificationType.MODALITY_INVITATION_RECEIVED, studentModalityId, inviterId);
        this.invitationId = invitationId;
        this.inviteeId = inviteeId;
        this.inviterId = inviterId;
        this.modalityName = modalityName;
        this.inviterName = inviterName;
    }
}


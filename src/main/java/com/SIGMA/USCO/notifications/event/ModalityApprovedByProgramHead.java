package com.SIGMA.USCO.notifications.event;

import com.SIGMA.USCO.notifications.entity.enums.NotificationType;
import lombok.Getter;

@Getter
public class ModalityApprovedByProgramHead extends DomainEvent {

    private final Long studentModalityId;
    private final Long programHeadId;

    public ModalityApprovedByProgramHead(Long studentModalityId, Long programHeadId) {
        super(NotificationType.MODALITY_APPROVED_BY_PROGRAM_HEAD, studentModalityId, programHeadId);
        this.studentModalityId = studentModalityId;
        this.programHeadId = programHeadId;
    }
}

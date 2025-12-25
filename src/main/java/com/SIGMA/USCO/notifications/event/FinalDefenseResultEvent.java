package com.SIGMA.USCO.notifications.event;

import com.SIGMA.USCO.Modalities.Entity.enums.AcademicDistinction;
import com.SIGMA.USCO.Modalities.Entity.enums.ModalityProcessStatus;
import com.SIGMA.USCO.notifications.entity.enums.NotificationType;
import lombok.Getter;

@Getter
public class FinalDefenseResultEvent extends DomainEvent {

    private final Long studentModalityId;
    private final ModalityProcessStatus finalStatus;
    private final AcademicDistinction academicDistinction;
    private final String observations;
    private final Long responsibleUserId;

    public FinalDefenseResultEvent(Long studentModalityId, ModalityProcessStatus finalStatus, AcademicDistinction academicDistinction, String observations, Long responsibleId) {
        super(NotificationType.DEFENSE_COMPLETED,studentModalityId, responsibleId);
        this.studentModalityId = studentModalityId;
        this.finalStatus = finalStatus;
        this.academicDistinction = academicDistinction;
        this.observations = observations;
        this.responsibleUserId = responsibleId;

    }
}

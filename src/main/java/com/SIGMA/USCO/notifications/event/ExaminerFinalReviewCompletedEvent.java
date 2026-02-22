package com.SIGMA.USCO.notifications.event;

import lombok.Getter;

@Getter
public class ExaminerFinalReviewCompletedEvent extends DomainEvent {
    private Long studentModalityId;
    private Long projectDirectorId;

    public ExaminerFinalReviewCompletedEvent() {
        super();
    }

    public ExaminerFinalReviewCompletedEvent(Long studentModalityId, Long projectDirectorId) {
        super(null, studentModalityId, null); // Ajusta según tu lógica de eventos
        this.studentModalityId = studentModalityId;
        this.projectDirectorId = projectDirectorId;
    }
}

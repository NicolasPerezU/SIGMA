package com.SIGMA.USCO.notifications.event;

import com.SIGMA.USCO.notifications.entity.enums.NotificationType;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class CorrectionDeadlineExpiredEvent extends DomainEvent {

    private final Long studentModalityId;
    private final Long studentId;
    private final LocalDateTime requestDate;

    public CorrectionDeadlineExpiredEvent(Long studentModalityId, Long studentId, LocalDateTime requestDate) {
        super(NotificationType.CORRECTION_DEADLINE_EXPIRED, null, null);
        this.studentModalityId = studentModalityId;
        this.studentId = studentId;
        this.requestDate = requestDate;
    }
}


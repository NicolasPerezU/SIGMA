package com.SIGMA.USCO.notifications.event;

import com.SIGMA.USCO.notifications.entity.enums.NotificationType;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class CorrectionDeadlineReminderEvent extends DomainEvent {

    private final Long studentModalityId;
    private final Long studentId;
    private final LocalDateTime deadline;
    private final int daysRemaining;

    public CorrectionDeadlineReminderEvent(Long studentModalityId, Long studentId, LocalDateTime deadline, int daysRemaining) {
        super(NotificationType.CORRECTION_DEADLINE_REMINDER, null, null);
        this.studentModalityId = studentModalityId;
        this.studentId = studentId;
        this.deadline = deadline;
        this.daysRemaining = daysRemaining;
    }
}


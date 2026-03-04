package com.SIGMA.USCO.notifications.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ExaminersAssignedEvent {
    private final Long studentModalityId;
}


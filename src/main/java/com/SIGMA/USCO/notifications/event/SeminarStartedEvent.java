package com.SIGMA.USCO.notifications.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeminarStartedEvent {

    private String recipientEmail;
    private String recipientName;
    private String seminarName;
    private LocalDateTime startDate;
    private Integer totalHours;
    private String programName;
}


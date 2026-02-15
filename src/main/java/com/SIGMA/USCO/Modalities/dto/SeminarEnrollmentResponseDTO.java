package com.SIGMA.USCO.Modalities.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SeminarEnrollmentResponseDTO {

    private Long enrollmentId;

    private Long seminarId;

    private String seminarName;

    private Long studentId;

    private String studentName;

    private String studentEmail;

    private String status;

    private String statusDescription;

    private LocalDateTime enrollmentDate;

    private Integer currentParticipants;

    private Integer maxParticipants;

    private Integer availableSpots;
}


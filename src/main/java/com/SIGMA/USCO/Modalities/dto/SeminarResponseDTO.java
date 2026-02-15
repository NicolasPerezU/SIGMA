package com.SIGMA.USCO.Modalities.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SeminarResponseDTO {

    private Long id;

    private Long academicProgramId;

    private String academicProgramName;

    private String facultyName;

    private String name;

    private String description;

    private BigDecimal totalCost;

    private Integer maxParticipants;

    private Integer minParticipants;

    private Integer currentParticipants;

    private Integer availableSpots;

    private Integer totalHours;

    private String status;

    private String statusDescription;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    private Boolean canEnroll;
}



package com.SIGMA.USCO.Modalities.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateSeminarDTO {

    private Long academicProgramId;

    private String name;

    private String description;

    private BigDecimal totalCost;

    private Integer maxParticipants;

    private Integer minParticipants;

    private Integer totalHours;
}



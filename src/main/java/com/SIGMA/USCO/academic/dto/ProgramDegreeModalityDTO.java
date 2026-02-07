package com.SIGMA.USCO.academic.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProgramDegreeModalityDTO {

    private Long id;
    private Long academicProgramId;
    private String academicProgramName;
    private String facultyName;
    private Long degreeModalityId;
    private String degreeModalityName;
    private String degreeModalityDescription;
    private Long creditsRequired;
    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}


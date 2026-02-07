package com.SIGMA.USCO.academic.dto;

import lombok.*;
import org.springframework.stereotype.Service;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProgramDegreeModalityRequest {

    private Long academicProgramId;

    private Long degreeModalityId;

    private Long creditsRequired;

}

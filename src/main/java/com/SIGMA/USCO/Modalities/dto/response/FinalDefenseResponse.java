package com.SIGMA.USCO.Modalities.dto.response;

import com.SIGMA.USCO.Modalities.Entity.enums.AcademicDistinction;
import com.SIGMA.USCO.Modalities.Entity.enums.ModalityProcessStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FinalDefenseResponse {

    private Long studentModalityId;

    private String studentName;
    private String studentEmail;

    private ModalityProcessStatus finalStatus;
    private boolean approved;

    private AcademicDistinction academicDistinction;

    private String observations;

    private LocalDateTime evaluationDate;

    private String evaluatedBy;

}

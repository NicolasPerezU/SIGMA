package com.SIGMA.USCO.report.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ModalityReportDTO {

    private String modalityName;
    private String modalityType;
    private String modalityStatus;
    private Long totalStudents;
    private String directors;
    private Integer year;
    private Integer semester;

}

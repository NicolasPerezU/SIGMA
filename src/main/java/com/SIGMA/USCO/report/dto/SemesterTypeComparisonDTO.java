package com.SIGMA.USCO.report.dto;


import com.SIGMA.USCO.Modalities.Entity.enums.ModalityType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SemesterTypeComparisonDTO {

    private Integer year;
    private Integer semester;
    private ModalityType modalityType;
    private Long totalStudents;
}

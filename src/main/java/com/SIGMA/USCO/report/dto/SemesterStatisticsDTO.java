package com.SIGMA.USCO.report.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO con estadísticas de un semestre académico
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SemesterStatisticsDTO {

    private Integer totalEnrolled;
    private Integer totalCompleted;
    private Integer totalInProgress;
    private Integer totalCancelled;
    private Double completionRate;
    private String mostPopularModality;
}


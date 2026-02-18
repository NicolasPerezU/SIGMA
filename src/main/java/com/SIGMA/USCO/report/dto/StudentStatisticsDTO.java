package com.SIGMA.USCO.report.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO con estadísticas de estudiantes para reportes
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentStatisticsDTO {

    private Integer totalActive;
    private Integer totalCompleted;
    private Integer totalCancelled;
    private Integer totalInProgress;
    private Integer totalPendingApproval;
    private Double averageProgress;
    private String programName;
}


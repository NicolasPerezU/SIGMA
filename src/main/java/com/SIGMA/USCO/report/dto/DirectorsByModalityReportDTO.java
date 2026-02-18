package com.SIGMA.USCO.report.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

/**
 * DTO para el Reporte de Directores por Modalidad
 * RF-49 - Reporte de Directores Asociados a Modalidades
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class DirectorsByModalityReportDTO extends BaseReportDTO {

    private String modalityType;
    private Integer totalDirectors;
    private List<DirectorWorkloadDTO> directors;
    private DirectorStatisticsDTO statistics;
}


package com.SIGMA.USCO.report.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * Resumen ejecutivo del reporte global de modalidades
 * Contiene métricas clave y estadísticas agregadas
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExecutiveSummaryDTO {


    private Integer totalActiveModalities;


    private Integer totalActiveStudents;


    private Integer totalActiveDirectors;


    private Map<String, Long> modalitiesByStatus;

    private Map<String, Long> modalitiesByType;

    private Integer individualModalities;

    private Integer groupModalities;

    private Double averageStudentsPerGroup;

    private Integer modalitiesWithoutDirector;

    private Integer modalitiesInReview;


    private Double overallProgressRate;

}


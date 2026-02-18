package com.SIGMA.USCO.report.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO con estadísticas de directores para reportes
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DirectorStatisticsDTO {

    private Integer totalActiveDirectors;
    private Integer totalDirectorsWithMaxCapacity;
    private Double averageProjectsPerDirector;
    private Integer maxProjectsPerDirector;
    private Integer minProjectsPerDirector;
}


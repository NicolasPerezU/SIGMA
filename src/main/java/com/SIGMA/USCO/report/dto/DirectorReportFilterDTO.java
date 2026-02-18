package com.SIGMA.USCO.report.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para los filtros del reporte de directores
 * RF-49 - Generación de Reportes por Director Asignado
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DirectorReportFilterDTO {

    /**
     * ID del director específico (opcional)
     * Si no se especifica, se incluyen todos los directores
     */
    private Long directorId;

    /**
     * Filtrar solo modalidades en estados específicos
     */
    private java.util.List<String> processStatuses;

    /**
     * Filtrar por tipos de modalidad específicos
     */
    private java.util.List<String> modalityTypes;

    /**
     * Solo directores con sobrecarga de trabajo
     */
    private Boolean onlyOverloaded;

    /**
     * Solo directores disponibles
     */
    private Boolean onlyAvailable;

    /**
     * Solo modalidades activas
     */
    private Boolean onlyActiveModalities;

    /**
     * Incluir análisis de carga de trabajo
     */
    private Boolean includeWorkloadAnalysis;
}


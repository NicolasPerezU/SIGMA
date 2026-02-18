package com.SIGMA.USCO.report.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para los filtros de la comparativa de modalidades por tipo
 * RF-48 - Comparativa de Modalidades por Tipo de Grado
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModalityComparisonFilterDTO {

    /**
     * Año específico para el análisis (opcional)
     * Si no se especifica, se toman todos los años
     */
    private Integer year;

    /**
     * Semestre específico (1 o 2) para el análisis (opcional)
     * Si no se especifica, se toman ambos semestres
     */
    private Integer semester;

    /**
     * Incluir comparación histórica con periodos anteriores
     */
    private Boolean includeHistoricalComparison;

    /**
     * Número de periodos anteriores a incluir en la comparación histórica
     * Por defecto: 3 periodos
     */
    private Integer historicalPeriodsCount;

    /**
     * Incluir análisis de tendencias
     */
    private Boolean includeTrendsAnalysis;

    /**
     * Solo incluir modalidades en estados específicos
     */
    private Boolean onlyActiveModalities;
}


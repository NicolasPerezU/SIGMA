package com.SIGMA.USCO.report.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO para los filtros del reporte de listado de estudiantes
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentListingFilterDTO {

    /**
     * Filtrar por estados de la modalidad
     * Ejemplo: ["PROPOSAL_APPROVED", "IN_PROGRESS", "COMPLETED"]
     */
    private List<String> statuses;

    /**
     * Filtrar por tipos de modalidad
     * Ejemplo: ["Proyecto de Grado", "Pasantía", "Práctica Profesional"]
     */
    private List<String> modalityTypes;

    /**
     * Filtrar por semestres específicos
     * Formato: ["2025-1", "2025-2", "2026-1"]
     */
    private List<String> semesters;

    /**
     * Filtrar por año específico
     */
    private Integer year;

    /**
     * Filtrar por estado de línea de tiempo
     * Valores: ON_TIME, DELAYED, AT_RISK
     */
    private String timelineStatus;

    /**
     * Filtrar por tipo de modalidad (individual/grupal)
     * Valores: INDIVIDUAL, GROUP
     */
    private String modalityTypeFilter;

    /**
     * Filtrar si tiene director asignado
     */
    private Boolean hasDirector;

    /**
     * Ordenar resultados
     * Valores: NAME, DATE, STATUS, MODALITY, PROGRESS
     */
    private String sortBy;

    /**
     * Dirección de orden
     * Valores: ASC, DESC
     */
    private String sortDirection;

    /**
     * Incluir estudiantes inactivos
     */
    private Boolean includeInactive;
}


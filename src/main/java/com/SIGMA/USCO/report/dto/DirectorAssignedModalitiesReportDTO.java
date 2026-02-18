package com.SIGMA.USCO.report.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * DTO para el reporte de modalidades por director asignado
 * RF-49 - Generación de Reportes por Director Asignado
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DirectorAssignedModalitiesReportDTO {

    /**
     * Información del reporte
     */
    private LocalDateTime generatedAt;
    private String generatedBy;
    private Long academicProgramId;
    private String academicProgramName;
    private String academicProgramCode;

    /**
     * Información del director (si se filtra por uno específico)
     */
    private DirectorInfoDTO directorInfo;

    /**
     * Resumen general
     */
    private DirectorSummaryDTO summary;

    /**
     * Lista de directores con sus modalidades
     */
    private List<DirectorWithModalitiesDTO> directors;

    /**
     * Estadísticas por estado
     */
    private Map<String, Integer> modalitiesByStatus;

    /**
     * Estadísticas por tipo de modalidad
     */
    private Map<String, Integer> modalitiesByType;

    /**
     * Análisis de carga de trabajo
     */
    private WorkloadAnalysisDTO workloadAnalysis;

    /**
     * Metadata del reporte
     */
    private ReportMetadataDTO metadata;

    /**
     * DTO para información básica del director
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DirectorInfoDTO {
        private Long directorId;
        private String fullName;
        private String email;
        private String academicTitle;
        private Integer totalAssignedModalities;
        private Integer activeModalities;
        private Integer completedModalities;
    }

    /**
     * DTO para el resumen general
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DirectorSummaryDTO {
        private Integer totalDirectors;
        private Integer totalModalitiesAssigned;
        private Integer totalActiveModalities;
        private Integer totalStudentsSupervised;
        private Double averageModalitiesPerDirector;
        private String directorWithMostModalities;
        private Integer maxModalitiesCount;
        private String directorWithLeastModalities;
        private Integer minModalitiesCount;
        private Integer directorsOverloaded; // Con más de X modalidades
        private Integer directorsAvailable; // Con menos de X modalidades
    }

    /**
     * DTO para un director con sus modalidades asignadas
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DirectorWithModalitiesDTO {
        private Long directorId;
        private String fullName;
        private String email;
        private String academicTitle;
        private Integer totalAssignedModalities;
        private Integer activeModalities;
        private Integer completedModalities;
        private Integer pendingApprovalModalities;
        private List<ModalityDetailDTO> modalities;
        private String workloadStatus; // NORMAL, HIGH, OVERLOADED
        private Double averageDaysPerModality;
    }

    /**
     * DTO para detalle de una modalidad
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ModalityDetailDTO {
        private Long modalityId;
        private String modalityType;
        private String modalityTypeName;
        private List<StudentBasicInfoDTO> students;
        private String currentStatus;
        private String statusDescription;
        private LocalDateTime startDate;
        private LocalDateTime lastUpdate;
        private Integer daysSinceStart;
        private Integer daysInCurrentStatus;
        private String projectTitle;
        private Boolean hasPendingActions;
        private String observations;
    }

    /**
     * DTO para información básica del estudiante
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StudentBasicInfoDTO {
        private Long studentId;
        private String fullName;
        private String studentCode;
        private String email;
        private Boolean isLeader;
    }

    /**
     * DTO para análisis de carga de trabajo
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WorkloadAnalysisDTO {
        private Integer recommendedMaxModalities;
        private List<String> directorsOverloaded;
        private List<String> directorsAvailable;
        private Double averageWorkload;
        private String overallWorkloadStatus; // BALANCED, UNBALANCED
        private Map<String, Integer> workloadDistribution; // LOW, NORMAL, HIGH, OVERLOADED
    }
}


package com.SIGMA.USCO.report.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * DTO para el reporte de modalidades finalizadas (exitosas y fallidas)
 * Incluye análisis completo de resultados, estadísticas y tendencias
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompletedModalitiesReportDTO {

    /**
     * Información del reporte
     */
    private LocalDateTime generatedAt;
    private String generatedBy;
    private Long academicProgramId;
    private String academicProgramName;
    private String academicProgramCode;

    /**
     * Filtros aplicados
     */
    private AppliedFiltersDTO appliedFilters;

    /**
     * Resumen ejecutivo
     */
    private ExecutiveSummaryDTO executiveSummary;

    /**
     * Listado de modalidades completadas
     */
    private List<CompletedModalityDetailDTO> completedModalities;

    /**
     * Estadísticas generales
     */
    private GeneralStatisticsDTO generalStatistics;

    /**
     * Análisis por resultado
     */
    private ResultAnalysisDTO resultAnalysis;

    /**
     * Análisis por tipo de modalidad
     */
    private List<ModalityTypeAnalysisDTO> modalityTypeAnalysis;

    /**
     * Análisis temporal
     */
    private TemporalAnalysisDTO temporalAnalysis;

    /**
     * Estadísticas de directores
     */
    private DirectorPerformanceDTO directorPerformance;

    /**
     * Análisis de distinciones
     */
    private DistinctionAnalysisDTO distinctionAnalysis;

    /**
     * Metadata del reporte
     */
    private ReportMetadataDTO metadata;

    /**
     * Filtros aplicados
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AppliedFiltersDTO {
        private List<String> modalityTypes;
        private List<String> results; // SUCCESS, FAILED
        private Integer year;
        private Integer semester;
        private Boolean includeDistinctions;
        private String filterDescription;
        private Boolean hasFilters;
    }

    /**
     * Resumen ejecutivo
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ExecutiveSummaryDTO {
        private Integer totalCompleted;
        private Integer totalSuccessful;
        private Integer totalFailed;
        private Double successRate;
        private Double failureRate;
        private Integer withDistinction;
        private Double averageGrade;
        private Double averageCompletionDays;
        private Integer totalStudents;
        private Integer uniqueDirectors;
        private Map<String, Integer> quickStats;
    }

    /**
     * Detalle de modalidad completada
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CompletedModalityDetailDTO {
        // Información de la modalidad
        private Long modalityId;
        private String modalityType;
        private String modalityTypeName;
        private String result; // SUCCESS, FAILED
        private LocalDateTime completionDate;
        private Integer completionDays;

        // Calificación
        private Double finalGrade;
        private String gradeDescription;
        private String academicDistinction;

        // Información de estudiantes
        private List<StudentInfoDTO> students;
        private Integer studentCount;
        private Boolean isGroup;

        // Director
        private String directorName;
        private String directorEmail;

        // Fechas clave
        private LocalDateTime selectionDate;
        private LocalDateTime defenseDate;

        // Sustentación
        private String defenseLocation;
        private List<String> examiners;

        // Periodo académico
        private Integer year;
        private Integer semester;
        private String periodLabel;

        // Observaciones
        private String observations;
    }

    /**
     * Información de estudiante en modalidad completada
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StudentInfoDTO {
        private Long studentId;
        private String studentCode;
        private String fullName;
        private String email;
        private Double cumulativeGPA;
        private Integer completedCredits;
        private Boolean isLeader;
    }

    /**
     * Estadísticas generales
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GeneralStatisticsDTO {
        // Resultados
        private Integer totalCompleted;
        private Integer approved;
        private Integer failed;
        private Double approvalRate;

        // Tiempos
        private Double averageCompletionDays;
        private Integer fastestCompletionDays;
        private Integer slowestCompletionDays;
        private Double medianCompletionDays;

        // Calificaciones
        private Double averageGrade;
        private Double highestGrade;
        private Double lowestGrade;
        private Double medianGrade;

        // Distinciones
        private Integer withMeritorious;
        private Integer withLaudeate;
        private Integer withoutDistinction;

        // Por tipo
        private Integer individualModalities;
        private Integer groupModalities;

        // Distribuciones
        private Map<String, Integer> byModalityType;
        private Map<String, Integer> byResult;
        private Map<String, Integer> byDistinction;
    }

    /**
     * Análisis por resultado
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ResultAnalysisDTO {
        // Exitosas
        private Integer successfulCount;
        private Double successRate;
        private Double averageSuccessGrade;
        private Double averageSuccessCompletionDays;
        private List<String> successFactors;

        // Fallidas
        private Integer failedCount;
        private Double failureRate;
        private Double averageFailureGrade;
        private Double averageFailureCompletionDays;
        private List<String> failureReasons;

        // Comparativa
        private String performanceVerdict;
        private List<String> recommendations;
    }

    /**
     * Análisis por tipo de modalidad
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ModalityTypeAnalysisDTO {
        private String modalityType;
        private Integer totalCompleted;
        private Integer successful;
        private Integer failed;
        private Double successRate;
        private Double averageGrade;
        private Double averageCompletionDays;
        private Integer withDistinction;
        private String performance; // EXCELLENT, GOOD, REGULAR, POOR
    }

    /**
     * Análisis temporal
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TemporalAnalysisDTO {
        private List<PeriodDataDTO> periodData;
        private String trend; // IMPROVING, STABLE, DECLINING
        private Double growthRate;
        private String bestPeriod;
        private String worstPeriod;
    }

    /**
     * Datos por periodo
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PeriodDataDTO {
        private String period;
        private Integer year;
        private Integer semester;
        private Integer completed;
        private Integer successful;
        private Integer failed;
        private Double successRate;
        private Double averageGrade;
    }

    /**
     * Desempeño de directores
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DirectorPerformanceDTO {
        private Integer totalDirectors;
        private List<TopDirectorDTO> topDirectors;
        private Double averageSuccessRateByDirector;
        private String bestDirector;
        private Integer bestDirectorSuccessCount;
    }

    /**
     * Top director
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TopDirectorDTO {
        private String directorName;
        private Integer totalSupervised;
        private Integer successful;
        private Integer failed;
        private Double successRate;
        private Double averageGrade;
        private Integer withDistinction;
    }

    /**
     * Análisis de distinciones
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DistinctionAnalysisDTO {
        private Integer totalWithDistinction;
        private Integer meritorious;
        private Integer laureate;
        private Double distinctionRate;
        private List<String> modalitiesWithMostDistinctions;
        private List<String> directorsWithMostDistinctions;
    }
}


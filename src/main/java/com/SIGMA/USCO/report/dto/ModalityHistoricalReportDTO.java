package com.SIGMA.USCO.report.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * DTO para el reporte histórico detallado de una modalidad específica
 * Análisis completo de evolución temporal de una modalidad de grado
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModalityHistoricalReportDTO {

    /**
     * Información del reporte
     */
    private LocalDateTime generatedAt;
    private String generatedBy;
    private Long academicProgramId;
    private String academicProgramName;
    private String academicProgramCode;

    /**
     * Información de la modalidad analizada
     */
    private ModalityInfoDTO modalityInfo;

    /**
     * Estado actual de la modalidad
     */
    private CurrentStateDTO currentState;

    /**
     * Análisis histórico por periodos académicos
     */
    private List<AcademicPeriodAnalysisDTO> historicalAnalysis;

    /**
     * Análisis de tendencias y evolución
     */
    private TrendsEvolutionDTO trendsEvolution;

    /**
     * Comparativa entre periodos
     */
    private ComparativeAnalysisDTO comparativeAnalysis;

    /**
     * Estadísticas de directores
     */
    private DirectorStatisticsDTO directorStatistics;

    /**
     * Estadísticas de estudiantes
     */
    private StudentStatisticsDTO studentStatistics;

    /**
     * Análisis de desempeño
     */
    private PerformanceAnalysisDTO performanceAnalysis;

    /**
     * Proyecciones futuras
     */
    private ProjectionsDTO projections;

    /**
     * Metadata del reporte
     */
    private ReportMetadataDTO metadata;

    /**
     * Información básica de la modalidad
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ModalityInfoDTO {
        private Long modalityId;
        private String modalityName;
        private String modalityCode;
        private String description;
        private Boolean requiresDirector;
        private String modalityType; // INDIVIDUAL, GROUP
        private Boolean isActive;
        private LocalDateTime createdAt;
        private Integer yearsActive;
        private Integer totalHistoricalInstances;
    }

    /**
     * Estado actual de la modalidad
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CurrentStateDTO {
        private Integer currentPeriodYear;
        private Integer currentPeriodSemester;
        private Integer activeInstances;
        private Integer totalStudentsEnrolled;
        private Integer assignedDirectors;
        private Integer completedInstances;
        private Integer inProgressInstances;
        private Integer inReviewInstances;
        private Double averageCompletionDays;
        private String currentPopularity; // HIGH, MEDIUM, LOW
        private Integer positionInRanking; // Posición entre todas las modalidades
    }

    /**
     * Análisis por periodo académico
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AcademicPeriodAnalysisDTO {
        private Integer year;
        private Integer semester;
        private String periodLabel;
        private Integer totalInstances;
        private Integer studentsEnrolled;
        private Integer individualInstances;
        private Integer groupInstances;
        private Integer completedSuccessfully;
        private Integer abandoned;
        private Integer cancelled;
        private Double completionRate; // Porcentaje
        private Double averageCompletionDays;
        private Integer directorsInvolved;
        private List<String> topDirectors; // Top 3
        private Double averageGrade; // Si está disponible
        private Map<String, Integer> distributionByStatus;
        private String observations;
    }

    /**
     * Análisis de tendencias y evolución
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TrendsEvolutionDTO {
        private String overallTrend; // GROWING, STABLE, DECLINING
        private Double growthRate; // Porcentaje de crecimiento
        private Integer peakYear;
        private Integer peakSemester;
        private Integer peakInstances;
        private Integer lowestYear;
        private Integer lowestSemester;
        private Integer lowestInstances;
        private List<TrendPointDTO> evolutionPoints;
        private String popularityTrend; // Tendencia de popularidad
        private String completionTrend; // Tendencia de completitud
        private List<String> identifiedPatterns; // Patrones identificados
    }

    /**
     * Punto de tendencia
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TrendPointDTO {
        private String period;
        private Integer value;
        private String indicator; // UP, DOWN, STABLE
        private Double changePercentage;
    }

    /**
     * Análisis comparativo entre periodos
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ComparativeAnalysisDTO {
        private PeriodComparisonDTO currentVsPrevious;
        private PeriodComparisonDTO currentVsLastYear;
        private PeriodComparisonDTO bestVsWorst;
        private Map<String, Double> averagesByYear;
        private List<String> keyFindings;
    }

    /**
     * Comparación entre dos periodos
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PeriodComparisonDTO {
        private String period1Label;
        private Integer period1Instances;
        private Integer period1Students;
        private String period2Label;
        private Integer period2Instances;
        private Integer period2Students;
        private Double instancesChange; // Porcentaje
        private Double studentsChange; // Porcentaje
        private String verdict; // IMPROVED, DECLINED, STABLE
    }

    /**
     * Estadísticas de directores
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DirectorStatisticsDTO {
        private Integer totalUniqueDirectors;
        private Integer currentActiveDirectors;
        private List<TopDirectorDTO> topDirectorsAllTime;
        private List<TopDirectorDTO> topDirectorsCurrentPeriod;
        private Double averageInstancesPerDirector;
        private String mostExperiencedDirector;
        private Integer mostExperiencedCount;
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
        private Integer instancesSupervised;
        private Integer studentsSupervised;
        private Double successRate;
        private List<String> periods;
    }

    /**
     * Estadísticas de estudiantes
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StudentStatisticsDTO {
        private Integer totalHistoricalStudents;
        private Integer currentStudents;
        private Double averageStudentsPerInstance;
        private Integer maxStudentsInGroup;
        private Integer minStudentsInGroup;
        private Double individualVsGroupRatio;
        private Map<String, Integer> studentsBySemester;
        private String preferredType; // INDIVIDUAL, GROUP
    }

    /**
     * Análisis de desempeño
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PerformanceAnalysisDTO {
        private Double overallCompletionRate;
        private Double averageCompletionTimeDays;
        private Double successRate;
        private Double abandonmentRate;
        private Integer fastestCompletionDays;
        private Integer slowestCompletionDays;
        private Map<String, Double> completionRateByYear;
        private Map<String, Double> successRateByYear;
        private String performanceVerdict; // EXCELLENT, GOOD, REGULAR, NEEDS_IMPROVEMENT
        private List<String> strengthPoints;
        private List<String> improvementAreas;
    }

    /**
     * Proyecciones futuras
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProjectionsDTO {
        private Integer projectedNextSemester;
        private Integer projectedNextYear;
        private String demandProjection; // HIGH, MEDIUM, LOW
        private String recommendedActions;
        private List<String> opportunities;
        private List<String> risks;
        private Double confidenceLevel; // 0-100
    }
}


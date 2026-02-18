package com.SIGMA.USCO.report.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * DTO para el reporte comparativo de modalidades por tipo de grado
 * RF-48 - Comparativa de Modalidades por Tipo de Grado
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModalityTypeComparisonReportDTO {

    /**
     * Fecha y hora de generación del reporte
     */
    private LocalDateTime generatedAt;

    /**
     * Usuario que generó el reporte
     */
    private String generatedBy;

    /**
     * ID del programa académico
     */
    private Long academicProgramId;

    /**
     * Nombre del programa académico
     */
    private String academicProgramName;

    /**
     * Código del programa académico
     */
    private String academicProgramCode;

    /**
     * Año del reporte (si se especificó filtro por periodo)
     */
    private Integer year;

    /**
     * Semestre del reporte (si se especificó filtro por periodo)
     */
    private Integer semester;

    /**
     * Resumen general de la comparativa
     */
    private ComparisonSummaryDTO summary;

    /**
     * Comparativa detallada por tipo de modalidad
     */
    private List<ModalityTypeStatisticsDTO> modalityTypeStatistics;

    /**
     * Comparativa histórica por periodo (si aplica)
     */
    private List<PeriodComparisonDTO> historicalComparison;

    /**
     * Distribución de estudiantes por tipo
     */
    private Map<String, Integer> studentDistributionByType;

    /**
     * Tendencias y análisis
     */
    private TrendsAnalysisDTO trendsAnalysis;

    /**
     * Metadata del reporte
     */
    private ReportMetadataDTO metadata;

    /**
     * DTO para el resumen general de la comparativa
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ComparisonSummaryDTO {
        private Integer totalModalityTypes;
        private Integer totalModalities;
        private Integer totalStudents;
        private String mostPopularType;
        private Integer mostPopularTypeCount;
        private String leastPopularType;
        private Integer leastPopularTypeCount;
        private Double averageModalitiesPerType;
        private Double averageStudentsPerType;
    }

    /**
     * DTO para estadísticas de cada tipo de modalidad
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ModalityTypeStatisticsDTO {
        private Long modalityTypeId;
        private String modalityTypeName;
        private String description;
        private Integer totalModalities;
        private Integer totalStudents;
        private Integer individualModalities;
        private Integer groupModalities;
        private Double averageStudentsPerModality;
        private Double percentageOfTotal;
        private Boolean requiresDirector;
        private Integer modalitiesWithDirector;
        private Integer modalitiesWithoutDirector;
        private Map<String, Integer> distributionByStatus;
        private String trend; // INCREASING, DECREASING, STABLE
    }

    /**
     * DTO para comparación histórica por periodo
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PeriodComparisonDTO {
        private Integer year;
        private Integer semester;
        private String periodLabel;
        private Map<String, Integer> modalitiesByType;
        private Map<String, Integer> studentsByType;
        private Integer totalModalitiesInPeriod;
        private Integer totalStudentsInPeriod;
    }

    /**
     * DTO para análisis de tendencias
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TrendsAnalysisDTO {
        private String overallTrend; // GROWING, DECLINING, STABLE
        private List<String> growingTypes;
        private List<String> decliningTypes;
        private List<String> stableTypes;
        private String mostImprovedType;
        private String mostDeclinedType;
        private Map<String, Double> growthRateByType; // Porcentaje de crecimiento
    }
}


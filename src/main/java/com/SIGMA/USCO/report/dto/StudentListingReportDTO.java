package com.SIGMA.USCO.report.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * DTO para el reporte de listado de estudiantes con filtros múltiples
 * Permite filtrar por estados, modalidades y semestres
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentListingReportDTO {

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
     * Listado de estudiantes
     */
    private List<StudentDetailDTO> students;

    /**
     * Estadísticas generales
     */
    private GeneralStatisticsDTO generalStatistics;

    /**
     * Distribuciones y análisis
     */
    private DistributionAnalysisDTO distributionAnalysis;

    /**
     * Estadísticas por modalidad
     */
    private List<ModalityStatisticsDTO> modalityStatistics;

    /**
     * Estadísticas por estado
     */
    private List<StatusStatisticsDTO> statusStatistics;

    /**
     * Estadísticas por semestre
     */
    private List<SemesterStatisticsDTO> semesterStatistics;

    /**
     * Metadata del reporte
     */
    private ReportMetadataDTO metadata;

    /**
     * Filtros aplicados al reporte
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AppliedFiltersDTO {
        private List<String> statuses;
        private List<String> modalityTypes;
        private List<String> semesters;
        private Integer year;
        private String filterDescription;
        private Boolean hasFilters;
    }

    /**
     * Resumen ejecutivo del reporte
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ExecutiveSummaryDTO {
        private Integer totalStudents;
        private Integer totalModalities;
        private Integer activeModalities;
        private Integer completedModalities;
        private Integer differentModalityTypes;
        private Integer differentStatuses;
        private Double averageProgress;
        private String mostCommonModalityType;
        private String mostCommonStatus;
        private Map<String, Integer> quickStats;
    }

    /**
     * Detalle completo de un estudiante
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StudentDetailDTO {
        // Información personal
        private Long studentId;
        private String studentCode;
        private String fullName;
        private String firstName;
        private String lastName;
        private String email;
        private String phone;

        // Información académica
        private String academicStatus;
        private Double cumulativeAverage;
        private Integer completedCredits;
        private Integer totalCredits;
        private Integer currentSemester;

        // Información de la modalidad
        private Long modalityId;
        private String modalityType;
        private String modalityName;
        private String modalityStatus;
        private String modalityStatusDescription;
        private LocalDateTime selectionDate;
        private LocalDateTime lastUpdateDate;
        private Integer daysInModality;

        // Director (si aplica)
        private String directorName;
        private String directorEmail;

        // Proyecto/Trabajo
        private String projectTitle;
        private String projectDescription;

        // Miembros (si es grupal)
        private Integer groupSize;
        private List<String> groupMembers;

        // Estadísticas adicionales
        private Double progressPercentage;
        private String timelineStatus; // ON_TIME, DELAYED, AT_RISK
        private Integer expectedCompletionDays;
        private String observations;
    }

    /**
     * Estadísticas generales del reporte
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GeneralStatisticsDTO {
        private Integer totalStudents;
        private Integer maleStudents;
        private Integer femaleStudents;
        private Integer otherGenderStudents;

        private Integer individualModalities;
        private Integer groupModalities;
        private Double individualVsGroupRatio;

        private Double averageCompletedCredits;
        private Double averageCumulativeGPA;
        private Double averageDaysInModality;
        private Double averageGroupSize;

        private Integer studentsWithDirector;
        private Integer studentsWithoutDirector;
        private Integer studentsOnTime;
        private Integer studentsDelayed;
        private Integer studentsAtRisk;

        private Map<String, Integer> studentsByAcademicStatus;
        private Map<String, Integer> studentsBySemester;
    }

    /**
     * Análisis de distribución
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DistributionAnalysisDTO {
        private Map<String, Integer> byModalityType;
        private Map<String, Double> byModalityTypePercentage;

        private Map<String, Integer> byStatus;
        private Map<String, Double> byStatusPercentage;

        private Map<String, Integer> bySemester;
        private Map<String, Double> bySemesterPercentage;

        private Map<String, Integer> byTimelineStatus;
        private Map<String, Double> byTimelineStatusPercentage;

        private Map<String, Integer> byGender;
        private Map<String, Double> byGenderPercentage;

        private List<TrendDataDTO> timelineTrends;
    }

    /**
     * Dato de tendencia
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TrendDataDTO {
        private String period;
        private Integer value;
        private String label;
        private Double percentage;
    }

    /**
     * Estadísticas por modalidad
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ModalityStatisticsDTO {
        private String modalityType;
        private String modalityName;
        private Integer totalStudents;
        private Integer activeStudents;
        private Integer completedStudents;
        private Double completionRate;
        private Double averageDaysToComplete;
        private Integer minDaysToComplete;
        private Integer maxDaysToComplete;
        private List<String> topDirectors;
        private Double averageGPA;
    }

    /**
     * Estadísticas por estado
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StatusStatisticsDTO {
        private String status;
        private String statusDescription;
        private Integer studentCount;
        private Double percentage;
        private Double averageDaysInStatus;
        private List<String> topModalities;
        private String trend; // INCREASING, STABLE, DECREASING
    }

    /**
     * Estadísticas por semestre
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SemesterStatisticsDTO {
        private String semester;
        private Integer year;
        private Integer studentCount;
        private Double percentage;
        private Integer modalitiesStarted;
        private Integer modalitiesCompleted;
        private Double completionRate;
        private Double averageGPA;
        private List<String> topModalityTypes;
    }
}


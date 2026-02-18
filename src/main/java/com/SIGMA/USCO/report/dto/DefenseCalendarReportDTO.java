package com.SIGMA.USCO.report.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * DTO para el reporte de calendario de sustentaciones y evaluaciones
 * Incluye sustentaciones próximas, historial, estadísticas y análisis
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DefenseCalendarReportDTO {

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
     * Sustentaciones próximas (calendario)
     */
    private List<UpcomingDefenseDTO> upcomingDefenses;

    /**
     * Sustentaciones en progreso
     */
    private List<InProgressDefenseDTO> inProgressDefenses;

    /**
     * Sustentaciones completadas (recientes)
     */
    private List<CompletedDefenseDTO> recentCompletedDefenses;

    /**
     * Estadísticas de sustentaciones
     */
    private DefenseStatisticsDTO statistics;

    /**
     * Análisis por mes
     */
    private List<MonthlyDefenseAnalysisDTO> monthlyAnalysis;

    /**
     * Análisis de jurados
     */
    private ExaminerAnalysisDTO examinerAnalysis;

    /**
     * Alertas y recordatorios
     */
    private List<DefenseAlertDTO> alerts;

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
        private String startDate;
        private String endDate;
        private List<String> modalityTypes;
        private List<String> status;
        private Boolean includeCompleted;
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
        private Integer totalScheduled;
        private Integer upcomingThisWeek;
        private Integer upcomingThisMonth;
        private Integer pendingScheduling;
        private Integer completedThisMonth;
        private Double averageSuccessRate;
        private Integer totalExaminersInvolved;
        private String nextDefenseDate;
        private Integer defensesToday;
        private Integer overduePending;
        private Map<String, Integer> quickStats;
    }

    /**
     * Sustentación próxima
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpcomingDefenseDTO {
        // Información básica
        private Long modalityId;
        private String modalityType;
        private String modalityTypeName;
        private LocalDateTime defenseDate;
        private String defenseTime;
        private String defenseLocation;
        private Integer daysUntilDefense;
        private String urgency; // URGENT, SOON, NORMAL

        // Estudiantes
        private List<StudentBasicInfoDTO> students;
        private String leaderName;
        private Integer studentCount;

        // Director
        private String directorName;
        private String directorEmail;
        private String directorPhone;

        // Jurados
        private List<ExaminerInfoDTO> examiners;
        private Boolean allExaminersConfirmed;
        private Integer confirmedExaminers;
        private Integer totalExaminers;

        // Estado de preparación
        private String preparationStatus; // READY, PENDING_CONFIRMATION, INCOMPLETE
        private List<String> pendingTasks;
        private Double readinessPercentage;

        // Información adicional
        private String projectTitle;
        private Integer estimatedDuration; // minutos
        private String modalityStatus;
        private LocalDateTime scheduledDate;
        private Boolean reminderSent;
    }

    /**
     * Información básica de estudiante
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StudentBasicInfoDTO {
        private Long studentId;
        private String studentCode;
        private String fullName;
        private String email;
        private String phone;
        private Boolean isLeader;
    }

    /**
     * Información de jurado
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ExaminerInfoDTO {
        private Long examinerId;
        private String fullName;
        private String email;
        private String examinerType; // PRESIDENT, MAIN, AUXILIARY
        private LocalDateTime assignmentDate;
        private Boolean confirmed;
        private String affiliation;
    }

    /**
     * Sustentación en progreso
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InProgressDefenseDTO {
        private Long modalityId;
        private String modalityType;
        private List<StudentBasicInfoDTO> students;
        private String directorName;
        private String currentStatus;
        private LocalDateTime statusDate;
        private Integer daysInCurrentStatus;
        private String nextAction;
        private LocalDateTime expectedDefenseDate;
        private Double progressPercentage;
        private List<String> completedSteps;
        private List<String> pendingSteps;
    }

    /**
     * Sustentación completada
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CompletedDefenseDTO {
        private Long modalityId;
        private String modalityType;
        private LocalDateTime defenseDate;
        private List<StudentBasicInfoDTO> students;
        private String directorName;
        private String result; // APPROVED, REJECTED, CORRECTIONS_REQUIRED
        private Double finalGrade;
        private String academicDistinction;
        private List<ExaminerInfoDTO> examiners;
        private String defenseLocation;
        private Integer daysAgo;
    }

    /**
     * Estadísticas de sustentaciones
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DefenseStatisticsDTO {
        // Totales generales
        private Integer totalScheduled;
        private Integer totalCompleted;
        private Integer totalPending;
        private Integer totalCancelled;

        // Por resultado
        private Integer approved;
        private Integer rejected;
        private Integer withCorrections;
        private Double approvalRate;

        // Tiempos promedio
        private Double averageDaysToDefense;
        private Double averageDefenseDuration;

        // Por tipo de modalidad
        private Map<String, Integer> byModalityType;
        private Map<String, Double> successRateByType;

        // Por mes
        private Map<String, Integer> defensesByMonth;

        // Distinción
        private Integer withMeritorious;
        private Integer withLaudeate;
        private Double distinctionRate;

        // Calificaciones
        private Double averageGrade;
        private Double highestGrade;
        private Double lowestGrade;

        // Tendencias
        private String trend; // INCREASING, STABLE, DECREASING
        private Double growthRate;
    }

    /**
     * Análisis mensual
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MonthlyDefenseAnalysisDTO {
        private String month;
        private Integer year;
        private String periodLabel;
        private Integer totalScheduled;
        private Integer completed;
        private Integer pending;
        private Integer approved;
        private Double successRate;
        private Double averageGrade;
        private Integer totalStudents;
        private List<String> peakDays;
    }

    /**
     * Análisis de jurados
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ExaminerAnalysisDTO {
        private Integer totalExaminers;
        private Integer activeExaminers;
        private List<TopExaminerDTO> topExaminers;
        private Map<String, Integer> examinersByType;
        private Double averageDefensesPerExaminer;
        private String mostActiveExaminer;
        private Integer mostActiveExaminerCount;
    }

    /**
     * Top jurado
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TopExaminerDTO {
        private String examinerName;
        private String examinerType;
        private Integer totalDefenses;
        private Integer upcomingDefenses;
        private Double averageGradeGiven;
        private String availability; // HIGH, MEDIUM, LOW
    }

    /**
     * Alerta de sustentación
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DefenseAlertDTO {
        private String alertType; // URGENT, WARNING, INFO
        private String title;
        private String description;
        private Long modalityId;
        private String studentName;
        private LocalDateTime defenseDate;
        private Integer priority; // 1-5
        private String actionRequired;
        private LocalDateTime alertDate;
    }
}


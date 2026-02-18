package com.SIGMA.USCO.report.enums;

/**
 * Enumeración de tipos de reportes disponibles en el sistema
 */
public enum ReportType {

    // RF-45: Reporte Global de Modalidades Activas
    GLOBAL_ACTIVE_MODALITIES("Reporte Global de Modalidades Activas", "RF-45"),

    // RF-46: Filtrado por Tipo de Modalidad
    FILTERED_MODALITIES("Reporte Filtrado de Modalidades", "RF-46"),

    // RF-48: Comparativa de Modalidades por Tipo de Grado
    MODALITY_TYPE_COMPARISON("Comparativa de Modalidades por Tipo", "RF-48"),

    // RF-49: Reportes por Director Asignado
    DIRECTOR_ASSIGNED_MODALITIES("Reporte de Modalidades por Director", "RF-49"),

    // Reportes de estudiantes
    STUDENTS_BY_MODALITY("Estudiantes por Modalidad", "RF-47"),
    STUDENTS_BY_SEMESTER("Estudiantes por Semestre Académico", "RF-50"),
    STUDENTS_IN_PROGRESS("Estudiantes Cursando Modalidad", "RF-51"),

    // Reportes de directores
    DIRECTORS_BY_MODALITY("Directores por Modalidad", "RF-52"),
    DIRECTORS_WORKLOAD("Carga de Trabajo de Directores", "RF-53"),

    // Reportes de modalidades
    MODALITIES_BY_PROGRAM("Modalidades por Programa", "RF-54"),
    MODALITIES_BY_STATUS("Modalidades por Estado", "RF-55"),
    MODALITY_COMPLETION_RATE("Tasa de Completitud de Modalidades", "RF-56"),

    // Reportes de evaluaciones
    DEFENSE_SCHEDULE("Cronograma de Sustentaciones", "RF-57"),
    EVALUATION_STATUS("Estado de Evaluaciones", "RF-58"),

    // Reportes estadísticos
    PROGRAM_STATISTICS("Estadísticas por Programa", "RF-59"),
    SEMESTER_STATISTICS("Estadísticas por Semestre", "RF-60"),
    COMPARATIVE_ANALYSIS("Análisis Comparativo", "RF-61");

    private final String displayName;
    private final String requirementCode;

    ReportType(String displayName, String requirementCode) {
        this.displayName = displayName;
        this.requirementCode = requirementCode;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getRequirementCode() {
        return requirementCode;
    }
}


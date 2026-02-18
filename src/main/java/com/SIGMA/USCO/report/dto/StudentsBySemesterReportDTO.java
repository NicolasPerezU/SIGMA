package com.SIGMA.USCO.report.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;
import java.util.Map;

/**
 * DTO para el Reporte de Estudiantes por Semestre
 * RF-47 - Reporte de Estudiantes por Semestre Académico
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class StudentsBySemesterReportDTO extends BaseReportDTO {

    private String academicPeriod;
    private Integer year;
    private Integer semester;
    private Integer totalStudents;
    private Map<String, Integer> studentsByModality;
    private List<StudentInfoDTO> students;
    private SemesterStatisticsDTO statistics;
}


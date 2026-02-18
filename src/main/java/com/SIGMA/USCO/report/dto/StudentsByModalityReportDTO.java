package com.SIGMA.USCO.report.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

/**
 * DTO para el Reporte de Estudiantes por Modalidad
 * RF-46 - Reporte de Estudiantes Asociados a una Modalidad
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class StudentsByModalityReportDTO extends BaseReportDTO {

    private String modalityType;
    private String modalityName;
    private Integer totalStudents;
    private List<StudentInfoDTO> students;
    private StudentStatisticsDTO statistics;
}


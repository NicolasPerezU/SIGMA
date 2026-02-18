package com.SIGMA.USCO.report.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO con información de carga de trabajo de un director
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DirectorWorkloadDTO {

    private Long directorId;
    private String directorName;
    private String directorEmail;
    private Integer activeProjects;
    private Integer completedProjects;
    private Integer totalProjects;
    private List<String> modalityTypes;
    private List<StudentInfoDTO> assignedStudents;
}


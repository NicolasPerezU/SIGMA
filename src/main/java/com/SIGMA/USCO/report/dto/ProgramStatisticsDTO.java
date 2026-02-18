package com.SIGMA.USCO.report.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProgramStatisticsDTO {


    private Long programId;

    private String programName;

    private String programCode;

    private Integer totalActiveModalities;

    private Integer totalActiveStudents;

    private Map<String, Long> modalityDistribution;

    private Map<String, Long> statusDistribution;


    private Double averageDaysInProcess;


    private String facultyName;

}


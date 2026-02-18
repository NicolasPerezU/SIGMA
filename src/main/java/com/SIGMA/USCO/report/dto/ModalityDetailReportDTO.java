package com.SIGMA.USCO.report.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModalityDetailReportDTO {


    private Long studentModalityId;


    private String modalityName;


    private String modalityType;


    private String academicProgram;


    private String currentStatus;


    private String statusDescription;


    private List<StudentInfoDTO> students;


    private DirectorInfoDTO director;


    private LocalDateTime startDate;


    private LocalDateTime lastUpdate;


    private Long daysSinceStart;


    private Long daysInCurrentStatus;


    private Boolean hasPendingActions;


    private String observations;

}


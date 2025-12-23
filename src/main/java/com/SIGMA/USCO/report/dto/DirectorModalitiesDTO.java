package com.SIGMA.USCO.report.dto;

import com.SIGMA.USCO.Modalities.Entity.enums.AcademicDistinction;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DirectorModalitiesDTO {

    private String directorName;
    private String studentName;
    private String modalityName;
    private String modalityStatus;
    private AcademicDistinction academicDistinction;
    private String studentEmail;
    private LocalDateTime startDate;

}

package com.SIGMA.USCO.Modalities.dto;

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
public class ScheduleDefenseDTO {

    private LocalDateTime defenseDate;
    private String defenseLocation;
    private boolean approved;
    private AcademicDistinction academicDistinction;
    private String observations;

}

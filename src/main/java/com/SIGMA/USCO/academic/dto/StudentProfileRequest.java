package com.SIGMA.USCO.academic.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentProfileRequest {

    private Long academicProgramId;
    private Long facultyId;
    private Long approvedCredits;
    private Double gpa;
    private Long semester;
    private String studentCode;
}

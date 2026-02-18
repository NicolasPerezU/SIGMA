package com.SIGMA.USCO.report.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentInfoDTO {


    private Long studentId;


    private String fullName;


    private String studentCode;


    private String email;


    private Long semester;


    private Double gpa;


    private Boolean isLeader;

}


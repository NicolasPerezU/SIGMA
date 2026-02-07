package com.SIGMA.USCO.Users.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentResponse {


    private String name;
    private String lastname;
    private String email;

    private Long approvedCredits;
    private Double gpa;
    private Long semester;
    private String studentCode;

    private String faculty;
    private String academicProgram;



}

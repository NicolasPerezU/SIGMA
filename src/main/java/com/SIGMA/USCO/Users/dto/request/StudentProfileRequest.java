package com.SIGMA.USCO.Users.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentProfileRequest {

    private Long approvedCredits;
    private Double gpa;
    private Long semester;
    private String studentCode;
}

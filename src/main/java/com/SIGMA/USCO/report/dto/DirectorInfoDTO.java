package com.SIGMA.USCO.report.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DirectorInfoDTO {


    private Long directorId;


    private String fullName;


    private String email;


    private Integer activeProjectsCount;

}


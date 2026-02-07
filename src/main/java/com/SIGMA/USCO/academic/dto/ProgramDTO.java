package com.SIGMA.USCO.academic.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProgramDTO {

    private Long id;
    private String name;
    private String code;
    private Long totalCredits;
    private String description;
    private Long facultyId;
    private boolean active;

}

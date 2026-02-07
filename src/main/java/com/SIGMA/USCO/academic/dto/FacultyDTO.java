package com.SIGMA.USCO.academic.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FacultyDTO {

    @NotBlank
    private String name;
    @NotBlank
    private String code;
    private String description;

    //response

    private Long id;
    private boolean active;
    private List<ProgramDTO> academicPrograms;
}

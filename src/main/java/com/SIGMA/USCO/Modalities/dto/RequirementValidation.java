package com.SIGMA.USCO.Modalities.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RequirementValidation {

    private String requirementName;

    private String expectedValue;

    private String studentValue;

    private boolean fulfilled;
}

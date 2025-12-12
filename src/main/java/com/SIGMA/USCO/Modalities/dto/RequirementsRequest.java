package com.SIGMA.USCO.Modalities.dto;

import com.SIGMA.USCO.Modalities.Entity.RuleType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RequirementsRequest {

    private String requirementName;

    private String description;

    private RuleType ruleType;

    private String expectedValue;

}

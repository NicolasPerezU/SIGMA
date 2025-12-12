package com.SIGMA.USCO.Modalities.dto;

import com.SIGMA.USCO.Modalities.Entity.ModalityType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModalityRequest {

    private String name;

    private String description;

    private Long creditsRequired;

    private ModalityType type;

}

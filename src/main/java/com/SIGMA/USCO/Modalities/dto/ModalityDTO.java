package com.SIGMA.USCO.Modalities.dto;


import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ModalityDTO {

    private Long id;
    private String name;
    private String description;
    private Long creditsRequired;

}

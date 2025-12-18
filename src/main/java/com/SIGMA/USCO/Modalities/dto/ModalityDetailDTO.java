package com.SIGMA.USCO.Modalities.dto;

import com.SIGMA.USCO.Modalities.Entity.ModalityType;
import com.SIGMA.USCO.documents.dto.view.DocumentDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ModalityDetailDTO {

    private Long id;
    private String name;
    private String description;
    private Long creditsRequired;
    private ModalityType type;

    private List<RequirementDTO> requirements;
    private List<DocumentDTO> documents;
}

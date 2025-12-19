package com.SIGMA.USCO.Modalities.dto;


import com.SIGMA.USCO.Modalities.Entity.enums.ModalityStatus;
import com.SIGMA.USCO.Modalities.Entity.enums.ModalityType;
import com.SIGMA.USCO.documents.dto.RequiredDocumentDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ModalityDTO {

    private Long id;
    private String name;
    private String description;
    private Long creditsRequired;
    private ModalityStatus status;
    private ModalityType type;
    private List<RequirementDTO> requirements;
    private List<RequiredDocumentDTO> documents;

}

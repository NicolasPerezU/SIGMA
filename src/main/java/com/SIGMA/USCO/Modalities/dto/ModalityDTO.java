package com.SIGMA.USCO.Modalities.dto;


import com.SIGMA.USCO.Modalities.Entity.enums.ModalityStatus;
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
    private Long facultyId;
    private String facultyName;
    private String name;
    private String description;
    private ModalityStatus status;
    private Double requiredCredits;
    private List<RequirementDTO> requirements;
    private List<RequiredDocumentDTO> documents;

}

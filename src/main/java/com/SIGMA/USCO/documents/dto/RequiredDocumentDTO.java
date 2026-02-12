package com.SIGMA.USCO.documents.dto;

import com.SIGMA.USCO.documents.entity.DocumentType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RequiredDocumentDTO {


    private Long modalityId;

    private Long id;

    private String documentName;
    private String allowedFormat;
    private Integer maxFileSizeMB;
    private DocumentType documentType;
    private String description;
    private boolean active;

}

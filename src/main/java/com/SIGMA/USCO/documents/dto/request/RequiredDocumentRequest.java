package com.SIGMA.USCO.documents.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequiredDocumentRequest {

    private Long modalityId;
    private String documentName;
    private String allowedFormat;
    private Integer maxFileSizeMB;
    private boolean mandatory;
    private String description;
    private boolean active;
}

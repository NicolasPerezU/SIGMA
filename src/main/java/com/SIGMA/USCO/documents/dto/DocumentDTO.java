package com.SIGMA.USCO.documents.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DocumentDTO {

    private Long id;
    private String documentName;
    private String allowedFormat;
    private Integer maxFileSizeMB;
    private boolean mandatory;
    private String description;

}

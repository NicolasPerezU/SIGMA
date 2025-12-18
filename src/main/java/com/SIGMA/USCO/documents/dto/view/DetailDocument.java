package com.SIGMA.USCO.documents.dto.view;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DetailDocument {

    private Long studentDocumentId;

    private String documentName;
    private boolean mandatory;

    private String status;
    private String statusDescription;

    private String notes;
    private LocalDateTime lastUpdate;

    private boolean uploaded;

}

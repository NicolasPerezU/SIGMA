package com.SIGMA.USCO.Modalities.dto;

import com.SIGMA.USCO.documents.entity.DocumentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentReviewDTO {

    private DocumentStatus status;
    private String notes;

}

package com.SIGMA.USCO.documents.dto.view;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DocumentStatusDTO {

    private String status;
    private String description;
    private LocalDateTime changeDate;
    private String responsible;
    private String observations;
}

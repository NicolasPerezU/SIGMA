package com.SIGMA.USCO.report.dto;

import com.SIGMA.USCO.Modalities.Entity.enums.ModalityProcessStatus;
import com.SIGMA.USCO.Modalities.Entity.enums.ModalityType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SemesterModalitiesFilterDTO {

    private LocalDateTime startDate;
    private ModalityType modalityType;
    private ModalityProcessStatus status;
}

package com.SIGMA.USCO.Modalities.dto;

import jakarta.persistence.Access;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ModalityStatusHistoryDTO {

    private String status;
    private String description;
    private LocalDateTime changeDate;
    private String responsible;
    private String observations;

}

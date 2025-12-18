package com.SIGMA.USCO.Modalities.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CurrentStudentModalityDTO {

    private Long studentModalityId;

    private String modalityName;

    private String currentStatus;

    private String currentStatusDescription;

    private LocalDateTime lastUpdatedAt;

    private List<ModalityStatusHistoryDTO> history;

}

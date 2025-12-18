package com.SIGMA.USCO.Modalities.dto;

import com.SIGMA.USCO.documents.dto.view.DetailDocument;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModalityCompleteDetail {

    private Long studentModalityId;

    private String studentName;
    private String studentEmail;

    private String modalityName;

    private String currentStatus;
    private String currentStatusDescription;
    private LocalDateTime lastUpdatedAt;

    private List<ModalityStatusHistoryDTO> history;
    private List<DetailDocument> documents;

}

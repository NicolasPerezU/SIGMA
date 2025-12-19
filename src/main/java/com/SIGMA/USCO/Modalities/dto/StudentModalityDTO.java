package com.SIGMA.USCO.Modalities.dto;

import com.SIGMA.USCO.documents.dto.DetailDocumentDTO;
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
public class StudentModalityDTO {

    private Long studentModalityId;
    private String modalityName;
    private String currentStatus;
    private String currentStatusDescription;
    private List<ModalityStatusHistoryDTO> history;
    private LocalDateTime lastUpdatedAt;
    private String studentName;
    private String studentEmail;
    private List<DetailDocumentDTO> documents;

}

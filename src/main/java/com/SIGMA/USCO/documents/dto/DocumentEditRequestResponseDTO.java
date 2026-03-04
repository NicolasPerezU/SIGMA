package com.SIGMA.USCO.documents.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO de salida con la información completa de una solicitud de edición de documento
 * y el estado de votación de los jurados.
 */
@Data
@Builder
public class DocumentEditRequestResponseDTO {

    private Long editRequestId;
    private Long studentDocumentId;
    private String documentName;
    private String documentType;

    // Quien solicita
    private String requesterName;
    private String requesterEmail;
    private String reason;

    // Estado general de la solicitud
    private String status;
    private String statusDescription;

    // Fechas
    private LocalDateTime createdAt;
    private LocalDateTime resolvedAt;

    // Votos individuales de los jurados
    private List<EditVoteDTO> votes;

    // Resultado final (si ya está resuelto)
    private String finalResolutionNotes;

    @Data
    @Builder
    public static class EditVoteDTO {
        private String examinerName;
        private String examinerEmail;
        private String examinerType;
        private String decision;        // APPROVED / REJECTED
        private String notes;
        private Boolean isTiebreakerVote;
        private LocalDateTime votedAt;
    }
}


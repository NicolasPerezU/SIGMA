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

    // Información del estudiante
    private Long studentId;
    private String studentName;
    private String studentLastName;
    private String studentEmail;
    private String studentCode;
    private Long approvedCredits;
    private Double gpa;
    private Long semester;

    // Información institucional
    private String facultyName;
    private String academicProgramName;

    // Información de la modalidad
    private Long studentModalityId;
    private String modalityName;
    private String modalityDescription;
    private Long creditsRequired;
    private String modalityType;

    // Estado actual
    private String currentStatus;
    private String currentStatusDescription;
    private LocalDateTime selectionDate;
    private LocalDateTime lastUpdatedAt;

    // Director de proyecto
    private Long projectDirectorId;
    private String projectDirectorName;
    private String projectDirectorEmail;

    // Sustentación/Defensa
    private LocalDateTime defenseDate;
    private String defenseLocation;
    private String defenseProposedByProjectDirector;

    // Distinción académica
    private String academicDistinction;

    // Correcciones
    private LocalDateTime correctionRequestDate;
    private LocalDateTime correctionDeadline;
    private Boolean correctionReminderSent;
    private Long daysRemainingForCorrection;

    // Documentos
    private List<DetailDocumentDTO> documents;
    private Integer totalDocuments;
    private Integer approvedDocuments;
    private Integer pendingDocuments;
    private Integer rejectedDocuments;

    // Historial de estados
    private List<ModalityStatusHistoryDTO> history;

    // Acciones disponibles
    private Boolean canUploadDocuments;
    private Boolean canRequestCancellation;
    private Boolean canSubmitCorrections;
    private Boolean hasDefenseScheduled;
    private Boolean requiresAction;

}

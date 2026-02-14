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


    private Long studentId;
    private String studentName;
    private String studentLastName;
    private String studentEmail;
    private String studentCode;
    private Long approvedCredits;
    private Double gpa;
    private Long semester;


    private String facultyName;
    private String academicProgramName;


    private Long studentModalityId;
    private Long modalityId;
    private String modalityName;
    private String modalityDescription;
    private Long creditsRequired;
    private String modalityType;

    // Lista de miembros (para modalidades grupales)
    private List<ModalityMemberDTO> members;


    private String currentStatus;
    private String currentStatusDescription;
    private LocalDateTime selectionDate;
    private LocalDateTime lastUpdatedAt;


    private Long projectDirectorId;
    private String projectDirectorName;
    private String projectDirectorEmail;


    private LocalDateTime defenseDate;
    private String defenseLocation;
    private String defenseProposedByProjectDirector;


    private String academicDistinction;


    private LocalDateTime correctionRequestDate;
    private LocalDateTime correctionDeadline;
    private Boolean correctionReminderSent;
    private Long daysRemainingForCorrection;


    private List<DetailDocumentDTO> documents;
    private Integer totalDocuments;
    private Integer approvedDocuments;
    private Integer pendingDocuments;
    private Integer rejectedDocuments;


    private List<ModalityStatusHistoryDTO> history;


    private Boolean canUploadDocuments;
    private Boolean canRequestCancellation;
    private Boolean canSubmitCorrections;
    private Boolean hasDefenseScheduled;
    private Boolean requiresAction;

}

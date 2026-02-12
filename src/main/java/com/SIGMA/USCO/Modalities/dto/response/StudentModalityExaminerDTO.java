package com.SIGMA.USCO.Modalities.dto.response;

import com.SIGMA.USCO.Modalities.dto.ModalityStatusHistoryDTO;
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
public class StudentModalityExaminerDTO {

    // Información del estudiante
    private Long studentId;
    private String studentName;
    private String studentLastName;
    private String studentEmail;
    private String studentCode;
    private Long approvedCredits;
    private Double gpa;
    private Long semester;

    // Información académica
    private String facultyName;
    private String academicProgramName;

    // Información de la modalidad
    private Long studentModalityId;
    private String modalityName;
    private String modalityDescription;
    private Long creditsRequired;

    // Estado actual
    private String currentStatus;
    private String currentStatusDescription;
    private LocalDateTime selectionDate;
    private LocalDateTime lastUpdatedAt;

    // Director del proyecto
    private Long projectDirectorId;
    private String projectDirectorName;
    private String projectDirectorEmail;

    // Información de la defensa
    private LocalDateTime defenseDate;
    private String defenseLocation;

    // Información de examinadores
    private List<ExaminerInfo> examiners;

    // Evaluación del examinador actual
    private ExaminerEvaluationInfo myEvaluation;

    // Distinción académica y calificación final
    private String academicDistinction;
    private Double finalGrade;

    // Documentos
    private List<DetailDocumentDTO> documents;
    private Integer totalDocuments;
    private Integer approvedDocuments;
    private Integer pendingDocuments;
    private Integer rejectedDocuments;

    // Historial
    private List<ModalityStatusHistoryDTO> history;

    // Permisos y acciones
    private Boolean canEvaluate;
    private Boolean hasEvaluated;
    private Boolean requiresAction;
    private Boolean defenseCompleted;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ExaminerInfo {
        private Long examinerId;
        private String examinerName;
        private String examinerEmail;
        private String examinerType;
        private LocalDateTime assignmentDate;
        private Boolean hasEvaluated;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ExaminerEvaluationInfo {
        private Long evaluationId;
        private Double grade;
        private String decision;
        private String observations;
        private LocalDateTime evaluationDate;
        private Boolean isFinalDecision;
    }
}


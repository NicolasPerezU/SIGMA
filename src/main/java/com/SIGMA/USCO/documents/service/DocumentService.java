package com.SIGMA.USCO.documents.service;

import com.SIGMA.USCO.Modalities.Repository.DegreeModalityRepository;
import com.SIGMA.USCO.Users.Entity.User;
import com.SIGMA.USCO.Users.repository.UserRepository;

import com.SIGMA.USCO.documents.dto.StatusHistoryDTO;
import com.SIGMA.USCO.documents.dto.RequiredDocumentDTO;
import com.SIGMA.USCO.documents.entity.DocumentStatus;
import com.SIGMA.USCO.documents.entity.RequiredDocument;
import com.SIGMA.USCO.documents.entity.StudentDocument;
import com.SIGMA.USCO.documents.entity.StudentDocumentStatusHistory;
import com.SIGMA.USCO.documents.repository.RequiredDocumentRepository;
import com.SIGMA.USCO.documents.repository.StudentDocumentRepository;
import com.SIGMA.USCO.documents.repository.StudentDocumentStatusHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DocumentService {

    private final RequiredDocumentRepository requiredDocumentRepository;
    private final DegreeModalityRepository degreeModalityRepository;
    private final UserRepository userRepository;
    private final StudentDocumentRepository studentDocumentRepository;
    private final StudentDocumentStatusHistoryRepository documentHistoryRepository;


    public ResponseEntity<?> createRequiredDocument(RequiredDocumentDTO request) {

        var modality = degreeModalityRepository.findById(request.getModalityId())
                .orElseThrow(() -> new RuntimeException(
                        "La modalidad con ID " + request.getModalityId() + " no existe.")
                );

        RequiredDocument document = RequiredDocument.builder()
                .modality(modality)
                .documentName(request.getDocumentName())
                .allowedFormat(request.getAllowedFormat())
                .maxFileSizeMB(request.getMaxFileSizeMB())
                .isMandatory(request.isMandatory())
                .description(request.getDescription())
                .active(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        requiredDocumentRepository.save(document);

        return ResponseEntity.ok("Documento obligatorio registrado correctamente.");
    }
    public ResponseEntity<?> updateRequiredDocument(Long documentId, RequiredDocumentDTO request) {

        if ( !requiredDocumentRepository.existsById(documentId) ) {
            return ResponseEntity.badRequest().body("El documento obligatorio con ID " + documentId + " no existe.");
        }
        var document = requiredDocumentRepository.findById(documentId).get();

        document.setDocumentName(request.getDocumentName());
        document.setDescription(request.getDescription());
        document.setAllowedFormat(request.getAllowedFormat());
        document.setMaxFileSizeMB(request.getMaxFileSizeMB());
        document.setMandatory(request.isMandatory());
        document.setActive(request.isActive());
        document.setUpdatedAt(LocalDateTime.now());

        requiredDocumentRepository.save(document);

        return ResponseEntity.ok("Documento obligatorio actualizado correctamente.");
    }

    private String describeDocumentStatus(DocumentStatus status) {

        return switch (status) {

            case PENDING ->
                    "Documento cargado y pendiente de revisión.";

            case ACCEPTED_FOR_SECRETARY_REVIEW ->
                    "Documento aprobado por la Secretaría.";

            case REJECTED_FOR_SECRETARY_REVIEW ->
                    "Documento rechazado por la Secretaría.";

            case CORRECTIONS_REQUESTED_BY_SECRETARY ->
                    "La Secretaría solicitó correcciones.";

            case ACCEPTED_FOR_COUNCIL_REVIEW ->
                    "Documento aprobado para revisión del Consejo.";

            case REJECTED_FOR_COUNCIL_REVIEW ->
                    "Documento rechazado por el Consejo.";

            case CORRECTIONS_REQUESTED_BY_COUNCIL ->
                    "El Consejo solicitó correcciones.";

            default ->
                    "Estado del documento no definido.";
        };
    }

    public ResponseEntity<?> getDocumentHistory(Long studentDocumentId) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        User student = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        StudentDocument document = studentDocumentRepository.findById(studentDocumentId)
                .orElseThrow(() -> new RuntimeException("Document not found"));

        if (!document.getStudentModality().getStudent().getId().equals(student.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No authorized access to document history.");
        }

        List<StudentDocumentStatusHistory> history =
                documentHistoryRepository
                        .findByStudentDocumentIdOrderByChangeDateAsc(studentDocumentId);

        List<StatusHistoryDTO> response = history.stream()
                .map(h -> StatusHistoryDTO.builder()
                        .status(h.getStatus().name())
                        .description(describeDocumentStatus(h.getStatus()))
                        .changeDate(h.getChangeDate())
                        .responsible(
                                h.getResponsible() != null
                                        ? h.getResponsible().getEmail()
                                        : "Sistema"
                        )
                        .observations(h.getObservations())
                        .build()
                )
                .toList();

        return ResponseEntity.ok(response);
    }

}

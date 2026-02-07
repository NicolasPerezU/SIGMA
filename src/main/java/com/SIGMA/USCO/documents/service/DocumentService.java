package com.SIGMA.USCO.documents.service;

import com.SIGMA.USCO.Modalities.Entity.StudentModality;
import com.SIGMA.USCO.Modalities.Repository.DegreeModalityRepository;
import com.SIGMA.USCO.Modalities.Repository.StudentModalityRepository;
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
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DocumentService {

    private final RequiredDocumentRepository requiredDocumentRepository;
    private final DegreeModalityRepository degreeModalityRepository;
    private final UserRepository userRepository;
    private final StudentDocumentRepository studentDocumentRepository;
    private final StudentDocumentStatusHistoryRepository documentHistoryRepository;
    private final StudentModalityRepository studentModalityRepository;

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Transactional
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

    @Transactional
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


    public ResponseEntity<?> deleteRequiredDocument(Long documentId) {

        RequiredDocument document = requiredDocumentRepository.findById(documentId)
                .orElseThrow(() ->
                        new RuntimeException("El documento obligatorio con ID " + documentId + " no existe.")
                );

        if (!document.isActive()) {
            return ResponseEntity.badRequest()
                    .body("El documento obligatorio ya se encuentra inactivo.");
        }

        document.setActive(false);
        document.setUpdatedAt(LocalDateTime.now());

        requiredDocumentRepository.save(document);

        return ResponseEntity.ok("Documento obligatorio desactivado correctamente.");
    }

    public ResponseEntity<List<RequiredDocumentDTO>> getRequiredDocumentsByModality(Long modalityId) {

        if (!degreeModalityRepository.existsById(modalityId)) {
            return ResponseEntity.badRequest().build();
        }

        List<RequiredDocumentDTO> documents =
                requiredDocumentRepository
                        .findByModalityIdAndActive(modalityId, true)
                        .stream()
                        .map(doc -> RequiredDocumentDTO.builder()
                                .id(doc.getId())
                                .modalityId( doc.getModality().getId())
                                .documentName(doc.getDocumentName())
                                .description(doc.getDescription())
                                .allowedFormat(doc.getAllowedFormat())
                                .maxFileSizeMB(doc.getMaxFileSizeMB())
                                .mandatory(doc.isMandatory())
                                .active(doc.isActive())
                                .build())
                        .toList();

        return ResponseEntity.ok(documents);
    }

    public ResponseEntity<List<RequiredDocumentDTO>>
    getRequiredDocumentsByModalityAndStatus(Long modalityId, boolean active) {

        if (!degreeModalityRepository.existsById(modalityId)) {
            return ResponseEntity.badRequest().build();
        }

        List<RequiredDocumentDTO> documents =
                requiredDocumentRepository
                        .findByModalityIdAndActive(modalityId, active)
                        .stream()
                        .map(doc -> RequiredDocumentDTO.builder()
                                .id(doc.getId())
                                .documentName(doc.getDocumentName())
                                .description(doc.getDescription())
                                .allowedFormat(doc.getAllowedFormat())
                                .maxFileSizeMB(doc.getMaxFileSizeMB())
                                .mandatory(doc.isMandatory())
                                .active(doc.isActive())
                                .build())
                        .toList();

        return ResponseEntity.ok(documents);
    }





    private String describeDocumentStatus(DocumentStatus status) {

        return switch (status) {

            case PENDING ->
                    "Documento cargado y pendiente de revisión.";

            case ACCEPTED_FOR_PROGRAM_HEAD_REVIEW ->
                    "Documento aprobado por la jefatura del programa.";

            case REJECTED_FOR_PROGRAM_HEAD_REVIEW ->
                    "Documento rechazado por la jefatura del programa.";

            case CORRECTIONS_REQUESTED_BY_PROGRAM_HEAD ->
                    "La jefatura del programa solicitó correcciones.";

            case ACCEPTED_FOR_PROGRAM_CURRICULUM_COMMITTEE_REVIEW ->
                    "Documento aprobado por el Comité Curricular del programa.";

            case REJECTED_FOR_PROGRAM_CURRICULUM_COMMITTEE_REVIEW ->
                    "Documento rechazado por el Comité Curricular del programa.";

            case CORRECTIONS_REQUESTED_BY_PROGRAM_CURRICULUM_COMMITTEE ->
                    "El Comité Curricular del programa solicitó correcciones.";

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

    public StudentDocument getDocumentCancellation(Long studentModalityId) {

        return studentDocumentRepository.findByStudentModalityIdAndDocumentConfig_DocumentName(studentModalityId, "Justificación de cancelación de modalidad de grado")
                .orElseThrow(() -> new RuntimeException("Documento de justificación no encontrado"));
    }

    public void uploadCancellationDocument(Long studentModalityId, MultipartFile file) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        StudentModality studentModality = studentModalityRepository.findById(studentModalityId)
                .orElseThrow(() -> new RuntimeException("Modalidad no encontrada"));

        if (!studentModality.getStudent().getEmail().equals(email)) {
            throw new RuntimeException("No autorizado");
        }

        RequiredDocument cancellationDocumentConfig = requiredDocumentRepository.findByModalityIdAndActiveTrue(studentModality.getProgramDegreeModality().getDegreeModality().getId())
                        .stream().filter(doc ->
                                doc.getDocumentName().equals("Justificación de cancelación de modalidad de grado"))
                        .findFirst()
                        .orElseThrow(() ->
                                new RuntimeException("Configuración del documento no encontrada")
                        );

        validateFile(file, cancellationDocumentConfig);

        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path destination = Paths.get(uploadDir, fileName);

        try {
            Files.createDirectories(destination.getParent());
            Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("Error guardando el archivo");
        }

        StudentDocument studentDocument = StudentDocument.builder()
                .studentModality(studentModality)
                .documentConfig(cancellationDocumentConfig)
                .fileName(fileName)
                .filePath(destination.toString())
                .status(DocumentStatus.PENDING)
                .uploadDate(LocalDateTime.now())
                .build();

        studentDocumentRepository.save(studentDocument);
    }

    private void validateFile(MultipartFile file, RequiredDocument config) {

        if (file.isEmpty()) {
            throw new RuntimeException("Archivo vacío");
        }

        String extension = FilenameUtils.getExtension(file.getOriginalFilename())
                .toUpperCase();

        List<String> allowed =
                Arrays.stream(config.getAllowedFormat().split(","))
                        .map(String::trim)
                        .map(String::toUpperCase)
                        .toList();

        if (!allowed.contains(extension)) {
            throw new RuntimeException("Formato no permitido");
        }

        long maxSizeBytes = config.getMaxFileSizeMB() * 1024L * 1024L;

        if (file.getSize() > maxSizeBytes) {
            throw new RuntimeException("Archivo supera el tamaño permitido");
        }
    }

}

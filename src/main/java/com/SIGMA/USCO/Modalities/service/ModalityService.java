package com.SIGMA.USCO.Modalities.service;

import com.SIGMA.USCO.Modalities.Entity.*;
import com.SIGMA.USCO.Modalities.Entity.enums.ModalityProcessStatus;
import com.SIGMA.USCO.Modalities.Entity.enums.ModalityStatus;
import com.SIGMA.USCO.Modalities.Entity.enums.ModalityType;
import com.SIGMA.USCO.Modalities.Entity.enums.RuleType;
import com.SIGMA.USCO.Modalities.Repository.DegreeModalityRepository;
import com.SIGMA.USCO.Modalities.Repository.ModalityProcessStatusHistoryRepository;
import com.SIGMA.USCO.Modalities.Repository.ModalityRequirementsRepository;
import com.SIGMA.USCO.Modalities.Repository.StudentModalityRepository;
import com.SIGMA.USCO.Modalities.dto.*;

import com.SIGMA.USCO.Users.Entity.StudentProfile;
import com.SIGMA.USCO.Users.Entity.User;
import com.SIGMA.USCO.Users.repository.StudentProfileRepository;
import com.SIGMA.USCO.Users.repository.UserRepository;
import com.SIGMA.USCO.documents.dto.DetailDocumentDTO;
import com.SIGMA.USCO.documents.dto.RequiredDocumentDTO;
import com.SIGMA.USCO.documents.entity.*;
import com.SIGMA.USCO.documents.repository.RequiredDocumentRepository;
import com.SIGMA.USCO.documents.repository.StudentDocumentRepository;
import com.SIGMA.USCO.documents.repository.StudentDocumentStatusHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ModalityService {

    private final DegreeModalityRepository degreeModalityRepository;
    private final ModalityRequirementsRepository modalityRequirementsRepository;
    private final RequiredDocumentRepository requiredDocumentRepository;
    private final UserRepository userRepository;
    private final StudentProfileRepository studentProfileRepository;
    private final StudentModalityRepository studentModalityRepository;
    private final StudentDocumentRepository studentDocumentRepository;
    private final ModalityProcessStatusHistoryRepository historyRepository;
    private final StudentDocumentStatusHistoryRepository documentHistoryRepository;


    @Value("${file.upload-dir}")
    private String uploadDir;


    public ResponseEntity<?> createModality(ModalityDTO request) {

        if (degreeModalityRepository.existsByNameIgnoreCase(request.getName())) {
            return ResponseEntity.badRequest().body("La modalidad con el nombre " + request.getName() + " ya existe.");
        }
        ModalityType type = ModalityType.valueOf(request.getType().toString());

        DegreeModality newModality = DegreeModality.builder()
                .name(request.getName())
                .description(request.getDescription())
                .creditsRequired(request.getCreditsRequired())
                .type(type)
                .status(ModalityStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        return ResponseEntity.ok(degreeModalityRepository.save(newModality));
    }
    public ResponseEntity<?> updateModality(Long modalityId, ModalityDTO request) {

        if (!degreeModalityRepository.existsById(modalityId)) {
            return ResponseEntity.badRequest().body("La modalidad con ID " + modalityId + " no existe.");
        }

        DegreeModality modality = degreeModalityRepository.findById(modalityId).orElseThrow();

        modality.setName(request.getName());
        modality.setStatus(request.getStatus());
        modality.setDescription(request.getDescription());
        modality.setCreditsRequired(request.getCreditsRequired());
        modality.setType(ModalityType.valueOf(request.getType().toString()));
        modality.setUpdatedAt(LocalDateTime.now());

        degreeModalityRepository.save(modality);

        return ResponseEntity.ok(modality);
    }
    public ResponseEntity<?> createModalityRequirements(Long modalityId, List<RequirementDTO> requirements) {

        if (!degreeModalityRepository.existsById(modalityId)) {
            return ResponseEntity.badRequest().body("La modalidad con ID " + modalityId + " no existe.");
        }
        DegreeModality modality = degreeModalityRepository.findById(modalityId).orElseThrow();


        for (RequirementDTO requirementRequest : requirements) {
            ModalityRequirements requirement = ModalityRequirements.builder()
                    .modality(modality)
                    .requirementName(requirementRequest.getRequirementName())
                    .description(requirementRequest.getDescription())
                    .ruleType(requirementRequest.getRuleType())
                    .expectedValue(requirementRequest.getExpectedValue())
                    .active(true)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();

            modalityRequirementsRepository.save(requirement);
        }

        return ResponseEntity.ok("Requisitos creados exitosamente");
    }
    public ResponseEntity<?> updateModalityRequirements(Long modalityId, List<RequirementDTO> requirements) {

        if (!degreeModalityRepository.existsById(modalityId)) {
            return ResponseEntity.badRequest()
                    .body("La modalidad con ID " + modalityId + " no existe.");
        }

        DegreeModality modality = degreeModalityRepository
                .findById(modalityId)
                .orElseThrow();

        List<ModalityRequirements> existingRequirements =
                modalityRequirementsRepository.findByModalityId(modalityId);

        Map<Long, ModalityRequirements> existingMap = existingRequirements.stream()
                .collect(Collectors.toMap(ModalityRequirements::getId, r -> r));

        List<ModalityRequirements> toSave = new ArrayList<>();

        for (RequirementDTO dto : requirements) {

            // NUEVO
            if (dto.getId() == null) {
                ModalityRequirements newReq = ModalityRequirements.builder()
                        .modality(modality)
                        .requirementName(dto.getRequirementName())
                        .description(dto.getDescription())
                        .ruleType(dto.getRuleType())
                        .expectedValue(dto.getExpectedValue())
                        .active(true)
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build();

                toSave.add(newReq);
                continue;
            }

            // UPDATE
            ModalityRequirements existing = existingMap.get(dto.getId());

            if (existing == null) {
                throw new RuntimeException(
                        "El requisito con ID " + dto.getId() + " no pertenece a esta modalidad"
                );
            }

            existing.setRequirementName(dto.getRequirementName());
            existing.setDescription(dto.getDescription());
            existing.setRuleType(dto.getRuleType());
            existing.setExpectedValue(dto.getExpectedValue());
            existing.setActive(dto.isActive());
            existing.setUpdatedAt(LocalDateTime.now());

            toSave.add(existing);
        }

        modalityRequirementsRepository.saveAll(toSave);

        return ResponseEntity.ok("Requisitos de la modalidad actualizados correctamente");
    }
    public ResponseEntity<List<ModalityDTO>> getAllModalities() {
        List<ModalityDTO> modalities = degreeModalityRepository.findByStatus(ModalityStatus.ACTIVE)
                .stream()
                .map(mod -> ModalityDTO.builder()
                        .id(mod.getId())
                        .name(mod.getName())
                        .description(mod.getDescription())
                        .creditsRequired(mod.getCreditsRequired())
                        .build())
                .toList();

        return ResponseEntity.ok(modalities);
    }
    public ResponseEntity<?> getModalityDetail(Long modalityId) {

        if (!degreeModalityRepository.existsById(modalityId)) {
            return ResponseEntity.badRequest().body("La modalidad con ID " + modalityId + " no existe.");
        }

        var requirements = modalityRequirementsRepository.findByModalityIdAndActiveTrue(modalityId)
                .stream()
                .map(req -> RequirementDTO.builder()
                        .id(req.getId())
                        .requirementName(req.getRequirementName())
                        .description(req.getDescription())
                        .expectedValue(req.getExpectedValue())
                        .ruleType(req.getRuleType())
                        .build())
                .toList();

        var documents = requiredDocumentRepository.findByModalityIdAndActiveTrue(modalityId)
                .stream()
                .map(doc -> RequiredDocumentDTO.builder()
                        .id(doc.getId())
                        .documentName(doc.getDocumentName())
                        .description(doc.getDescription())
                        .allowedFormat(doc.getAllowedFormat())
                        .maxFileSizeMB(doc.getMaxFileSizeMB())
                        .build())
                .toList();

        ModalityDTO modalityDetail = ModalityDTO.builder()
                .id(modalityId)
                .name(degreeModalityRepository.findById(modalityId).orElseThrow().
                        getName())
                .description(degreeModalityRepository.findById(modalityId).orElseThrow().
                        getDescription())
                .creditsRequired(degreeModalityRepository.findById(modalityId).orElseThrow().
                        getCreditsRequired())
                .type(degreeModalityRepository.findById(modalityId).orElseThrow().
                        getType())
                .requirements(requirements)
                .documents(documents)
                .build();

        return ResponseEntity.ok(modalityDetail);

    }
    public ResponseEntity<?> startStudentModality(Long modalityId) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        User student = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        DegreeModality modality = degreeModalityRepository.findById(modalityId)
                .orElseThrow(() -> new RuntimeException(
                        "La modalidad con ID " + modalityId + " no existe"
                ));

        StudentProfile profile = studentProfileRepository.findByUserId(student.getId())
                .orElseThrow(() -> new RuntimeException(
                        "Debe completar su perfil académico antes de seleccionar una modalidad"
                ));


        List<ModalityProcessStatus> activeStatuses = List.of(
                ModalityProcessStatus.MODALITY_SELECTED
        );

        boolean hasActiveModality =
                studentModalityRepository.existsByStudentIdAndStatusIn(
                        student.getId(),
                        activeStatuses
                );

        if (hasActiveModality) {
            return ResponseEntity.badRequest().body(
                    Map.of(
                            "eligible", false,
                            "message", "Ya tienes una modalidad de grado en curso. No puedes iniciar otra."
                    )
            );
        }


        boolean exists = studentModalityRepository
                .existsByStudent_IdAndModality_Id(student.getId(), modalityId);

        if (exists) {
            return ResponseEntity.badRequest()
                    .body("Ya has iniciado esta modalidad");
        }


        List<ModalityRequirements> requirements =
                modalityRequirementsRepository.findByModalityIdAndActiveTrue(modalityId);

        List<ValidationItemDTO> results = new ArrayList<>();
        boolean allValid = true;

        for (ModalityRequirements req : requirements) {

            if (req.getRuleType() != RuleType.NUMERIC) {
                continue;
            }

            boolean fulfilled = true;
            String studentValue = "";

            if (req.getRequirementName().toLowerCase().contains("crédito")) {

                fulfilled = profile.getApprovedCredits() >=
                        Long.parseLong(req.getExpectedValue());

                studentValue = String.valueOf(profile.getApprovedCredits());
            }

            if (req.getRequirementName().toLowerCase().contains("promedio")) {

                fulfilled = profile.getGpa() >=
                        Double.parseDouble(req.getExpectedValue());

                studentValue = String.valueOf(profile.getGpa());
            }

            results.add(
                    ValidationItemDTO.builder()
                            .requirementName(req.getRequirementName())
                            .expectedValue(req.getExpectedValue())
                            .studentValue(studentValue)
                            .fulfilled(fulfilled)
                            .build()
            );

            if (!fulfilled) {
                allValid = false;
            }
        }

        if (!allValid) {
            return ResponseEntity.badRequest().body(
                    ValidationResultDTO.builder()
                            .eligible(false)
                            .results(results)
                            .message("No cumples los requisitos académicos para esta modalidad")
                            .build()
            );
        }


        StudentModality studentModality = StudentModality.builder()
                .student(student)
                .modality(modality)
                .status(ModalityProcessStatus.MODALITY_SELECTED)
                .selectionDate(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        studentModalityRepository.save(studentModality);

        historyRepository.save(
                ModalityProcessStatusHistory.builder()
                        .studentModality(studentModality)
                        .status(ModalityProcessStatus.MODALITY_SELECTED)
                        .changeDate(LocalDateTime.now())
                        .responsible(student)
                        .observations("Modalidad seleccionada por el estudiante")
                        .build()
        );


        return ResponseEntity.ok(
                Map.of(
                        "eligible", true,
                        "studentModalityId", studentModality.getId(),
                        "studentModalityName", modality.getName(),
                        "message", "Modalidad iniciada correctamente. Puedes subir los documentos."
                )
        );
    }

    public ResponseEntity<?> uploadRequiredDocument(
            Long studentModalityId,
            Long requiredDocumentId,
            MultipartFile file
    ) throws IOException {

        if (file == null || file.isEmpty()) {
            return ResponseEntity.badRequest().body("El archivo es obligatorio");
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        User student = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        StudentModality studentModality = studentModalityRepository.findById(studentModalityId)
                .orElseThrow(() -> new RuntimeException("Modalidad del estudiante no encontrada"));

        if (!studentModality.getStudent().getId().equals(student.getId())) {
            return ResponseEntity.status(403).body("No autorizado");
        }

        RequiredDocument requiredDocument = requiredDocumentRepository.findById(requiredDocumentId)
                .orElseThrow(() -> new RuntimeException("Documento requerido no existe"));

        DegreeModality modality = studentModality.getModality();


        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename.substring(originalFilename.lastIndexOf('.') + 1).toLowerCase();

        if (requiredDocument.getAllowedFormat() != null &&
                !requiredDocument.getAllowedFormat().toLowerCase().contains(extension)) {
            return ResponseEntity.badRequest().body("Formato de archivo no permitido");
        }

        if (requiredDocument.getMaxFileSizeMB() != null &&
                file.getSize() > requiredDocument.getMaxFileSizeMB() * 1024L * 1024L) {
            return ResponseEntity.badRequest().body("El archivo supera el tamaño permitido");
        }


        String modalityFolder = modality.getName()
                .replaceAll("[^a-zA-Z0-9]", "_");

        String studentFolder = student.getName() + student.getLastName() + "_" +
                student.getLastName() + "_" +
                student.getId();

        Path basePath = Paths.get(
                uploadDir,
                modalityFolder,
                studentFolder
        );

        Files.createDirectories(basePath);

        String finalFileName = UUID.randomUUID() + "_" + originalFilename;
        Path fullPath = basePath.resolve(finalFileName);

        Files.copy(file.getInputStream(), fullPath, StandardCopyOption.REPLACE_EXISTING);


        StudentDocument studentDocument = studentDocumentRepository
                .findByStudentModalityIdAndDocumentConfigId(studentModalityId, requiredDocumentId)
                .orElse(
                        StudentDocument.builder()
                                .studentModality(studentModality)
                                .documentConfig(requiredDocument)
                                .build()
                );

        studentDocument.setFileName(originalFilename);
        studentDocument.setFilePath(fullPath.toString());
        studentDocument.setStatus(DocumentStatus.PENDING);
        studentDocument.setUploadDate(LocalDateTime.now());
        studentDocumentRepository.save(studentDocument);

        documentHistoryRepository.save(
                StudentDocumentStatusHistory.builder()
                        .studentDocument(studentDocument)
                        .status(DocumentStatus.PENDING)
                        .changeDate(LocalDateTime.now())
                        .responsible(student)
                        .observations("Documento cargado o actualizado por el estudiante")
                        .build()
        );


        return ResponseEntity.ok(
                Map.of(
                        "message", "Documento subido correctamente",
                        "path", fullPath.toString()
                )
        );
    }

    public ResponseEntity<?> validateAllDocumentsUploaded(Long studentModalityId) {

        StudentModality studentModality = studentModalityRepository.findById(studentModalityId)
                .orElseThrow(() -> new RuntimeException("Modalidad no encontrada"));

        Long modalityId = studentModality.getModality().getId();

        List<RequiredDocument> requiredDocuments =
                requiredDocumentRepository.findByModalityIdAndActiveTrue(modalityId);

        List<StudentDocument> uploadedDocuments =
                studentDocumentRepository.findByStudentModalityId(studentModalityId);

        Set<Long> uploadedIds = uploadedDocuments.stream()
                .map(d -> d.getDocumentConfig().getId())
                .collect(Collectors.toSet());

        List<String> missingDocuments = requiredDocuments.stream()
                .filter(RequiredDocument::isMandatory)
                .filter(doc -> !uploadedIds.contains(doc.getId()))
                .map(RequiredDocument::getDocumentName)
                .toList();

        boolean allUploaded = missingDocuments.isEmpty();

        return ResponseEntity.ok(
                Map.of(
                        "canContinue", allUploaded,
                        "missingDocuments", missingDocuments
                )
        );
    }

    public ResponseEntity<?> getStudentDocuments(Long studentModalityId) {

        StudentModality studentModality = studentModalityRepository
                .findById(studentModalityId)
                .orElseThrow(() ->
                        new RuntimeException("Modalidad del estudiante no encontrada")
                );

        List<StudentDocument> documents =
                studentDocumentRepository.findByStudentModalityId(studentModalityId);

        List<Map<String, Object>> response = documents.stream()
                .map(doc -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("studentDocumentId", doc.getId());
                    map.put("documentName", doc.getDocumentConfig().getDocumentName());
                    map.put("mandatory", doc.getDocumentConfig().isMandatory());
                    map.put("status", doc.getStatus());
                    map.put("notes", doc.getNotes());
                    map.put("uploadedAt", doc.getUploadDate());
                    map.put("filePath", doc.getFilePath());
                    return map;
                })
                .toList();

        return ResponseEntity.ok(response);
    }

    public ResponseEntity<?> viewStudentDocument(Long studentDocumentId) throws MalformedURLException {

        StudentDocument doc = studentDocumentRepository.findById(studentDocumentId)
                .orElseThrow(() -> new RuntimeException("Document not found"));

        Path path = Paths.get(doc.getFilePath());
        if (!Files.exists(path)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("File not found on server");
        }

        UrlResource resource = new UrlResource(path.toUri());

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=\"" + doc.getFileName() + "\"")
                .body(resource);

    }

    public ResponseEntity<?> reviewStudentDocument(Long studentDocumentId, DocumentReviewDTO request) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        User secretary = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        StudentDocument document = studentDocumentRepository.findById(studentDocumentId)
                .orElseThrow(() -> new RuntimeException("Document not found"));

        if ((request.getStatus() == DocumentStatus.REJECTED_FOR_SECRETARY_REVIEW ||
                request.getStatus() == DocumentStatus.CORRECTIONS_REQUESTED_BY_SECRETARY)
                && (request.getNotes() == null || request.getNotes().isBlank())) {

            return ResponseEntity.badRequest()
                    .body("You must provide notes when rejecting or requesting corrections");
        }

        document.setStatus(request.getStatus());
        document.setNotes(request.getNotes());
        document.setUploadDate(LocalDateTime.now());

        studentDocumentRepository.save(document);

        documentHistoryRepository.save(
                StudentDocumentStatusHistory.builder()
                        .studentDocument(document)
                        .status(request.getStatus())
                        .changeDate(LocalDateTime.now())
                        .responsible(secretary)
                        .observations(request.getNotes())
                        .build()
        );


        return ResponseEntity.ok(
                Map.of(
                        "message", "Documento revisado correctamente",
                        "documentId", document.getId(),
                        "newStatus", document.getStatus()
                )
        );


    }

    public ResponseEntity<?> approveModalityBySecretary(Long studentModalityId) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        User secretary = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        StudentModality studentModality = studentModalityRepository.findById(studentModalityId)
                .orElseThrow(() -> new RuntimeException("Modality not found"));


        if (!(studentModality.getStatus() == ModalityProcessStatus.MODALITY_SELECTED ||
                studentModality.getStatus() == ModalityProcessStatus.CORRECTIONS_REQUESTED_SECRETARY)) {

            return ResponseEntity.badRequest().body(
                    Map.of(
                            "approved", false,
                            "message", "La modalidad no está en un estado válido para ser aprobada por secretaría",
                            "currentStatus", studentModality.getStatus()
                    )
            );
        }

        Long modalityId = studentModality.getModality().getId();


        List<RequiredDocument> mandatoryDocuments =
                requiredDocumentRepository.findByModalityIdAndActiveTrue(modalityId)
                        .stream()
                        .filter(RequiredDocument::isMandatory)
                        .toList();


        List<StudentDocument> uploadedDocuments =
                studentDocumentRepository.findByStudentModalityId(studentModalityId);

        Map<Long, StudentDocument> uploadedMap = uploadedDocuments.stream()
                .collect(Collectors.toMap(
                        doc -> doc.getDocumentConfig().getId(),
                        doc -> doc
                ));


        List<Map<String, Object>> invalidDocuments = new ArrayList<>();

        for (RequiredDocument required : mandatoryDocuments) {

            StudentDocument uploaded = uploadedMap.get(required.getId());

            if (uploaded == null) {
                invalidDocuments.add(
                        Map.of(
                                "documentName", required.getDocumentName(),
                                "status", "NOT_UPLOADED"
                        )
                );
                continue;
            }

            if (uploaded.getStatus() != DocumentStatus.ACCEPTED_FOR_SECRETARY_REVIEW) {
                invalidDocuments.add(
                        Map.of(
                                "documentName", required.getDocumentName(),
                                "status", uploaded.getStatus()
                        )
                );
            }
        }


        if (!invalidDocuments.isEmpty()) {
            return ResponseEntity.badRequest().body(
                    Map.of(
                            "approved", false,
                            "message", "Para poder aprobar la modalidad, todos los documentos obligatorios deben estar aceptados",
                            "documents", invalidDocuments
                    )
            );
        }


        studentModality.setStatus(ModalityProcessStatus.READY_FOR_COUNCIL);
        studentModality.setUpdatedAt(LocalDateTime.now());
        studentModalityRepository.save(studentModality);


        ModalityProcessStatusHistory history = ModalityProcessStatusHistory.builder()
                .studentModality(studentModality)
                .status(ModalityProcessStatus.READY_FOR_COUNCIL)
                .changeDate(LocalDateTime.now())
                .responsible(secretary)
                .observations("Modalidad aprobada por secretaría")
                .build();

        historyRepository.save(history);


        return ResponseEntity.ok(
                Map.of(
                        "approved", true,
                        "newStatus", ModalityProcessStatus.READY_FOR_COUNCIL,
                        "message", "Modalidad aprobada correctamente y enviada al consejo"
                )
        );
    }

    public ResponseEntity<?> approveModalityByCouncil(Long studentModalityId) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        User councilMember = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        StudentModality studentModality = studentModalityRepository.findById(studentModalityId)
                .orElseThrow(() -> new RuntimeException("Modality not found"));


        if (!(studentModality.getStatus() == ModalityProcessStatus.READY_FOR_COUNCIL ||
                studentModality.getStatus() == ModalityProcessStatus.UNDER_REVIEW_COUNCIL)) {

            return ResponseEntity.badRequest().body(
                    Map.of(
                            "approved", false,
                            "message", "La modalidad no está en estado válido para aprobación por el Consejo",
                            "currentStatus", studentModality.getStatus()
                    )
            );
        }


        studentModality.setStatus(ModalityProcessStatus.PROPOSAL_APPROVED);
        studentModality.setUpdatedAt(LocalDateTime.now());
        studentModalityRepository.save(studentModality);


        ModalityProcessStatusHistory history = ModalityProcessStatusHistory.builder()
                .studentModality(studentModality)
                .status(ModalityProcessStatus.PROPOSAL_APPROVED)
                .changeDate(LocalDateTime.now())
                .responsible(councilMember)
                .observations("Modalidad aprobada por el Consejo Académico")
                .build();

        historyRepository.save(history);

        return ResponseEntity.ok(
                Map.of(
                        "approved", true,
                        "newStatus", ModalityProcessStatus.PROPOSAL_APPROVED,
                        "message", "Modalidad aprobada definitivamente por el Consejo Académico"
                )
        );
    }

    public ResponseEntity<?> reviewStudentDocumentByCouncil(Long studentDocumentId, DocumentReviewDTO request) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        User councilMember = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        StudentDocument document = studentDocumentRepository.findById(studentDocumentId)
                .orElseThrow(() -> new RuntimeException("Document not found"));


        if ((request.getStatus() == DocumentStatus.REJECTED_FOR_COUNCIL_REVIEW ||
                request.getStatus() == DocumentStatus.CORRECTIONS_REQUESTED_BY_COUNCIL)
                && (request.getNotes() == null || request.getNotes().isBlank())) {

            return ResponseEntity.badRequest().body(
                    "You must provide notes when rejecting or requesting corrections"
            );
        }


        document.setStatus(request.getStatus());
        document.setNotes(request.getNotes());
        document.setUploadDate(LocalDateTime.now());

        studentDocumentRepository.save(document);


        documentHistoryRepository.save(
                StudentDocumentStatusHistory.builder()
                        .studentDocument(document)
                        .status(request.getStatus())
                        .changeDate(LocalDateTime.now())
                        .responsible(councilMember)
                        .observations(request.getNotes())
                        .build()
        );


        return ResponseEntity.ok(
                Map.of(
                        "success", true,
                        "documentId", document.getId(),
                        "documentName", document.getDocumentConfig().getDocumentName(),
                        "newStatus", document.getStatus(),
                        "message", "Documento revisado correctamente por el Consejo"
                )
        );
    }

    private String describeModalityStatus(ModalityProcessStatus status) {

        return switch (status) {

            case MODALITY_SELECTED ->
                    "Has seleccionado una modalidad de grado. Está pendiente de revisión por Secretaría.";

            case CORRECTIONS_REQUESTED_SECRETARY ->
                    "La Secretaría solicitó correcciones. Debes ajustar la información requerida.";

            case READY_FOR_COUNCIL ->
                    "La Secretaría aprobó tu modalidad de grado. Está pendiente de revisión por el Concejo.";

            case UNDER_REVIEW_COUNCIL ->
                    "El Concejo Académico está revisando tu modalidad.";

            case CORRECTIONS_REQUESTED_COUNCIL ->
                    "El Cocsejo solicitó correcciones. Debes realizar los ajustes indicados.";

            case PROPOSAL_APPROVED ->
                    "Tu modalidad fue aprobada por el Concejo Académico.";

            case MODALITY_CANCELLED ->
                    "La modalidad fue cancelada.";

            case MODALITY_CLOSED ->
                    "La modalidad fue cerrada.";

            default ->
                    "Estado del proceso no definido.";
        };
    }

    public ResponseEntity<?> getCurrentStudentModality() {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        User student = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        StudentModality studentModality = studentModalityRepository
                .findTopByStudentIdOrderByUpdatedAtDesc(student.getId())
                .orElseThrow(() ->
                        new RuntimeException("Not current modality found for the student")
                );

        List<ModalityProcessStatusHistory> historyEntities =
                historyRepository.findByStudentModalityIdOrderByChangeDateAsc(
                        studentModality.getId()
                );

        List<ModalityStatusHistoryDTO> history = historyEntities.stream()
                .map(h -> ModalityStatusHistoryDTO.builder()
                        .status(h.getStatus().name())
                        .description(describeModalityStatus(h.getStatus()))
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

        return ResponseEntity.ok(
                StudentModalityDTO.builder()
                        .studentModalityId(studentModality.getId())
                        .modalityName(studentModality.getModality().getName())
                        .currentStatus(studentModality.getStatus().name())
                        .currentStatusDescription(
                                describeModalityStatus(studentModality.getStatus())
                        )
                        .lastUpdatedAt(studentModality.getUpdatedAt())
                        .history(history)
                        .build()
        );
    }

    public ResponseEntity<?> getAllStudentModalitiesForSecretary() {

        List<StudentModality> modalities = studentModalityRepository.findAll();

        List<ModalityListDTO> response = modalities.stream()
                .map(sm -> {

                    ModalityProcessStatus status = sm.getStatus();

                    boolean pending =
                            status == ModalityProcessStatus.MODALITY_SELECTED ||
                                    status == ModalityProcessStatus.CORRECTIONS_REQUESTED_SECRETARY;

                    return ModalityListDTO.builder()
                            .studentModalityId(sm.getId())
                            .studentName(
                                    sm.getStudent().getName() + " " + sm.getStudent().getLastName()
                            )
                            .studentEmail(sm.getStudent().getEmail())
                            .modalityName(sm.getModality().getName())
                            .currentStatus(status.name())
                            .currentStatusDescription(describeModalityStatus(status))
                            .lastUpdatedAt(sm.getUpdatedAt())
                            .hasPendingActions(pending)
                            .build();
                })
                .toList();

        return ResponseEntity.ok(response);
    }

    public ResponseEntity<?> getStudentModalityDetailForSecretary(Long studentModalityId) {

        StudentModality studentModality = studentModalityRepository.findById(studentModalityId)
                .orElseThrow(() -> new RuntimeException("Modality not found"));

        User student = studentModality.getStudent();
        DegreeModality modality = studentModality.getModality();

        List<ModalityStatusHistoryDTO> history =
                historyRepository
                        .findByStudentModalityIdOrderByChangeDateAsc(studentModalityId)
                        .stream()
                        .map(h -> ModalityStatusHistoryDTO.builder()
                                .status(h.getStatus().name())
                                .description(describeModalityStatus(h.getStatus()))
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



        List<RequiredDocument> requiredDocuments =
                requiredDocumentRepository.findByModalityIdAndActiveTrue(modality.getId());

        List<StudentDocument> uploadedDocuments =
                studentDocumentRepository.findByStudentModalityId(studentModalityId);

        Map<Long, StudentDocument> uploadedMap =
                uploadedDocuments.stream()
                        .collect(Collectors.toMap(
                                d -> d.getDocumentConfig().getId(),
                                d -> d
                        ));

        List<DetailDocumentDTO> documents = requiredDocuments.stream()
                .map(req -> {

                    StudentDocument uploaded = uploadedMap.get(req.getId());

                    return DetailDocumentDTO.builder()
                            .studentDocumentId(
                                    uploaded != null ? uploaded.getId() : null
                            )
                            .documentName(req.getDocumentName())
                            .mandatory(req.isMandatory())
                            .uploaded(uploaded != null)
                            .status(
                                    uploaded != null
                                            ? uploaded.getStatus().name()
                                            : "NOT_UPLOADED"
                            )
                            .statusDescription(
                                    uploaded != null
                                            ? uploaded.getStatus().name()
                                            : "Documento aún no cargado por el estudiante."
                            )
                            .notes(
                                    uploaded != null ? uploaded.getNotes() : null
                            )
                            .lastUpdate(
                                    uploaded != null ? uploaded.getUploadDate() : null
                            )
                            .build();
                })
                .toList();

        return ResponseEntity.ok(
                StudentModalityDTO.builder()
                        .studentModalityId(studentModality.getId())
                        .studentName(student.getName() + " " + student.getLastName())
                        .studentEmail(student.getEmail())
                        .modalityName(modality.getName())
                        .currentStatus(studentModality.getStatus().name())
                        .currentStatusDescription(
                                describeModalityStatus(studentModality.getStatus())
                        )
                        .lastUpdatedAt(studentModality.getUpdatedAt())
                        .history(history)
                        .documents(documents)
                        .build()
        );




    }


}
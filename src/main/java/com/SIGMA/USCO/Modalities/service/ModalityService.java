package com.SIGMA.USCO.Modalities.service;

import com.SIGMA.USCO.Modalities.Entity.*;
import com.SIGMA.USCO.Modalities.Entity.enums.*;
import com.SIGMA.USCO.Modalities.Repository.DegreeModalityRepository;
import com.SIGMA.USCO.Modalities.Repository.ModalityProcessStatusHistoryRepository;
import com.SIGMA.USCO.Modalities.Repository.ModalityRequirementsRepository;
import com.SIGMA.USCO.Modalities.Repository.StudentModalityRepository;
import com.SIGMA.USCO.Modalities.dto.*;

import com.SIGMA.USCO.Modalities.dto.response.FinalDefenseResponse;
import com.SIGMA.USCO.Modalities.dto.response.ProjectDirectorResponse;
import com.SIGMA.USCO.Users.Entity.ProgramAuthority;
import com.SIGMA.USCO.Users.Entity.enums.ProgramRole;
import com.SIGMA.USCO.Users.repository.ProgramAuthorityRepository;
import com.SIGMA.USCO.academic.entity.AcademicProgram;
import com.SIGMA.USCO.academic.entity.Faculty;
import com.SIGMA.USCO.academic.entity.ProgramDegreeModality;
import com.SIGMA.USCO.academic.entity.StudentProfile;
import com.SIGMA.USCO.Users.Entity.User;
import com.SIGMA.USCO.academic.repository.FacultyRepository;
import com.SIGMA.USCO.academic.repository.ProgramDegreeModalityRepository;
import com.SIGMA.USCO.academic.repository.StudentProfileRepository;
import com.SIGMA.USCO.Users.repository.UserRepository;
import com.SIGMA.USCO.documents.dto.DetailDocumentDTO;
import com.SIGMA.USCO.documents.dto.RequiredDocumentDTO;
import com.SIGMA.USCO.documents.entity.*;
import com.SIGMA.USCO.documents.repository.RequiredDocumentRepository;
import com.SIGMA.USCO.documents.repository.StudentDocumentRepository;
import com.SIGMA.USCO.documents.repository.StudentDocumentStatusHistoryRepository;
import com.SIGMA.USCO.notifications.entity.enums.NotificationRecipientType;
import com.SIGMA.USCO.notifications.event.*;
import com.SIGMA.USCO.notifications.publisher.NotificationEventPublisher;
import jakarta.transaction.Transactional;
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
    private final NotificationEventPublisher notificationEventPublisher;
    private final FacultyRepository facultyRepository;
    private final ProgramDegreeModalityRepository programDegreeModalityRepository;
    private final ProgramAuthorityRepository programAuthorityRepository;


    @Value("${file.upload-dir}")
    private String uploadDir;


    public DegreeModality createModality(ModalityDTO request) {

        if (request.getName() == null || request.getName().isBlank()) {
            throw new IllegalArgumentException("El nombre de la modalidad es obligatorio.");
        }

        if (request.getFacultyId() == null) {
            throw new IllegalArgumentException("La facultad es obligatoria.");
        }

        Faculty faculty = facultyRepository.findById(request.getFacultyId())
                .orElseThrow(() ->
                        new IllegalArgumentException("La facultad no existe.")
                );

        if (degreeModalityRepository.existsByNameIgnoreCaseAndFacultyId(request.getName(), faculty.getId())) {
            throw new IllegalArgumentException("Ya existe una modalidad con ese nombre en esta facultad.");
        }

        DegreeModality modality = DegreeModality.builder()
                .name(request.getName().toUpperCase())
                .description(request.getDescription())
                .status(ModalityStatus.ACTIVE)
                .faculty(faculty)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        return degreeModalityRepository.save(modality);
    }
    public ResponseEntity<?> updateModality(Long modalityId, ModalityDTO request) {

        if (!degreeModalityRepository.existsById(modalityId)) {
            return ResponseEntity.badRequest().body("La modalidad con ID " + modalityId + " no existe.");
        }

        DegreeModality modality = degreeModalityRepository.findById(modalityId).orElseThrow();

        Faculty faculty = facultyRepository.findById(request.getFacultyId())
                .orElseThrow(() ->
                        new IllegalArgumentException("La facultad no existe.")
                );

        modality.setFaculty(faculty);
        modality.setName(request.getName());
        modality.setDescription(request.getDescription());
        modality.setStatus(request.getStatus());
        modality.setUpdatedAt(LocalDateTime.now());

        degreeModalityRepository.save(modality);

        return ResponseEntity.ok("Modalidad actualizada exitosamente");
    }
    public ResponseEntity<?> desactiveModality(Long modalityId) {

        if (!degreeModalityRepository.existsById(modalityId)) {
            return ResponseEntity.badRequest().body("La modalidad con ID " + modalityId + " no existe.");
        }

        DegreeModality modality = degreeModalityRepository.findById(modalityId).orElseThrow();

        modality.setStatus(ModalityStatus.INACTIVE);
        modality.setUpdatedAt(LocalDateTime.now());

        degreeModalityRepository.save(modality);

        return ResponseEntity.ok("Modalidad desactivada exitosamente");
    }
    public void createModalityRequirements(Long modalityId, List<RequirementDTO> requirements) {

        if (requirements == null || requirements.isEmpty()) {
            throw new IllegalArgumentException("La lista de requisitos no puede estar vacía.");
        }

        DegreeModality modality = degreeModalityRepository.findById(modalityId)
                .orElseThrow(() -> new IllegalArgumentException("La modalidad con ID " + modalityId + " no existe."));

        for (RequirementDTO req : requirements) {

            if (req.getRequirementName() == null || req.getRequirementName().isBlank()) {throw new IllegalArgumentException("El nombre del requisito es obligatorio.");}

            if (req.getRuleType() == null) {
                throw new IllegalArgumentException("El tipo de regla es obligatorio para el requisito: " + req.getRequirementName());
            }

            if (req.getExpectedValue() == null || req.getExpectedValue().isBlank()) {
                throw new IllegalArgumentException("El valor esperado es obligatorio para el requisito: " + req.getRequirementName());
            }

            ModalityRequirements requirement = ModalityRequirements.builder()
                    .modality(modality)
                    .requirementName(req.getRequirementName())
                    .description(req.getDescription())
                    .ruleType(req.getRuleType())
                    .expectedValue(req.getExpectedValue())
                    .active(true)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();

            modalityRequirementsRepository.save(requirement);
        }
    }

    public void updateModalityRequirement(Long modalityId, Long requirementId, RequirementDTO req) {

        DegreeModality modality = degreeModalityRepository.findById(modalityId)
                .orElseThrow(() ->
                        new IllegalArgumentException("La modalidad con ID " + modalityId + " no existe.")
                );

        ModalityRequirements requirement = modalityRequirementsRepository.findById(requirementId)
                .orElseThrow(() ->
                        new IllegalArgumentException("El requisito con ID " + requirementId + " no existe.")
                );

        if (!requirement.getModality().getId().equals(modality.getId())) {
            throw new IllegalArgumentException(
                    "El requisito no pertenece a la modalidad indicada."
            );
        }



        if (req.getRequirementName() != null) {
            if (req.getRequirementName().isBlank()) {
                throw new IllegalArgumentException("El nombre del requisito no puede estar vacío.");
            }
            requirement.setRequirementName(req.getRequirementName());
        }

        if (req.getDescription() != null) {
            requirement.setDescription(req.getDescription());
        }

        if (req.getRuleType() != null) {
            requirement.setRuleType(req.getRuleType());
        }

        if (req.getExpectedValue() != null) {
            if (req.getExpectedValue().isBlank()) {
                throw new IllegalArgumentException("El valor esperado no puede estar vacío.");
            }
            requirement.setExpectedValue(req.getExpectedValue());
        }



        requirement.setUpdatedAt(LocalDateTime.now());

        modalityRequirementsRepository.save(requirement);
    }

    public ResponseEntity<List<RequirementDTO>> getModalityRequirements(Long modalityId, Boolean active) {

        if (!degreeModalityRepository.existsById(modalityId)) {
            return ResponseEntity.badRequest().body(List.of());
        }

        List<ModalityRequirements> requirements;

        if (active != null) {
            requirements = modalityRequirementsRepository.findByModalityIdAndActive(modalityId, active);
        } else {
            requirements = modalityRequirementsRepository.findByModalityId(modalityId);
        }

        List<RequirementDTO> response = requirements.stream()
                .map(r -> RequirementDTO.builder()
                        .id(r.getId())
                        .requirementName(r.getRequirementName())
                        .description(r.getDescription())
                        .ruleType(r.getRuleType())
                        .expectedValue(r.getExpectedValue())
                        .active(r.isActive())
                        .build())
                .toList();

        return ResponseEntity.ok(response);
    }
    public ResponseEntity<?> deleteRequirement(Long requirementId) {

        ModalityRequirements requirement = modalityRequirementsRepository.findById(requirementId)
                .orElseThrow(() -> new RuntimeException("Requisito no encontrado"));

        requirement.setActive(false);
        requirement.setUpdatedAt(LocalDateTime.now());

        modalityRequirementsRepository.save(requirement);

        return ResponseEntity.ok("Requisito desactivado correctamente");
    }
    public ResponseEntity<List<ModalityDTO>> getAllModalities() {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        StudentProfile profile = studentProfileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Perfil académico no encontrado"));

        Long userProgramId = profile.getAcademicProgram().getId();

        List<DegreeModality> modalities = degreeModalityRepository.findByStatus(ModalityStatus.ACTIVE);

        List<ModalityDTO> modalityDTOs = modalities.stream().map(mod -> {

            Optional<ProgramDegreeModality> pdmOpt = programDegreeModalityRepository
                    .findByAcademicProgramIdAndDegreeModalityIdAndActiveTrue(userProgramId, mod.getId());

            Long creditsRequired = null;
            if (pdmOpt.isPresent() && pdmOpt.get().getCreditsRequired() != null) {
                creditsRequired = pdmOpt.get().getCreditsRequired();
            }

            return ModalityDTO.builder()
                    .id(mod.getId())
                    .name(mod.getName())
                    .facultyName(mod.getFaculty().getName())
                    .description(mod.getDescription())
                    .facultyId(mod.getFaculty().getId())
                    .status(mod.getStatus())
                    .requiredCredits(creditsRequired != null ? creditsRequired.doubleValue() : null)
                    .build();

        }).toList();

        return ResponseEntity.ok(modalityDTOs);
    }
    public ResponseEntity<?> getModalityDetail(Long modalityId) {

        if (!degreeModalityRepository.existsById(modalityId)) {
            return ResponseEntity.badRequest().body("La modalidad con ID " + modalityId + " no existe.");
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        StudentProfile profile = studentProfileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Perfil académico no encontrado"));

        Long userProgramId = profile.getAcademicProgram().getId();

        // Obtener créditos requeridos para el programa del estudiante
        Optional<ProgramDegreeModality> pdmOpt = programDegreeModalityRepository
                .findByAcademicProgramIdAndDegreeModalityIdAndActiveTrue(userProgramId, modalityId);

        Long creditsRequired = null;
        if (pdmOpt.isPresent() && pdmOpt.get().getCreditsRequired() != null) {
            creditsRequired = pdmOpt.get().getCreditsRequired();
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

        var documents = requiredDocumentRepository
                .findByModalityIdAndActiveTrueAndIsMandatoryTrue(modalityId)
                .stream()
                .map(doc -> RequiredDocumentDTO.builder()
                        .id(doc.getId())
                        .modalityId(modalityId)
                        .documentName(doc.getDocumentName())
                        .description(doc.getDescription())
                        .allowedFormat(doc.getAllowedFormat())
                        .maxFileSizeMB(doc.getMaxFileSizeMB())
                        .mandatory(doc.isMandatory())
                        .build())
                .toList();

        DegreeModality modality = degreeModalityRepository.findById(modalityId).orElseThrow();

        ModalityDTO modalityDetail = ModalityDTO.builder()
                .id(modalityId)
                .name(modality.getName())
                .description(modality.getDescription())
                .facultyId(modality.getFaculty().getId())
                .facultyName(modality.getFaculty().getName())
                .requiredCredits(creditsRequired != null ? creditsRequired.doubleValue() : null)
                .requirements(requirements)
                .documents(documents)
                .build();

        return ResponseEntity.ok(modalityDetail);

    }
    @Transactional
    public ResponseEntity<?> startStudentModality(Long modalityId) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        User student = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        StudentProfile profile = studentProfileRepository.findByUserId(student.getId())
                .orElseThrow(() -> new RuntimeException("Debe completar su perfil académico antes de seleccionar una modalidad"));


        DegreeModality modality = degreeModalityRepository.findById(modalityId)
                .orElseThrow(() -> new RuntimeException("La modalidad con ID " + modalityId + " no existe"));


        ProgramDegreeModality programDegreeModality =
                programDegreeModalityRepository.findByAcademicProgramIdAndDegreeModalityIdAndActiveTrue(profile.getAcademicProgram().getId(), modalityId)
                        .orElseThrow(() -> new RuntimeException("La modalidad no está habilitada para tu programa académico"));


        List<ModalityProcessStatus> activeStatuses = List.of(ModalityProcessStatus.MODALITY_SELECTED);

        boolean hasActiveModality = studentModalityRepository.existsByStudentIdAndStatusIn(student.getId(),activeStatuses);

        if (hasActiveModality) {
            return ResponseEntity.badRequest().body(
                    Map.of(
                            "eligible", false,
                            "message", "Ya tienes una modalidad de grado en curso. No puedes iniciar otra."
                    )
            );
        }


        boolean exists = studentModalityRepository.existsByStudent_IdAndProgramDegreeModality_Id(student.getId(), programDegreeModality.getId());

        if (exists) {
            return ResponseEntity.badRequest().body("Ya has iniciado esta modalidad anteriormente");
        }


        List<ModalityRequirements> requirements = modalityRequirementsRepository.findByModalityIdAndActiveTrue(modalityId);

        List<ValidationItemDTO> results = new ArrayList<>();
        boolean allValid = true;

        for (ModalityRequirements req : requirements) {

            if (req.getRuleType() != RuleType.NUMERIC) {
                continue;
            }

            boolean fulfilled = true;
            String studentValue = "";


            if (req.getRequirementName().toLowerCase().contains("crédito")) {

                double percentageRequired = Double.parseDouble(req.getExpectedValue()); // ej: 0.7
                long totalCredits = profile.getAcademicProgram().getTotalCredits();
                long requiredCredits = Math.round(totalCredits * percentageRequired);

                fulfilled = profile.getApprovedCredits() >= requiredCredits;
                studentValue = profile.getApprovedCredits() + " / " + requiredCredits;
            }


            if (req.getRequirementName().toLowerCase().contains("promedio")) {

                fulfilled = profile.getGpa() >= Double.parseDouble(req.getExpectedValue());
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
                .academicProgram(profile.getAcademicProgram())
                .programDegreeModality(programDegreeModality)
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


        notificationEventPublisher.publish(
                new StudentModalityStarted(
                        studentModality.getId(),
                        student.getId()
                )
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

    public ResponseEntity<?> uploadRequiredDocument(Long studentModalityId, Long requiredDocumentId, MultipartFile file) throws IOException {

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

        DegreeModality modality = studentModality.getProgramDegreeModality().getDegreeModality();

        if (!requiredDocument.getModality().getId().equals(modality.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("El documento no pertenece a la modalidad seleccionada");
        }

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

        notificationEventPublisher.publish(
                new StudentDocumentUpdatedEvent(studentModality.getId(), studentDocument.getId(), student.getId()
                )
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

        Long modalityId = studentModality.getProgramDegreeModality().getDegreeModality().getId();

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

        User programHead = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        StudentDocument document = studentDocumentRepository.findById(studentDocumentId)
                .orElseThrow(() -> new RuntimeException("Documento no encontrado"));

        AcademicProgram documentProgram = document.getStudentModality().getAcademicProgram();


        boolean authorized = programAuthorityRepository.existsByUser_IdAndAcademicProgram_IdAndRole(programHead.getId(), documentProgram.getId(), ProgramRole.PROGRAM_HEAD);

        if (!authorized) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("No tienes permisos para revisar documentos de este programa académico");
        }



        if ((request.getStatus() == DocumentStatus.REJECTED_FOR_PROGRAM_HEAD_REVIEW ||
                request.getStatus() == DocumentStatus.CORRECTIONS_REQUESTED_BY_PROGRAM_HEAD)
                && (request.getNotes() == null || request.getNotes().isBlank())) {

            return ResponseEntity.badRequest().body("Debe proporcionar notas al rechazar o solicitar correcciones");
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
                        .responsible(programHead)
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

    @Transactional
    public ResponseEntity<?> approveModalityByProgramHead(Long studentModalityId) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        User programHead = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        StudentModality studentModality = studentModalityRepository.findById(studentModalityId)
                .orElseThrow(() -> new RuntimeException("Modality not found"));


        Long academicProgramId = studentModality.getAcademicProgram().getId();

        boolean isAuthorized =
                programAuthorityRepository.existsByUser_IdAndAcademicProgram_IdAndRole(
                                programHead.getId(),
                                academicProgramId,
                                ProgramRole.PROGRAM_HEAD
                        );

        if (!isAuthorized) {
            return ResponseEntity.status(403).body(
                    Map.of(
                            "approved", false,
                            "message", "No tienes permisos para aprobar modalidades de este programa académico"
                    )
            );
        }


        if (!(studentModality.getStatus() == ModalityProcessStatus.MODALITY_SELECTED ||
                studentModality.getStatus() == ModalityProcessStatus.CORRECTIONS_REQUESTED_PROGRAM_HEAD)) {

            return ResponseEntity.badRequest().body(
                    Map.of(
                            "approved", false,
                            "message", "La modalidad no está en un estado válido para ser aprobada por la jefatura de programa",
                            "currentStatus", studentModality.getStatus()
                    )
            );
        }

        Long modalityId =
                studentModality
                        .getProgramDegreeModality()
                        .getDegreeModality()
                        .getId();


        List<RequiredDocument> mandatoryDocuments =
                requiredDocumentRepository.findByModalityIdAndActiveTrue(modalityId)
                        .stream()
                        .filter(RequiredDocument::isMandatory)
                        .toList();

        List<StudentDocument> uploadedDocuments =
                studentDocumentRepository.findByStudentModalityId(studentModalityId);

        Map<Long, StudentDocument> uploadedMap =
                uploadedDocuments.stream()
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

            if (uploaded.getStatus() != DocumentStatus.ACCEPTED_FOR_PROGRAM_HEAD_REVIEW) {
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


        studentModality.setStatus(ModalityProcessStatus.READY_FOR_PROGRAM_CURRICULUM_COMMITTEE);
        studentModality.setUpdatedAt(LocalDateTime.now());
        studentModalityRepository.save(studentModality);

        historyRepository.save(
                ModalityProcessStatusHistory.builder()
                        .studentModality(studentModality)
                        .status(ModalityProcessStatus.READY_FOR_PROGRAM_CURRICULUM_COMMITTEE)
                        .changeDate(LocalDateTime.now())
                        .responsible(programHead)
                        .observations("Modalidad aprobada por jefatura de programa")
                        .build()
        );

        notificationEventPublisher.publish(
                new ModalityApprovedByProgramHead(
                        studentModality.getId(),
                        programHead.getId()
                )
        );

        return ResponseEntity.ok(
                Map.of(
                        "approved", true,
                        "newStatus", ModalityProcessStatus.READY_FOR_PROGRAM_CURRICULUM_COMMITTEE,
                        "message", "Modalidad aprobada correctamente y enviada al comité de currículo de programa"
                )
        );
    }

    @Transactional
    public ResponseEntity<?> approveModalityByCommittee(Long studentModalityId) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        User committeeMember = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        StudentModality studentModality = studentModalityRepository.findById(studentModalityId)
                .orElseThrow(() -> new RuntimeException("Modality not found"));

        // 🔐 VALIDACIÓN CLAVE: comité asociado al programa académico
        Long academicProgramId = studentModality.getAcademicProgram().getId();

        boolean isAuthorized =
                programAuthorityRepository
                        .existsByUser_IdAndAcademicProgram_IdAndRole(
                                committeeMember.getId(),
                                academicProgramId,
                                ProgramRole.PROGRAM_CURRICULUM_COMMITTEE
                        );

        if (!isAuthorized) {
            return ResponseEntity.status(403).body(
                    Map.of(
                            "approved", false,
                            "message", "No tienes permisos para aprobar modalidades de este programa académico"
                    )
            );
        }

        // 🔄 Validación de estado
        if (!(studentModality.getStatus() == ModalityProcessStatus.READY_FOR_PROGRAM_CURRICULUM_COMMITTEE ||
                studentModality.getStatus() == ModalityProcessStatus.UNDER_REVIEW_PROGRAM_CURRICULUM_COMMITTEE)) {

            return ResponseEntity.badRequest().body(
                    Map.of(
                            "approved", false,
                            "message", "La modalidad no está en estado válido para aprobación por el comité de currículo de programa",
                            "currentStatus", studentModality.getStatus()
                    )
            );
        }

        // ✅ Aprobación final
        studentModality.setStatus(ModalityProcessStatus.PROPOSAL_APPROVED);
        studentModality.setUpdatedAt(LocalDateTime.now());
        studentModalityRepository.save(studentModality);

        historyRepository.save(
                ModalityProcessStatusHistory.builder()
                        .studentModality(studentModality)
                        .status(ModalityProcessStatus.PROPOSAL_APPROVED)
                        .changeDate(LocalDateTime.now())
                        .responsible(committeeMember)
                        .observations("Modalidad aprobada por el Comité de currículo de programa")
                        .build()
        );

        notificationEventPublisher.publish(
                new ModalityApprovedByCommitteeEvent(
                        studentModality.getId(),
                        committeeMember.getId()
                )
        );

        return ResponseEntity.ok(
                Map.of(
                        "approved", true,
                        "newStatus", ModalityProcessStatus.PROPOSAL_APPROVED,
                        "message", "Modalidad aprobada definitivamente por el comité de currículo de programa"
                )
        );
    }

    public ResponseEntity<?> reviewStudentDocumentByCommittee(Long studentDocumentId, DocumentReviewDTO request) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        User committeeMember = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        StudentDocument document = studentDocumentRepository.findById(studentDocumentId)
                .orElseThrow(() -> new RuntimeException("Document not found"));


        StudentModality studentModality = document.getStudentModality();
        Long academicProgramId = studentModality.getAcademicProgram().getId();

        boolean isAuthorized =
                programAuthorityRepository
                        .existsByUser_IdAndAcademicProgram_IdAndRole(
                                committeeMember.getId(),
                                academicProgramId,
                                ProgramRole.PROGRAM_CURRICULUM_COMMITTEE
                        );

        if (!isAuthorized) {
            return ResponseEntity.status(403).body(
                    Map.of(
                            "success", false,
                            "message", "No tienes permisos para revisar documentos de este programa académico"
                    )
            );
        }


        if ((request.getStatus() == DocumentStatus.REJECTED_FOR_PROGRAM_CURRICULUM_COMMITTEE_REVIEW ||
                request.getStatus() == DocumentStatus.CORRECTIONS_REQUESTED_BY_PROGRAM_CURRICULUM_COMMITTEE)
                && (request.getNotes() == null || request.getNotes().isBlank())) {

            return ResponseEntity.badRequest().body(
                    "Debe proporcionar notas al rechazar o solicitar correcciones"
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
                        .responsible(committeeMember)
                        .observations(request.getNotes())
                        .build()
        );


        if (request.getStatus() ==
                DocumentStatus.CORRECTIONS_REQUESTED_BY_PROGRAM_CURRICULUM_COMMITTEE) {

            notificationEventPublisher.publish(
                    new DocumentCorrectionsRequestedEvent(
                            document.getId(),
                            studentModality.getStudent().getId(),
                            request.getNotes(),
                            NotificationRecipientType.PROGRAM_CURRICULUM_COMMITTEE,
                            committeeMember.getId()
                    )
            );
        }

        return ResponseEntity.ok(
                Map.of(
                        "success", true,
                        "documentId", document.getId(),
                        "documentName", document.getDocumentConfig().getDocumentName(),
                        "newStatus", document.getStatus(),
                        "message", "Documento revisado correctamente por el comité de currículo de programa"
                )
        );
    }

    private String describeModalityStatus(ModalityProcessStatus status) {

        return switch (status) {

            case MODALITY_SELECTED ->
                    "Has seleccionado una modalidad de grado. Está pendiente de revisión por la jefatura del programa.";

            case CORRECTIONS_REQUESTED_PROGRAM_HEAD ->
                    "La jefatura del programa solicitó correcciones. Debes ajustar la información requerida.";

            case READY_FOR_PROGRAM_CURRICULUM_COMMITTEE ->
                    "La jefatura de programa aprobó tu modalidad de grado. Está pendiente de revisión por el comité de currículo de programa.";

            case UNDER_REVIEW_PROGRAM_CURRICULUM_COMMITTEE ->
                    "El comité de currículo de programa está revisando tu modalidad de grado.";

            case CORRECTIONS_REQUESTED_PROGRAM_CURRICULUM_COMMITTEE ->
                    "El comité de currículo de programa solicitó correcciones. Debes ajustar la información requerida.";

            case PROPOSAL_APPROVED ->
                    "Tu modalidad fue aprobada por el comité de currículo de programa.";

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

        User student = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));

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
                        .facultyName( studentModality.getProgramDegreeModality().getAcademicProgram().getFaculty().getName())
                        .academicProgramName( studentModality.getProgramDegreeModality().getAcademicProgram().getName())
                        .studentModalityId(studentModality.getId())
                        .modalityName(studentModality.getProgramDegreeModality().getDegreeModality().getName())
                        .currentStatus(studentModality.getStatus().name())
                        .currentStatusDescription(
                                describeModalityStatus(studentModality.getStatus())
                        )
                        .lastUpdatedAt(studentModality.getUpdatedAt())
                        .history(history)
                        .build()
        );
    }
    public ResponseEntity<?> getAllStudentModalitiesForProgramHead(List<ModalityProcessStatus> statuses, String name) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        User programHead = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        List<Long> programIds = programAuthorityRepository
                        .findByUser_Id(programHead.getId())
                        .stream()
                        .filter(pa -> pa.getRole() == ProgramRole.PROGRAM_HEAD)
                        .map(pa -> pa.getAcademicProgram().getId())
                        .toList();

        if (programIds.isEmpty()) {
            return ResponseEntity.ok(List.of());
        }

        boolean hasStatusFilter = statuses != null && !statuses.isEmpty();
        boolean hasNameFilter = name != null && !name.isBlank();

        List<StudentModality> modalities;

        if (hasStatusFilter && hasNameFilter) {

            modalities =
                    studentModalityRepository.findForProgramHeadWithStatusAndName(programIds, statuses, name);

        } else if (hasStatusFilter) {

            modalities =
                    studentModalityRepository
                            .findForProgramHeadWithStatus(programIds, statuses);

        } else if (hasNameFilter) {

            modalities =
                    studentModalityRepository
                            .findForProgramHeadWithName(programIds, name);

        } else {

            modalities =
                    studentModalityRepository
                            .findForProgramHead(programIds);
        }

        List<ModalityListDTO> response =
                modalities.stream()
                        .map(sm -> {

                            ModalityProcessStatus status = sm.getStatus();

                            boolean pending =
                                    status == ModalityProcessStatus.MODALITY_SELECTED ||
                                            status == ModalityProcessStatus.CORRECTIONS_REQUESTED_PROGRAM_HEAD;

                            return ModalityListDTO.builder()
                                    .studentModalityId(sm.getId())
                                    .studentName(
                                            sm.getStudent().getName() + " " +
                                                    sm.getStudent().getLastName()
                                    )
                                    .studentEmail(sm.getStudent().getEmail())
                                    .modalityName(
                                            sm.getProgramDegreeModality()
                                                    .getDegreeModality()
                                                    .getName()
                                    )
                                    .currentStatus(status.name())
                                    .currentStatusDescription(describeModalityStatus(status))
                                    .lastUpdatedAt(sm.getUpdatedAt())
                                    .hasPendingActions(pending)
                                    .build();
                        })
                        .toList();

        return ResponseEntity.ok(response);
    }

    public ResponseEntity<?> getAllStudentModalitiesForProgramCurriculumCommittee(List<ModalityProcessStatus> statuses, String name) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        User committeeMember = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));


        List<Long> programIds = programAuthorityRepository
                .findByUser_Id(committeeMember.getId())
                .stream()
                .filter(pa -> pa.getRole() == ProgramRole.PROGRAM_CURRICULUM_COMMITTEE)
                .map(pa -> pa.getAcademicProgram().getId())
                .toList();

        if (programIds.isEmpty()) {
            return ResponseEntity.ok(List.of());
        }

        boolean hasStatusFilter = statuses != null && !statuses.isEmpty();
        boolean hasNameFilter = name != null && !name.isBlank();

        List<StudentModality> modalities;

        if (hasStatusFilter && hasNameFilter) {

            modalities = studentModalityRepository
                    .findForProgramHeadWithStatusAndName(programIds, statuses, name);

        } else if (hasStatusFilter) {

            modalities = studentModalityRepository
                    .findForProgramHeadWithStatus(programIds, statuses);

        } else if (hasNameFilter) {

            modalities = studentModalityRepository
                    .findForProgramHeadWithName(programIds, name);

        } else {

            modalities = studentModalityRepository
                    .findForProgramHead(programIds);
        }

        List<ModalityListDTO> response =
                modalities.stream()
                        .map(sm -> {

                            ModalityProcessStatus status = sm.getStatus();

                            boolean pending =
                                    status == ModalityProcessStatus.READY_FOR_PROGRAM_CURRICULUM_COMMITTEE ||
                                            status == ModalityProcessStatus.UNDER_REVIEW_PROGRAM_CURRICULUM_COMMITTEE;

                            return ModalityListDTO.builder()
                                    .studentModalityId(sm.getId())
                                    .studentName(
                                            sm.getStudent().getName() + " " +
                                                    sm.getStudent().getLastName()
                                    )
                                    .studentEmail(sm.getStudent().getEmail())
                                    .modalityName(
                                            sm.getProgramDegreeModality()
                                                    .getDegreeModality()
                                                    .getName()
                                    )
                                    .currentStatus(status.name())
                                    .currentStatusDescription(describeModalityStatus(status))
                                    .lastUpdatedAt(sm.getUpdatedAt())
                                    .hasPendingActions(pending)
                                    .build();
                        })
                        .toList();

        return ResponseEntity.ok(response);
    }


    public ResponseEntity<?> getStudentModalityDetailForProgramHead(Long studentModalityId) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        User programHead = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        StudentModality studentModality = studentModalityRepository.findById(studentModalityId)
                .orElseThrow(() -> new RuntimeException("Modality not found"));


        AcademicProgram academicProgram = studentModality.getProgramDegreeModality().getAcademicProgram();

        boolean authorized =
                programAuthorityRepository.existsByUser_IdAndAcademicProgram_IdAndRole(
                        programHead.getId(),
                        academicProgram.getId(),
                        ProgramRole.PROGRAM_HEAD
                );

        if (!authorized) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("No tiene permiso para ver esta modalidad");
        }

        User student = studentModality.getStudent();
        DegreeModality modality =
                studentModality.getProgramDegreeModality().getDegreeModality();

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
                requiredDocumentRepository
                        .findByModalityIdAndActiveTrue(modality.getId());

        List<StudentDocument> uploadedDocuments =
                studentDocumentRepository
                        .findByStudentModalityId(studentModalityId);

        Map<Long, StudentDocument> uploadedMap =
                uploadedDocuments.stream()
                        .collect(Collectors.toMap(
                                d -> d.getDocumentConfig().getId(),
                                d -> d
                        ));

        List<DetailDocumentDTO> documents =
                requiredDocuments.stream()
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
                        .facultyName(academicProgram.getFaculty().getName())
                        .academicProgramName(academicProgram.getName())
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

    public ResponseEntity<?> getStudentModalityDetailForCommittee(Long studentModalityId) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        User committeeMember = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        StudentModality studentModality = studentModalityRepository.findById(studentModalityId)
                .orElseThrow(() -> new RuntimeException("Modality not found"));


        AcademicProgram academicProgram = studentModality.getProgramDegreeModality().getAcademicProgram();

        boolean authorized =
                programAuthorityRepository.existsByUser_IdAndAcademicProgram_IdAndRole(
                        committeeMember.getId(),
                        academicProgram.getId(),
                        ProgramRole.PROGRAM_CURRICULUM_COMMITTEE
                );

        if (!authorized) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("No tiene permiso para ver esta modalidad");
        }

        User student = studentModality.getStudent();
        DegreeModality modality =
                studentModality.getProgramDegreeModality().getDegreeModality();

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
                requiredDocumentRepository
                        .findByModalityIdAndActiveTrue(modality.getId());

        List<StudentDocument> uploadedDocuments =
                studentDocumentRepository
                        .findByStudentModalityId(studentModalityId);

        Map<Long, StudentDocument> uploadedMap =
                uploadedDocuments.stream()
                        .collect(Collectors.toMap(
                                d -> d.getDocumentConfig().getId(),
                                d -> d
                        ));

        List<DetailDocumentDTO> documents =
                requiredDocuments.stream()
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
                        .facultyName(academicProgram.getFaculty().getName())
                        .academicProgramName(academicProgram.getName())
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

    public ResponseEntity<?> requestCancellation(Long studentModalityId) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        User student = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        StudentModality studentModality = studentModalityRepository.findById(studentModalityId)
                .orElseThrow(() -> new RuntimeException("Modalidad no encontrada"));

        if (!studentModality.getStudent().getId().equals(student.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No autorizado");
        }

        if (studentModality.getStatus() == ModalityProcessStatus.CANCELLATION_REQUESTED ||
                studentModality.getStatus() == ModalityProcessStatus.MODALITY_CANCELLED) {

            return ResponseEntity.badRequest().body(
                    Map.of(
                            "success", false,
                            "message", "La modalidad ya tiene una solicitud de cancelación"
                    )
            );
        }


        List<StudentDocument> documents =
                studentDocumentRepository.findByStudentModalityId(studentModalityId);

        boolean hasJustification = documents.stream()
                .anyMatch(doc ->
                        doc.getDocumentConfig().getModality().getId()
                                .equals(studentModality.getProgramDegreeModality().getDegreeModality().getId()));

        if (!hasJustification) {
            return ResponseEntity.badRequest().body(
                    Map.of(
                            "success", false,
                            "message", "Debe subir el documento de justificación de cancelación"
                    )
            );
        }


        studentModality.setStatus(ModalityProcessStatus.CANCELLATION_REQUESTED);
        studentModality.setUpdatedAt(LocalDateTime.now());
        studentModalityRepository.save(studentModality);


        historyRepository.save(
                ModalityProcessStatusHistory.builder()
                        .studentModality(studentModality)
                        .status(ModalityProcessStatus.CANCELLATION_REQUESTED)
                        .changeDate(LocalDateTime.now())
                        .responsible(student)
                        .observations("Solicitud de cancelación enviada por el estudiante")
                        .build()
        );

        notificationEventPublisher.publish(new CancellationRequestedEvent(studentModality.getId(), student.getId())
        );


        return ResponseEntity.ok(
                Map.of(
                        "success", true,
                        "message", "Solicitud de cancelación enviada correctamente"
                )
        );
    }


    @Transactional
    public ResponseEntity<?> approveCancellation(Long studentModalityId) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        User committeeMember = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        StudentModality modality = studentModalityRepository.findById(studentModalityId)
                .orElseThrow(() -> new RuntimeException("Modalidad no encontrada"));


        AcademicProgram academicProgram = modality.getProgramDegreeModality().getAcademicProgram();

        boolean authorized =
                programAuthorityRepository.existsByUser_IdAndAcademicProgram_IdAndRole(
                        committeeMember.getId(),
                        academicProgram.getId(),
                        ProgramRole.PROGRAM_CURRICULUM_COMMITTEE
                );

        if (!authorized) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("No tiene permiso para aprobar la cancelación de esta modalidad");
        }

        if (modality.getStatus() != ModalityProcessStatus.CANCELLATION_REQUESTED) {
            return ResponseEntity.badRequest().body(
                    Map.of(
                            "success", false,
                            "message", "La modalidad no está en estado de cancelación solicitada",
                            "currentStatus", modality.getStatus()
                    )
            );
        }

        modality.setStatus(ModalityProcessStatus.MODALITY_CANCELLED);
        modality.setUpdatedAt(LocalDateTime.now());
        studentModalityRepository.save(modality);

        historyRepository.save(
                ModalityProcessStatusHistory.builder()
                        .studentModality(modality)
                        .status(ModalityProcessStatus.MODALITY_CANCELLED)
                        .changeDate(LocalDateTime.now())
                        .responsible(committeeMember)
                        .observations("Cancelación aprobada por el comité de currículo del programa")
                        .build()
        );

        notificationEventPublisher.publish(
                new CancellationApprovedEvent(modality.getId(), committeeMember.getId())
        );

        return ResponseEntity.ok(
                Map.of(
                        "success", true,
                        "message", "La modalidad fue cancelada correctamente"
                )
        );
    }

    @Transactional
    public ResponseEntity<?> rejectCancellation(Long studentModalityId, String reason) {

        if (reason == null || reason.isBlank()) {
            return ResponseEntity.badRequest().body(
                    Map.of(
                            "success", false,
                            "message", "Debe indicar el motivo del rechazo"
                    )
            );
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        User committeeMember = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        StudentModality modality = studentModalityRepository.findById(studentModalityId)
                .orElseThrow(() -> new RuntimeException("Modalidad no encontrada"));


        AcademicProgram academicProgram =
                modality
                        .getProgramDegreeModality()
                        .getAcademicProgram();

        boolean authorized =
                programAuthorityRepository.existsByUser_IdAndAcademicProgram_IdAndRole(
                        committeeMember.getId(),
                        academicProgram.getId(),
                        ProgramRole.PROGRAM_CURRICULUM_COMMITTEE
                );

        if (!authorized) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("No tiene permiso para rechazar la cancelación de esta modalidad");
        }

        if (modality.getStatus() != ModalityProcessStatus.CANCELLATION_REQUESTED) {
            return ResponseEntity.badRequest().body(
                    Map.of(
                            "success", false,
                            "message", "Estado inválido para rechazo",
                            "currentStatus", modality.getStatus()
                    )
            );
        }

        modality.setStatus(ModalityProcessStatus.CANCELLATION_REJECTED);
        modality.setUpdatedAt(LocalDateTime.now());
        studentModalityRepository.save(modality);

        historyRepository.save(
                ModalityProcessStatusHistory.builder()
                        .studentModality(modality)
                        .status(ModalityProcessStatus.CANCELLATION_REJECTED)
                        .changeDate(LocalDateTime.now())
                        .responsible(committeeMember)
                        .observations(reason)
                        .build()
        );

        notificationEventPublisher.publish(
                new CancellationRejectedEvent(
                        modality.getId(),
                        reason,
                        committeeMember.getId()
                )
        );

        return ResponseEntity.ok(
                Map.of(
                        "success", true,
                        "message", "Solicitud de cancelación rechazada correctamente"
                )
        );
    }

    public List<CancellationList> getPendingCancellations() {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        User committeeMember = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));


        List<Long> academicProgramIds =
                programAuthorityRepository
                        .findByUser_IdAndRole(
                                committeeMember.getId(),
                                ProgramRole.PROGRAM_CURRICULUM_COMMITTEE
                        )
                        .stream()
                        .map(pa -> pa.getAcademicProgram().getId())
                        .toList();

        if (academicProgramIds.isEmpty()) {
            return List.of();
        }


        List<StudentModality> modalities =
                studentModalityRepository
                        .findByStatusAndProgramDegreeModality_AcademicProgram_IdIn(
                                ModalityProcessStatus.CANCELLATION_REQUESTED,
                                academicProgramIds
                        );

        return modalities.stream()
                .map(sm -> CancellationList.builder()
                        .studentModalityId(sm.getId())
                        .studentName(
                                sm.getStudent().getName() + " " +
                                        sm.getStudent().getLastName()
                        )
                        .email(sm.getStudent().getEmail())
                        .modalityName(
                                sm.getProgramDegreeModality()
                                        .getDegreeModality()
                                        .getName()
                        )
                        .requestDate(sm.getUpdatedAt())
                        .build()
                )
                .toList();
    }


    @Transactional
    public ResponseEntity<?> assignProjectDirector(Long studentModalityId, Long directorId) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        User committeeMember = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        StudentModality studentModality = studentModalityRepository.findById(studentModalityId)
                .orElseThrow(() -> new RuntimeException("Modalidad del estudiante no encontrada"));

        User director = userRepository.findById(directorId)
                .orElseThrow(() -> new RuntimeException("Director no encontrado"));


        Long academicProgramId =
                studentModality
                        .getProgramDegreeModality()
                        .getAcademicProgram()
                        .getId();


        boolean committeeAuthorized =
                programAuthorityRepository.existsByUser_IdAndAcademicProgram_IdAndRole(
                        committeeMember.getId(),
                        academicProgramId,
                        ProgramRole.PROGRAM_CURRICULUM_COMMITTEE
                );

        if (!committeeAuthorized) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("No tiene permiso para asignar director en este programa académico");
        }


        boolean hasDirectorRole =
                director.getRoles().stream()
                        .anyMatch(role -> role.getName().equalsIgnoreCase("PROJECT_DIRECTOR"));

        if (!hasDirectorRole) {
            return ResponseEntity.badRequest().body(
                    Map.of(
                            "success", false,
                            "message", "El usuario seleccionado no tiene rol de Director de Proyecto"
                    )
            );
        }


        boolean directorAuthorized =
                programAuthorityRepository.existsByUser_IdAndAcademicProgram_IdAndRole(
                        director.getId(),
                        academicProgramId,
                        ProgramRole.PROJECT_DIRECTOR
                );

        if (!directorAuthorized) {
            return ResponseEntity.badRequest().body(
                    Map.of(
                            "success", false,
                            "message", "El director no pertenece a este programa académico"
                    )
            );
        }


        if (studentModality.getStatus() == ModalityProcessStatus.MODALITY_SELECTED ||
                studentModality.getStatus() == ModalityProcessStatus.CORRECTIONS_REQUESTED_PROGRAM_HEAD) {

            return ResponseEntity.badRequest().body(
                    Map.of(
                            "success", false,
                            "message", "La modalidad aún no está lista para asignar Director",
                            "currentStatus", studentModality.getStatus()
                    )
            );
        }


        User previousDirector = studentModality.getProjectDirector();

        studentModality.setProjectDirector(director);
        studentModality.setUpdatedAt(LocalDateTime.now());
        studentModalityRepository.save(studentModality);

        String observation =
                previousDirector == null
                        ? "Director asignado: " + director.getEmail()
                        : "Cambio de Director: " +
                        previousDirector.getEmail() +
                        " → " +
                        director.getEmail();

        historyRepository.save(
                ModalityProcessStatusHistory.builder()
                        .studentModality(studentModality)
                        .status(studentModality.getStatus())
                        .changeDate(LocalDateTime.now())
                        .responsible(committeeMember)
                        .observations(observation)
                        .build()
        );

        notificationEventPublisher.publish(
                new DirectorAssignedEvent(
                        studentModality.getId(),
                        director.getId(),
                        committeeMember.getId()
                )
        );

        return ResponseEntity.ok(
                Map.of(
                        "success", true,
                        "studentModalityId", studentModality.getId(),
                        "directorAssigned", director.getEmail(),
                        "message", "Director asignado correctamente a la modalidad"
                )
        );
    }

    @Transactional
    public ResponseEntity<?> scheduleDefense(Long studentModalityId, ScheduleDefenseDTO request) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        User committeeMember = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        StudentModality studentModality = studentModalityRepository.findById(studentModalityId)
                .orElseThrow(() -> new RuntimeException("Modalidad no encontrada"));


        Long academicProgramId =
                studentModality
                        .getProgramDegreeModality()
                        .getAcademicProgram()
                        .getId();


        boolean authorized =
                programAuthorityRepository.existsByUser_IdAndAcademicProgram_IdAndRole(
                        committeeMember.getId(),
                        academicProgramId,
                        ProgramRole.PROGRAM_CURRICULUM_COMMITTEE
                );

        if (!authorized) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("No tiene permiso para programar sustentaciones en este programa académico");
        }


        if (studentModality.getStatus() != ModalityProcessStatus.PROPOSAL_APPROVED) {
            return ResponseEntity.badRequest().body(
                    Map.of(
                            "success", false,
                            "message", "La modalidad no se encuentra en estado válido para programar sustentación",
                            "currentStatus", studentModality.getStatus()
                    )
            );
        }


        if (request.getDefenseDate() == null ||
                request.getDefenseLocation() == null ||
                request.getDefenseLocation().isBlank()) {

            return ResponseEntity.badRequest().body(
                    Map.of(
                            "success", false,
                            "message", "Debe ingresar fecha y lugar válidos para la sustentación"
                    )
            );
        }


        studentModality.setDefenseDate(request.getDefenseDate());
        studentModality.setDefenseLocation(request.getDefenseLocation());
        studentModality.setStatus(ModalityProcessStatus.DEFENSE_SCHEDULED);
        studentModality.setUpdatedAt(LocalDateTime.now());

        studentModalityRepository.save(studentModality);

        historyRepository.save(
                ModalityProcessStatusHistory.builder()
                        .studentModality(studentModality)
                        .status(ModalityProcessStatus.DEFENSE_SCHEDULED)
                        .changeDate(LocalDateTime.now())
                        .responsible(committeeMember)
                        .observations(
                                "Sustentación programada para el "
                                        + request.getDefenseDate()
                                        + " en "
                                        + request.getDefenseLocation()
                        )
                        .build()
        );

        notificationEventPublisher.publish(
                new DefenseScheduledEvent(
                        studentModality.getId(),
                        request.getDefenseDate(),
                        request.getDefenseLocation(),
                        committeeMember.getId()
                )
        );

        return ResponseEntity.ok(
                Map.of(
                        "success", true,
                        "studentModalityId", studentModalityId,
                        "defenseDate", request.getDefenseDate(),
                        "defenseLocation", request.getDefenseLocation(),
                        "newStatus", ModalityProcessStatus.DEFENSE_SCHEDULED,
                        "message", "Sustentación programada correctamente"
                )
        );
    }

    @Transactional
    public ResponseEntity<?> registerFinalDefenseEvaluation(Long studentModalityId, ScheduleDefenseDTO request) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        User committeeMember = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        StudentModality studentModality = studentModalityRepository.findById(studentModalityId)
                .orElseThrow(() -> new RuntimeException("Modalidad no encontrada"));


        Long academicProgramId =
                studentModality
                        .getProgramDegreeModality()
                        .getAcademicProgram()
                        .getId();


        boolean authorized =
                programAuthorityRepository.existsByUser_IdAndAcademicProgram_IdAndRole(
                        committeeMember.getId(),
                        academicProgramId,
                        ProgramRole.PROGRAM_CURRICULUM_COMMITTEE
                );

        if (!authorized) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("No tiene permiso para registrar la evaluación final de esta modalidad");
        }


        if (studentModality.getStatus() != ModalityProcessStatus.DEFENSE_SCHEDULED &&
                studentModality.getStatus() != ModalityProcessStatus.DEFENSE_COMPLETED) {

            return ResponseEntity.badRequest().body(
                    Map.of(
                            "success", false,
                            "message", "La modalidad no se encuentra en estado válido para evaluación final",
                            "currentStatus", studentModality.getStatus()
                    )
            );
        }


        if (request.getObservations() == null || request.getObservations().isBlank()) {
            return ResponseEntity.badRequest().body(
                    Map.of(
                            "success", false,
                            "message", "Debe registrar observaciones del resultado final"
                    )
            );
        }


        if (!request.isApproved()) {

            studentModality.setStatus(ModalityProcessStatus.GRADED_FAILED);
            studentModality.setAcademicDistinction(AcademicDistinction.NO_DISTINCTION);
            studentModality.setUpdatedAt(LocalDateTime.now());

            studentModalityRepository.save(studentModality);

            historyRepository.save(
                    ModalityProcessStatusHistory.builder()
                            .studentModality(studentModality)
                            .status(ModalityProcessStatus.GRADED_FAILED)
                            .changeDate(LocalDateTime.now())
                            .responsible(committeeMember)
                            .observations("Modalidad reprobada. " + request.getObservations())
                            .build()
            );

            notificationEventPublisher.publish(
                    new FinalDefenseResultEvent(
                            studentModality.getId(),
                            ModalityProcessStatus.GRADED_FAILED,
                            AcademicDistinction.NO_DISTINCTION,
                            request.getObservations(),
                            committeeMember.getId()
                    )
            );

            return ResponseEntity.ok(
                    Map.of(
                            "success", true,
                            "approved", false,
                            "finalStatus", ModalityProcessStatus.GRADED_FAILED,
                            "message", "La modalidad fue REPROBADA"
                    )
            );
        }


        AcademicDistinction distinction =
                request.getAcademicDistinction() != null
                        ? request.getAcademicDistinction()
                        : AcademicDistinction.NO_DISTINCTION;

        studentModality.setStatus(ModalityProcessStatus.GRADED_APPROVED);
        studentModality.setAcademicDistinction(distinction);
        studentModality.setUpdatedAt(LocalDateTime.now());

        studentModalityRepository.save(studentModality);

        historyRepository.save(
                ModalityProcessStatusHistory.builder()
                        .studentModality(studentModality)
                        .status(ModalityProcessStatus.GRADED_APPROVED)
                        .changeDate(LocalDateTime.now())
                        .responsible(committeeMember)
                        .observations(
                                "Modalidad aprobada. Mención: " + distinction.name() +
                                        ". " + request.getObservations()
                        )
                        .build()
        );

        notificationEventPublisher.publish(
                new FinalDefenseResultEvent(
                        studentModality.getId(),
                        ModalityProcessStatus.GRADED_APPROVED,
                        distinction,
                        request.getObservations(),
                        committeeMember.getId()
                )
        );

        return ResponseEntity.ok(
                Map.of(
                        "success", true,
                        "approved", true,
                        "academicDistinction", distinction,
                        "finalStatus", ModalityProcessStatus.GRADED_APPROVED,
                        "message", "Modalidad aprobada correctamente"
                )
        );
    }

    public ResponseEntity<?> getFinalDefenseResult(Long studentModalityId) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        StudentModality studentModality = studentModalityRepository.findById(studentModalityId)
                .orElseThrow(() -> new RuntimeException("Modalidad no encontrada"));


        Long academicProgramId =
                studentModality
                        .getProgramDegreeModality()
                        .getAcademicProgram()
                        .getId();


        boolean authorized =
                programAuthorityRepository
                        .existsByUser_IdAndAcademicProgram_IdAndRoleIn(
                                user.getId(),
                                academicProgramId,
                                List.of(
                                        ProgramRole.PROGRAM_HEAD,
                                        ProgramRole.PROGRAM_CURRICULUM_COMMITTEE
                                )
                        );

        if (!authorized) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("No tiene permiso para consultar el resultado final de esta modalidad");
        }


        if (studentModality.getStatus() != ModalityProcessStatus.GRADED_APPROVED &&
                studentModality.getStatus() != ModalityProcessStatus.GRADED_FAILED) {

            return ResponseEntity.badRequest().body(
                    Map.of(
                            "success", false,
                            "message", "La modalidad aún no tiene un resultado final registrado",
                            "currentStatus", studentModality.getStatus()
                    )
            );
        }

        ModalityProcessStatus finalStatus = studentModality.getStatus();

        ModalityProcessStatusHistory history =
                historyRepository
                        .findTopByStudentModalityAndStatusOrderByChangeDateDesc(
                                studentModality,
                                finalStatus
                        )
                        .orElseThrow(() ->
                                new RuntimeException("No se encontró historial de evaluación final")
                        );

        return ResponseEntity.ok(
                FinalDefenseResponse.builder()
                        .studentModalityId(studentModality.getId())
                        .studentName(
                                studentModality.getStudent().getName() + " " +
                                        studentModality.getStudent().getLastName()
                        )
                        .studentEmail(studentModality.getStudent().getEmail())
                        .finalStatus(finalStatus)
                        .approved(finalStatus == ModalityProcessStatus.GRADED_APPROVED)
                        .academicDistinction(studentModality.getAcademicDistinction())
                        .observations(history.getObservations())
                        .evaluationDate(history.getChangeDate())
                        .evaluatedBy(
                                history.getResponsible() != null
                                        ? history.getResponsible().getName()
                                        : "Comité de currículo de programa"
                        )
                        .build()
        );
    }

    public ResponseEntity<?> getMyFinalDefenseResult() {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        User student = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        StudentModality studentModality = studentModalityRepository
                .findByStudent(student)
                .orElseThrow(() -> new RuntimeException(
                        "No se encontró una modalidad asociada al estudiante"
                ));

        if (studentModality.getStatus() != ModalityProcessStatus.GRADED_APPROVED &&
                studentModality.getStatus() != ModalityProcessStatus.GRADED_FAILED) {

            return ResponseEntity.ok(
                    Map.of(
                            "hasResult", false,
                            "message", "Tu modalidad aún no tiene un resultado final"
                    )
            );
        }

        ModalityProcessStatus finalStatus = studentModality.getStatus();

        ModalityProcessStatusHistory history = historyRepository
                .findTopByStudentModalityAndStatusOrderByChangeDateDesc(
                        studentModality,
                        finalStatus
                )
                .orElseThrow(() -> new RuntimeException(
                        "No se encontró historial de evaluación final"
                ));

        return ResponseEntity.ok(
                FinalDefenseResponse.builder()
                        .studentModalityId(studentModality.getId())
                        .studentName(student.getName() + " " + student.getLastName())
                        .studentEmail(student.getEmail())
                        .finalStatus(finalStatus)
                        .approved(finalStatus == ModalityProcessStatus.GRADED_APPROVED)
                        .academicDistinction(studentModality.getAcademicDistinction())
                        .observations(history.getObservations())
                        .evaluationDate(history.getChangeDate())
                        .evaluatedBy(
                                history.getResponsible() != null
                                        ? history.getResponsible().getName()
                                        : "Comité de currículo de programa"
                        )
                        .build()
        );
    }

    public List<ProjectDirectorResponse> getProjectDirectors() {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));


        List<ProgramAuthority> committeeAuthorities = programAuthorityRepository
                .findByUser_IdAndRole(currentUser.getId(), ProgramRole.PROGRAM_CURRICULUM_COMMITTEE);

        if (committeeAuthorities.isEmpty()) {
            throw new RuntimeException("El usuario no tiene el rol de PROGRAM_CURRICULUM_COMMITTEE");
        }


        Set<Long> userProgramIds = committeeAuthorities.stream()
                .map(authority -> authority.getAcademicProgram().getId())
                .collect(Collectors.toSet());


        List<com.SIGMA.USCO.Users.Entity.ProgramAuthority> projectDirectorAuthorities = programAuthorityRepository
                .findByAcademicProgram_IdAndRole(userProgramIds.iterator().next(),
                        ProgramRole.PROJECT_DIRECTOR
                );


        return projectDirectorAuthorities.stream()
                .map(authority -> new ProjectDirectorResponse(
                        authority.getUser().getId(),
                        authority.getUser().getName(),
                        authority.getUser().getLastName(),
                        authority.getUser().getEmail()
                ))
                .distinct()
                .collect(Collectors.toList());
    }

}
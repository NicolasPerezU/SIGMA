package com.SIGMA.USCO.Modalities.Controller;

import com.SIGMA.USCO.Modalities.Entity.enums.ModalityProcessStatus;
import com.SIGMA.USCO.Modalities.dto.*;
import com.SIGMA.USCO.Modalities.dto.response.ProjectDirectorResponse;
import com.SIGMA.USCO.Modalities.service.ModalityService;
import com.SIGMA.USCO.documents.entity.StudentDocument;
import com.SIGMA.USCO.documents.service.DocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequestMapping("/modalities")
@RequiredArgsConstructor
public class ModalityController {

    private final ModalityService modalityService;
    private final DocumentService documentService;

    @PostMapping("/create")
    @PreAuthorize("hasAuthority('PERM_CREATE_MODALITY') or hasAuthority('PERM_UPDATE_MODALITY')")
    public ResponseEntity<?> createModality(@RequestBody ModalityDTO request) {
        return modalityService.createModality(request);
    }
    @PutMapping("/update/{modalityId}")
    @PreAuthorize("hasAuthority('PERM_CREATE_MODALITY') or hasAuthority('PERM_UPDATE_MODALITY')")
    public ResponseEntity<?> updateModality(@PathVariable Long modalityId, @RequestBody ModalityDTO request) {
        return modalityService.updateModality(modalityId, request);
    }

    @PutMapping("delete/{modalityId}")
    @PreAuthorize("hasAuthority('PERM_DESACTIVE_MODALITY')")
    public ResponseEntity<?> deactivateModality(@PathVariable Long modalityId) {
        return modalityService.desactiveModality(modalityId);
    }



    @PostMapping("/requirements/create/{modalityId}")
    @PreAuthorize("hasAuthority('PERM_CREATE_MODALITY') or hasAuthority('PERM_UPDATE_MODALITY')")
    public ResponseEntity<?> createModalityRequirements(@PathVariable Long modalityId, @RequestBody List<RequirementDTO> requirements) {
        return modalityService.createModalityRequirements(modalityId, requirements);
    }

    @PutMapping("/requirements/{modalityId}/update")
    @PreAuthorize("hasAuthority('PERM_CREATE_MODALITY') or hasAuthority('PERM_UPDATE_MODALITY')")
    public ResponseEntity<?> updateModalityRequirements(@PathVariable Long ModalityId,@RequestBody List<RequirementDTO> request) {
        return modalityService.updateModalityRequirements(ModalityId, request);
    }

    @GetMapping("/{modalityId}/requirements")
    public ResponseEntity<List<RequirementDTO>> listRequirements(@PathVariable Long modalityId, @RequestParam(required = false) Boolean active) {
        return modalityService.getModalityRequirements(modalityId, active);
    }

    @PutMapping("/requirements/delete/{requirementId}")
    @PreAuthorize("hasAuthority('PERM_DELETE_MODALITY_REQUIREMENT')")
    public ResponseEntity<?> desactiveRequirements(@PathVariable Long requirementId) {
        return modalityService.deleteRequirement(requirementId);
    }


    @GetMapping
    public ResponseEntity<?> getAllModalities() {
        return modalityService.getAllModalities();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getModalityById(@PathVariable Long id) {
        return modalityService.getModalityDetail(id);
    }

    @PostMapping(
            value = "/{studentModalityId}/documents/{requiredDocumentId}",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<?> uploadDocument(
            @PathVariable Long studentModalityId,
            @PathVariable Long requiredDocumentId,
            @RequestPart("file") MultipartFile file
    ) throws IOException {

        return modalityService.uploadRequiredDocument(
                studentModalityId,
                requiredDocumentId,
                file
        );
    }



    @PostMapping("/{modalityId}/start")
    public ResponseEntity<?> startModality(@PathVariable Long modalityId) {
        return modalityService.startStudentModality(modalityId);
    }

    @GetMapping("/{id}/validate-documents")
    public ResponseEntity<?> validateDocuments(@PathVariable Long id) {
        return modalityService.validateAllDocumentsUploaded(id);
    }

    @GetMapping("/{studentModalityId}/documents")
    @PreAuthorize("hasAuthority('PERM_REVIEW_DOCUMENTS')")
    public ResponseEntity<?> listStudentDocuments(@PathVariable Long studentModalityId) {
        return modalityService.getStudentDocuments(studentModalityId);
    }

    @GetMapping("/student/{studentDocumentId}/view")
    @PreAuthorize("hasAuthority('PERM_VIEW_DOCUMENTS')")
    public ResponseEntity<?> viewStudentDocument(@PathVariable Long studentDocumentId) throws MalformedURLException {
        return modalityService.viewStudentDocument(studentDocumentId);
    }
    @PutMapping("/documents/{studentDocumentId}/review")
    @PreAuthorize("hasAuthority('PERM_REVIEW_DOCUMENTS')")
    public ResponseEntity<?> reviewDocument(
            @PathVariable Long studentDocumentId,@RequestBody DocumentReviewDTO request) {
        return modalityService.reviewStudentDocument(studentDocumentId, request);
    }
    @PostMapping("/{studentModalityId}/approve-secretary")
    @PreAuthorize("hasAuthority('PERM_APPROVE_MODALITY')")
    public ResponseEntity<?> approveBySecretary(@PathVariable Long studentModalityId) {
        return modalityService.approveModalityBySecretary(studentModalityId);
    }
    @PostMapping("/{studentModalityId}/approve-council")
    @PreAuthorize("hasAuthority('PERM_APPROVE_MODALITY')")
    public ResponseEntity<?> approveByCouncil(@PathVariable Long studentModalityId) {
        return modalityService.approveModalityByCouncil(studentModalityId);
    }
    @PostMapping("/documents/{studentDocumentId}/review-council")
    @PreAuthorize("hasAuthority('PERM_REVIEW_DOCUMENTS')")
    public ResponseEntity<?> reviewDocumentCouncil(@PathVariable Long studentDocumentId, @RequestBody DocumentReviewDTO request) {
        return modalityService.reviewStudentDocumentByCouncil(studentDocumentId, request);
    }
    @GetMapping("/students")
    @PreAuthorize("hasAuthority('PERM_VIEW_ALL_MODALITIES')")
    public ResponseEntity<?> listAllModalitiesForSecretary(@RequestParam(required = false)
                                                               List<ModalityProcessStatus> statuses, @RequestParam(required = false)
    String name) {
        return modalityService.getAllStudentModalitiesForSecretary(statuses, name);
    }

    @GetMapping("/students/{studentModalityId}")
    @PreAuthorize("hasAuthority('PERM_VIEW_ALL_MODALITIES')")
    public ResponseEntity<?> getModalityDetailForSecretary(@PathVariable Long studentModalityId) {
        return modalityService.getStudentModalityDetailForSecretary(studentModalityId);
    }
    @PostMapping("/{studentModalityId}/cancellation/approve")
    @PreAuthorize("hasAuthority('PERM_APPROVE_CANCELLATION')")
    public ResponseEntity<?> approveCancellation(@PathVariable Long studentModalityId) {
        return modalityService.approveCancellation(studentModalityId);
    }

    @PostMapping("/{studentModalityId}/cancellation/reject")
    @PreAuthorize("hasAuthority('PERM_REJECT_CANCELLATION')")
    public ResponseEntity<?> rejectCancellation(@PathVariable Long studentModalityId, @RequestBody String reason
    ) {
        return modalityService.rejectCancellation(studentModalityId, reason);
    }

    @GetMapping("/cancellation-request")
    @PreAuthorize("hasAuthority('PERM_VIEW_CANCELLATIONS')")
    public ResponseEntity<List<CancellationList>> getPendingCancellations() {

        List<CancellationList> cancellations =
                modalityService.getPendingCancellations();

        return ResponseEntity.ok(cancellations);
    }

    @GetMapping("/cancellation/document/{studentModalityId}")
    @PreAuthorize("hasAuthority('PERM_VIEW_CANCELLATIONS')")
    public ResponseEntity<Resource> getCancellationDocument(@PathVariable Long studentModalityId) throws MalformedURLException {

        StudentDocument document = documentService.getDocumentCancellation(studentModalityId);

        Path filePath = Paths.get(document.getFilePath());
        Resource resource = new UrlResource(filePath.toUri());

        if (!resource.exists() || !resource.isReadable()) {
            throw new RuntimeException("No se pudo leer el archivo");
        }

        return ResponseEntity.ok().contentType(MediaType.APPLICATION_PDF)
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=\"" + document.getFileName() + "\""
                )
                .body(resource);
    }

    @PostMapping("/{studentModalityId}/assign-director/{directorId}")
    @PreAuthorize("hasAuthority('PERM_ASSIGN_PROJECT_DIRECTOR')")
    public ResponseEntity<?> assignProjectDirector(@PathVariable Long studentModalityId, @PathVariable Long directorId) {
        return modalityService.assignProjectDirector(studentModalityId, directorId);
    }

    @PostMapping("/{studentModalityId}/schedule-defense")
    @PreAuthorize("hasAuthority('PERM_SCHEDULE_DEFENSE')")
    public ResponseEntity<?> scheduleDefense(@PathVariable Long studentModalityId, @RequestBody ScheduleDefenseDTO request) {
        return modalityService.scheduleDefense(studentModalityId, request);
    }
    @PostMapping("/{studentModalityId}/final-evaluation")
    @PreAuthorize("hasAuthority('PERM_SCHEDULE_DEFENSE')")
    public ResponseEntity<?> registerFinalDefenseEvaluation(@PathVariable Long studentModalityId, @RequestBody ScheduleDefenseDTO request) {
        return modalityService.registerFinalDefenseEvaluation(studentModalityId, request);
    }

    @GetMapping("/project-directors")
    @PreAuthorize("hasAuthority('PERM_VIEW_PROJECT_DIRECTOR')")
    public ResponseEntity<List<ProjectDirectorResponse>> getProjectDirectors() {
        return ResponseEntity.ok(modalityService.getProjectDirectors());
    }

    @GetMapping("/{studentModalityId}/final-evaluation")
    @PreAuthorize("hasAuthority('PERM_VIEW_FINAL_DEFENSE_RESULT')")
    public ResponseEntity<?> getFinalDefenseResult(@PathVariable Long studentModalityId) {
        return modalityService.getFinalDefenseResult(studentModalityId);
    }

    @GetMapping("/final-evaluation/my-result")
    public ResponseEntity<?> getMyFinalDefenseResult() {
        return modalityService.getMyFinalDefenseResult();
    }

}

package com.SIGMA.USCO.Modalities.Controller;

import com.SIGMA.USCO.Modalities.dto.*;
import com.SIGMA.USCO.Modalities.service.ModalityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

@RestController
@RequestMapping("/modalities")
@RequiredArgsConstructor
public class ModalityController {

    private final ModalityService modalityService;

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
    public ResponseEntity<?> listAllModalitiesForSecretary() {
        return modalityService.getAllStudentModalitiesForSecretary();
    }

    @GetMapping("/students/{studentModalityId}")
    @PreAuthorize("hasAuthority('PERM_VIEW_ALL_MODALITIES')")
    public ResponseEntity<?> getModalityDetailForSecretary(@PathVariable Long studentModalityId) {
        return modalityService.getStudentModalityDetailForSecretary(studentModalityId);
    }













}

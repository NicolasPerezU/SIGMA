package com.SIGMA.USCO.Modalities.Controller;

import com.SIGMA.USCO.Modalities.dto.DocumentReview;
import com.SIGMA.USCO.Modalities.dto.ModalityRequest;
import com.SIGMA.USCO.Modalities.dto.RequirementsRequest;
import com.SIGMA.USCO.Modalities.dto.UpdateModalityRequirementRequest;
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
    public ResponseEntity<?> createModality(@RequestBody ModalityRequest request) {
        return modalityService.createModality(request);
    }
    @PutMapping("/update/{modalityId}")
    @PreAuthorize("hasAuthority('PERM_CREATE_MODALITY') or hasAuthority('PERM_UPDATE_MODALITY')")
    public ResponseEntity<?> updateModality(@PathVariable Long modalityId, @RequestBody ModalityRequest request) {
        return modalityService.updateModality(modalityId, request);
    }

    @PostMapping("/requirements/create/{modalityId}")
    @PreAuthorize("hasAuthority('PERM_CREATE_MODALITY') or hasAuthority('PERM_UPDATE_MODALITY')")
    public ResponseEntity<?> createModalityRequirements(@PathVariable Long modalityId, @RequestBody List<RequirementsRequest> requirements) {
        return modalityService.createModalityRequirements(modalityId, requirements);
    }

    @PutMapping("/requirements/update")
    @PreAuthorize("hasAuthority('PERM_CREATE_MODALITY') or hasAuthority('PERM_UPDATE_MODALITY')")
    public ResponseEntity<?> updateModalityRequirements(@RequestBody UpdateModalityRequirementRequest request) {
        return modalityService.updateModalityRequirements(request);
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
            @PathVariable Long studentDocumentId,@RequestBody DocumentReview request) {
        return modalityService.reviewStudentDocument(studentDocumentId, request);
    }



}

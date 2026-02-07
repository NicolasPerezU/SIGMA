package com.SIGMA.USCO.documents.controller;


import com.SIGMA.USCO.documents.dto.RequiredDocumentDTO;
import com.SIGMA.USCO.documents.service.DocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/required-documents")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService documentService;

    @PostMapping("/create")
    @PreAuthorize("hasAuthority('PERM_CREATE_REQUIRED_DOCUMENT') or hasAuthority('PERM_UPDATE_REQUIRED_DOCUMENT')")
    public ResponseEntity<?> createRequiredDocument(@RequestBody RequiredDocumentDTO request) {
        return documentService.createRequiredDocument(request);
    }

    @PutMapping("/update/{documentId}")
    @PreAuthorize("hasAuthority('PERM_UPDATE_REQUIRED_DOCUMENT')")
    public ResponseEntity<?> updateRequiredDocument(@PathVariable Long documentId, @RequestBody RequiredDocumentDTO request) {
        return documentService.updateRequiredDocument(documentId, request);
    }

    @PutMapping("/delete/{documentId}")
    @PreAuthorize("hasAuthority('PERM_DELETE_REQUIRED_DOCUMENT')")
    public ResponseEntity<?> deleteRequiredDocument(@PathVariable Long documentId) {
        return documentService.deleteRequiredDocument(documentId);
    }

    @GetMapping("/modality/{modalityId}")
    public ResponseEntity<List<RequiredDocumentDTO>>
    getByModality(@PathVariable Long modalityId) {
        return documentService.getRequiredDocumentsByModality(modalityId);
    }

    @GetMapping("/modality/{modalityId}/filter")
    @PreAuthorize("hasAuthority('PERM_VIEW_REQUIRED_DOCUMENT')")
    public ResponseEntity<List<RequiredDocumentDTO>>
    getByModalityAndStatus(@PathVariable Long modalityId, @RequestParam boolean active) {
        return documentService.getRequiredDocumentsByModalityAndStatus(modalityId, active);
    }
}

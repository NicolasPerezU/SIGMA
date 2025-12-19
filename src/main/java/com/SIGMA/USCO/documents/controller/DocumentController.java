package com.SIGMA.USCO.documents.controller;


import com.SIGMA.USCO.documents.dto.RequiredDocumentDTO;
import com.SIGMA.USCO.documents.service.DocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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



}

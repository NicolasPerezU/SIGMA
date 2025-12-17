package com.SIGMA.USCO.documents.service;

import com.SIGMA.USCO.Modalities.Repository.DegreeModalityRepository;
import com.SIGMA.USCO.documents.dto.RequiredDocumentRequest;
import com.SIGMA.USCO.documents.entity.RequiredDocument;
import com.SIGMA.USCO.documents.repository.RequiredDocumentRepository;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class DocumentService {

    private final RequiredDocumentRepository requiredDocumentRepository;
    private final DegreeModalityRepository degreeModalityRepository;

    public ResponseEntity<?> createRequiredDocument(RequiredDocumentRequest request) {

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
    public ResponseEntity<?> updateRequiredDocument(Long documentId, RequiredDocumentRequest request) {

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


}

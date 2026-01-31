package com.SIGMA.USCO.Users.controller;

import com.SIGMA.USCO.Modalities.service.ModalityService;
import com.SIGMA.USCO.Users.dto.request.StudentProfileRequest;
import com.SIGMA.USCO.Users.service.StudentService;
import com.SIGMA.USCO.documents.service.DocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/students")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;
    private final ModalityService modalityService;
    private final DocumentService documentService;

    @PostMapping("/profile")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<?> updateStudentProfile(@RequestBody StudentProfileRequest request){
        return studentService.updateStudentProfile(request);
    }

    @GetMapping("/profile")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<?> getStudentInfo() {
        return studentService.getStudentProfile();
    }

    @GetMapping("/modality/current")
    public ResponseEntity<?> getCurrentStudentModality() {
        return modalityService.getCurrentStudentModality();
    }

    @GetMapping("/documents/{studentDocumentId}/history")
    public ResponseEntity<?> getDocumentHistory(@PathVariable Long studentDocumentId) {
        return documentService.getDocumentHistory(studentDocumentId);
    }

    @PostMapping("/{studentModalityId}/request-cancellation")
    public ResponseEntity<?> requestCancellation(@PathVariable Long studentModalityId) {
        return modalityService.requestCancellation(studentModalityId);
    }

    @GetMapping("/my-documents")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<?> getMyDocuments() {
        return studentService.getMyDocuments();
    }

    @PostMapping("/cancellation-document/{studentModalityId}")
    public ResponseEntity<?> uploadCancellationDocument(@PathVariable Long studentModalityId, @RequestParam("file") MultipartFile file) {
        documentService.uploadCancellationDocument(studentModalityId, file);
        return ResponseEntity.ok(
                Map.of(
                        "success", true,
                        "message", "Documento de justificación cargado correctamente"
                )
        );
    }

}

package com.SIGMA.USCO.Users.controller;

import com.SIGMA.USCO.Modalities.service.ModalityService;
import com.SIGMA.USCO.Users.Entity.User;
import com.SIGMA.USCO.Users.dto.StudentProfileDTO;
import com.SIGMA.USCO.Users.service.StudentService;
import com.SIGMA.USCO.documents.service.DocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/students")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;
    private final ModalityService modalityService;
    private final DocumentService documentService;

    @PostMapping("/profile")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<?> updateStudentProfile(@RequestBody StudentProfileDTO request){
        return studentService.updateStudentProfile(request);
    }

    @GetMapping("/modality/current")
    public ResponseEntity<?> getCurrentStudentModality() {
        return modalityService.getCurrentStudentModality();
    }

    @GetMapping("/documents/{studentDocumentId}/history")
    public ResponseEntity<?> getDocumentHistory(@PathVariable Long studentDocumentId) {
        return documentService.getDocumentHistory(studentDocumentId);
    }



}

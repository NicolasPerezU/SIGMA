package com.SIGMA.USCO.Users.service;

import com.SIGMA.USCO.Modalities.Entity.StudentModality;
import com.SIGMA.USCO.Modalities.Repository.StudentModalityRepository;
import com.SIGMA.USCO.Users.Entity.StudentProfile;
import com.SIGMA.USCO.Users.Entity.User;
import com.SIGMA.USCO.Users.dto.request.StudentProfileRequest;
import com.SIGMA.USCO.Users.dto.response.StudentResponse;
import com.SIGMA.USCO.Users.repository.StudentProfileRepository;
import com.SIGMA.USCO.Users.repository.UserRepository;
import com.SIGMA.USCO.documents.entity.StudentDocument;
import com.SIGMA.USCO.documents.repository.StudentDocumentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final UserRepository userRepository;
    private final StudentProfileRepository studentProfileRepository;
    private final StudentModalityRepository studentModalityRepository;
    private final StudentDocumentRepository studentDocumentRepository;

    public ResponseEntity<?> updateStudentProfile(StudentProfileRequest request) {

        
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        Optional<User> userOpt = userRepository.findByEmail(email);


        if (userOpt.isEmpty()) {
            return ResponseEntity.status(404).body("Usuario no encontrado");
        }

        User user = userOpt.get();
        Optional<StudentProfile> studentProfileOpt = studentProfileRepository.findByUserId(user.getId());
        StudentProfile studentProfile;


        if (studentProfileOpt.isPresent()) {
            studentProfile = studentProfileOpt.get();
        } else {
            studentProfile = new StudentProfile();
            studentProfile.setUser(user);
        }


        if (request.getSemester() < 1 || request.getSemester() > 10) {
            return ResponseEntity.badRequest().body("El semestre debe estar entre 1 y 10.");
        }


        if (request.getGpa() < 0.0 || request.getGpa() > 5.0) {
            return ResponseEntity.badRequest().body("La nota promedio debe estar entre 0.0 y 5.0.");
        }


        if (request.getApprovedCredits() > 180) {
            return ResponseEntity.badRequest().body("El número máximo de créditos aprobados es 180.");
        }


        studentProfile.setStudentCode(request.getStudentCode());
        studentProfile.setGpa(request.getGpa());
        studentProfile.setApprovedCredits(request.getApprovedCredits());
        studentProfile.setSemester(request.getSemester());

        studentProfileRepository.save(studentProfile);

        return ResponseEntity.ok("Datos de perfil de estudiante actualizados correctamente");
    }

    public ResponseEntity<?> getStudentProfile() {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(404).body("Usuario no encontrado");
        }
        User user = userOpt.get();
        Optional<StudentProfile> profileOpt = studentProfileRepository.findByUserId(user.getId());

        StudentResponse response = StudentResponse.builder()
                .name(user.getName())
                .lastname(user.getLastName())
                .email(user.getEmail())
                .approvedCredits(profileOpt.map(StudentProfile::getApprovedCredits).orElse(null))
                .gpa(profileOpt.map(StudentProfile::getGpa).orElse(null))
                .semester(profileOpt.map(StudentProfile::getSemester).orElse(null))
                .studentCode(profileOpt.map(StudentProfile::getStudentCode).orElse(null))
                .build();
        return ResponseEntity.ok(response);


    }

    public ResponseEntity<?> getMyDocuments(){

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty()) {
            return ResponseEntity.status(404).body("Usuario no encontrado");
        }
        User currentUser = user.get();
        Optional<StudentModality> modalityOpt =
                studentModalityRepository.findTopByStudentIdOrderByUpdatedAtDesc(currentUser.getId());

        if (modalityOpt.isEmpty()) {
            return ResponseEntity.status(404).body("No se encontró ninguna modalidad asociada al estudiante");
        }

        Long studentModalityId = modalityOpt.get().getId();

        List<StudentDocument> documents = studentDocumentRepository.findByStudentModalityId(studentModalityId);



        List<Map<String, Object>> response = documents.stream()
                .map(doc -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("notes", doc.getNotes());
                    map.put("filePath", doc.getFilePath());
                    map.put("studentDocumentId", doc.getId());
                    map.put("uploadedAt", doc.getUploadDate());
                    map.put("documentName", doc.getDocumentConfig().getDocumentName());
                    map.put("mandatory", doc.getDocumentConfig().isMandatory());
                    map.put("status", doc.getStatus());
                    return map;
                })
                .toList();


        return ResponseEntity.ok(response);



    }

}

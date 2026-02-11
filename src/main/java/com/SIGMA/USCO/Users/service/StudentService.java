package com.SIGMA.USCO.Users.service;

import com.SIGMA.USCO.Modalities.Entity.StudentModality;
import com.SIGMA.USCO.Modalities.Repository.StudentModalityRepository;
import com.SIGMA.USCO.academic.entity.AcademicProgram;
import com.SIGMA.USCO.academic.entity.Faculty;
import com.SIGMA.USCO.academic.entity.StudentProfile;
import com.SIGMA.USCO.Users.Entity.User;
import com.SIGMA.USCO.academic.dto.StudentProfileRequest;
import com.SIGMA.USCO.Users.dto.response.StudentResponse;
import com.SIGMA.USCO.academic.repository.AcademicProgramRepository;
import com.SIGMA.USCO.academic.repository.FacultyRepository;
import com.SIGMA.USCO.academic.repository.StudentProfileRepository;
import com.SIGMA.USCO.Users.repository.UserRepository;
import com.SIGMA.USCO.documents.entity.StudentDocument;
import com.SIGMA.USCO.documents.repository.StudentDocumentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
    private final FacultyRepository facultyRepository;
    private final AcademicProgramRepository academicProgramRepository;

    public ResponseEntity<?> updateStudentProfile(StudentProfileRequest request) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        StudentProfile studentProfile = studentProfileRepository
                .findByUserId(user.getId())
                .orElseGet(() -> {
                    StudentProfile sp = new StudentProfile();
                    sp.setUser(user);
                    return sp;
                });



        if (request.getSemester() < 1 || request.getSemester() > 10) {
            return ResponseEntity.badRequest().body("El semestre debe estar entre 1 y 10.");
        }

        if (request.getGpa() < 0.0 || request.getGpa() > 5.0) {
            return ResponseEntity.badRequest().body("El promedio debe estar entre 0.0 y 5.0.");
        }

        if (request.getApprovedCredits() < 0) {
            return ResponseEntity.badRequest().body("Los créditos aprobados no pueden ser negativos.");
        }



        Faculty faculty = facultyRepository.findById(request.getFacultyId())
                .orElseThrow(() ->
                        new RuntimeException("La facultad con ID " + request.getFacultyId() + " no existe")
                );



        AcademicProgram program = academicProgramRepository.findById(request.getAcademicProgramId())
                .orElseThrow(() ->
                        new RuntimeException("El programa académico con ID " + request.getAcademicProgramId() + " no existe")
                );


        if (!program.getFaculty().getId().equals(faculty.getId())) {
            return ResponseEntity.badRequest().body(
                    "El programa académico no pertenece a la facultad seleccionada."
            );
        }


        // (opcional pero muy recomendada)
        if (request.getApprovedCredits() > program.getTotalCredits()) {
            return ResponseEntity.badRequest().body(
                    "Los créditos aprobados no pueden superar el total del programa (" +
                            program.getTotalCredits() + ")."
            );
        }



        studentProfile.setFaculty(faculty);
        studentProfile.setAcademicProgram(program);
        studentProfile.setStudentCode(request.getStudentCode());
        studentProfile.setSemester(request.getSemester());
        studentProfile.setGpa(request.getGpa());
        studentProfile.setApprovedCredits(request.getApprovedCredits());

        studentProfileRepository.save(studentProfile);

        return ResponseEntity.ok("Perfil académico actualizado correctamente");
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
                .faculty( profileOpt.map(StudentProfile::getFaculty).map(Faculty::getName).orElse(null))
                .academicProgram(profileOpt.map(StudentProfile::getAcademicProgram).map(AcademicProgram::getName).orElse(null))

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

    public ResponseEntity<?> viewMyDocument(Long studentDocumentId) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        StudentDocument document = studentDocumentRepository.findById(studentDocumentId)
                .orElseThrow(() -> new RuntimeException("Documento no encontrado"));

        // Verificar que el documento pertenece al estudiante autenticado
        Long documentOwnerId = document.getStudentModality().getStudent().getId();
        if (!documentOwnerId.equals(currentUser.getId())) {
            return ResponseEntity.status(403).body("No tienes permiso para ver este documento");
        }

        // Leer el archivo desde el sistema de archivos
        try {
            Path filePath = Paths.get(document.getFilePath());
            Resource resource = new UrlResource(filePath.toUri());

            if (!resource.exists() || !resource.isReadable()) {
                return ResponseEntity.status(404).body("Archivo no encontrado o no legible");
            }

            // Detectar tipo de contenido
            String contentType = Files.probeContentType(filePath);
            if (contentType == null) {
                contentType = "application/octet-stream";
            }

            // Retornar el archivo como blob para visualización
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);

        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al leer el archivo: " + e.getMessage());
        }
    }

}

package com.SIGMA.USCO.Users.service;

import com.SIGMA.USCO.Users.Entity.StudentProfile;
import com.SIGMA.USCO.Users.Entity.User;
import com.SIGMA.USCO.Users.dto.StudentProfileDTO;
import com.SIGMA.USCO.Users.repository.StudentProfileRepository;
import com.SIGMA.USCO.Users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final UserRepository userRepository;
    private final StudentProfileRepository studentProfileRepository;

    public ResponseEntity<?> updateStudentProfile(StudentProfileDTO request) {

        
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
}

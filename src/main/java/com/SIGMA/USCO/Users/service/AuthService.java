package com.SIGMA.USCO.Users.service;

import com.SIGMA.USCO.Users.Entity.Role;
import com.SIGMA.USCO.Users.Entity.Status;
import com.SIGMA.USCO.Users.Entity.User;
import com.SIGMA.USCO.Users.dto.CreateUserRequest;
import com.SIGMA.USCO.Users.repository.UserRepository;
import com.SIGMA.USCO.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private AuthenticationManager authenticationManager;


    public ResponseEntity<?> register (CreateUserRequest request){
        if (userRepository.existsByEmail(request.getEmail())){
            return ResponseEntity.badRequest().body("Este correo ya está en uso");
        }

        if (request.getName().isEmpty() || request.getLastName().isEmpty() || request.getEmail().isEmpty() || request.getPassword().isEmpty()) {
            return ResponseEntity.badRequest().body("Todos los campos son obligatorios (nombre, apellido, correo y contraseña)");
        }

        User user = User.builder()
                .name(request.getName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.STUDENT)
                .status(Status.ACTIVE)
                .creationDate(LocalDateTime.now())
                .lastUpdateDate(LocalDateTime.now())
                .build();
        userRepository.save(user);

        String token = jwtService.generateToken(user);

        return ResponseEntity.ok(token);

    }



}

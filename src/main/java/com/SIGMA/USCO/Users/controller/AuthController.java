package com.SIGMA.USCO.Users.controller;

import com.SIGMA.USCO.Users.dto.AuthRequest;
import com.SIGMA.USCO.Users.dto.ResetPasswordRequest;
import com.SIGMA.USCO.Users.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody AuthRequest request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request) {
        return authService.login(request);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody AuthRequest request) {
        authService.sendResetPasswordLink(request);
        return ResponseEntity.ok("Se envió un enlace de recuperación al correo institucional ingresado.");
    }
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request);
        return ResponseEntity.ok("Tu contraseña fue actualizada correctamente.");
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        return authService.logout(token);
    }





}

package com.SIGMA.USCO.academic.controller;


import com.SIGMA.USCO.academic.dto.ProgramDTO;
import com.SIGMA.USCO.academic.entity.AcademicProgram;
import com.SIGMA.USCO.academic.service.AcademicProgramService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/academic-programs")
@RequiredArgsConstructor
public class AcademicProgramController {

    private final AcademicProgramService academicProgramService;

    @PostMapping("/create")
    @PreAuthorize("hasAuthority('PERM_CREATE_PROGRAM')")
    public ResponseEntity<?> createProgram(@RequestBody ProgramDTO request) {

        try {
            ProgramDTO program = academicProgramService.createProgram(request);

            return ResponseEntity.status(HttpStatus.CREATED).body(
                    Map.of(
                            "message", "Programa académico creado exitosamente."
                    )
            );

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(
                    Map.of("error", e.getMessage())
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    Map.of("error", "Ocurrió un error al crear el programa académico.")
            );
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getProgramById(@PathVariable Long id) {
        try {
            ProgramDTO program = academicProgramService.getProgramById(id);
            return ResponseEntity.ok(program);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    Map.of("error", e.getMessage())
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    Map.of("error", "Ocurrió un error al obtener el programa académico.")
            );
        }
    }

    @GetMapping("/all")
    @PreAuthorize("hasAuthority('PERM_VIEW_PROGRAMS')")
    public ResponseEntity<?> getAllPrograms() {
        try {
            return ResponseEntity.ok(academicProgramService.getAllPrograms());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    Map.of("error", "Ocurrió un error al obtener los programas académicos.")
            );
        }
    }

    @GetMapping("/active")
    public ResponseEntity<?> getActivePrograms() {
        try {
            return ResponseEntity.ok(academicProgramService.getActivePrograms());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    Map.of("error", "Ocurrió un error al obtener los programas académicos activos.")
            );
        }
    }

    @PutMapping("/update/{id}")
    @PreAuthorize("hasAuthority('PERM_UPDATE_PROGRAM')")
    public ResponseEntity<?> updateProgram(@PathVariable Long id, @RequestBody ProgramDTO request) {
        try {
            AcademicProgram updatedProgram = academicProgramService.updateProgram(id, request);
            return ResponseEntity.ok(
                    Map.of(
                            "message", "Programa académico actualizado exitosamente."
                    )
            );
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(
                    Map.of("error", e.getMessage())
            );
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    Map.of("error", e.getMessage())
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    Map.of("error", "Ocurrió un error al actualizar el programa académico.")
            );
        }
    }
}

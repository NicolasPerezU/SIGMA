package com.SIGMA.USCO.academic.controller;

import com.SIGMA.USCO.academic.dto.ProgramDegreeModalityDTO;
import com.SIGMA.USCO.academic.dto.ProgramDegreeModalityRequest;
import com.SIGMA.USCO.academic.service.ProgramDegreeModalityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/program-degree-modalities")
@RequiredArgsConstructor
public class ProgramDegreeModalityController {

    private final ProgramDegreeModalityService programDegreeModalityService;

    // CREATE
    @PostMapping("/create")
    @PreAuthorize("hasAuthority('PERM_CREATE_PROGRAM_DEGREE_MODALITY')")
    public ResponseEntity<?> createProgramDegreeModality(@RequestBody ProgramDegreeModalityRequest request) {
        try {
            ProgramDegreeModalityDTO dto = programDegreeModalityService.createProgramModality(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(
                    Map.of(
                            "success", true,
                            "message", "Modalidad de grado del programa creada exitosamente.",
                            "data", dto
                    )
            );
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(
                    Map.of("success", false, "error", e.getMessage())
            );
        } catch (Exception e) {
            return ResponseEntity.status(500).body(
                    Map.of("success", false, "error", "Ocurrió un error al crear la modalidad de grado del programa.")
            );
        }
    }


    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('PERM_VIEW_PROGRAM_DEGREE_MODALITY', 'PERM_CREATE_PROGRAM_DEGREE_MODALITY', 'PERM_UPDATE_PROGRAM_DEGREE_MODALITY')")
    public ResponseEntity<?> getProgramModalityById(@PathVariable Long id) {
        try {
            ProgramDegreeModalityDTO dto = programDegreeModalityService.getProgramModalityById(id);
            return ResponseEntity.ok(
                    Map.of(
                            "success", true,
                            "data", dto
                    )
            );
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(
                    Map.of("success", false, "error", e.getMessage())
            );
        } catch (Exception e) {
            return ResponseEntity.status(500).body(
                    Map.of("success", false, "error", "Ocurrió un error al obtener la configuración.")
            );
        }
    }


    @GetMapping("/all")
    @PreAuthorize("hasAnyAuthority('PERM_VIEW_PROGRAM_DEGREE_MODALITY', 'PERM_CREATE_PROGRAM_DEGREE_MODALITY')")
    public ResponseEntity<?> getAllProgramModalities(
            @RequestParam(required = false) Boolean active,
            @RequestParam(required = false) Long degreeModalityId,
            @RequestParam(required = false) Long facultyId,
            @RequestParam(required = false) Long academicProgramId
    ) {
        try {
            List<ProgramDegreeModalityDTO> list = programDegreeModalityService.getAllProgramModalities(
                    active, degreeModalityId, facultyId, academicProgramId
            );
            return ResponseEntity.ok(
                    Map.of(
                            "success", true,
                            "data", list,
                            "count", list.size()
                    )
            );
        } catch (Exception e) {
            return ResponseEntity.status(500).body(
                    Map.of("success", false, "error", "Ocurrió un error al obtener las configuraciones.")
            );
        }
    }

    @PutMapping("/update/{id}")
    @PreAuthorize("hasAnyAuthority('PERM_VIEW_PROGRAM_DEGREE_MODALITY', 'PERM_CREATE_PROGRAM_DEGREE_MODALITY')")
    public ResponseEntity<?> updateProgramModality(@PathVariable Long id, @RequestBody ProgramDegreeModalityRequest request) {
        try {
            ProgramDegreeModalityDTO dto = programDegreeModalityService.updateProgramModality(id, request);
            return ResponseEntity.ok(
                    Map.of(
                            "success", true,
                            "message", "Configuración de modalidad actualizada exitosamente.",
                            "data", dto
                    )
            );
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(
                    Map.of("success", false, "error", e.getMessage())
            );
        } catch (Exception e) {
            return ResponseEntity.status(500).body(
                    Map.of("success", false, "error", "Ocurrió un error al actualizar la configuración.")
            );
        }
    }


    @PutMapping("/desactivate/{id}")
    @PreAuthorize("hasAnyAuthority('PERM_VIEW_PROGRAM_DEGREE_MODALITY', 'PERM_CREATE_PROGRAM_DEGREE_MODALITY')")
    public ResponseEntity<?> deactivateProgramModality(@PathVariable Long id) {
        try {
            programDegreeModalityService.deactivateProgramModality(id);
            return ResponseEntity.ok(
                    Map.of(
                            "success", true,
                            "message", "Configuración de modalidad desactivada exitosamente."
                    )
            );
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(
                    Map.of("success", false, "error", e.getMessage())
            );
        } catch (Exception e) {
            return ResponseEntity.status(500).body(
                    Map.of("success", false, "error", "Ocurrió un error al desactivar la configuración.")
            );
        }
    }


    @PutMapping("/activate/{id}")
    @PreAuthorize("hasAnyAuthority('PERM_VIEW_PROGRAM_DEGREE_MODALITY', 'PERM_CREATE_PROGRAM_DEGREE_MODALITY')")
    public ResponseEntity<?> activateProgramModality(@PathVariable Long id) {
        try {
            programDegreeModalityService.activateProgramModality(id);
            return ResponseEntity.ok(
                    Map.of(
                            "success", true,
                            "message", "Configuración de modalidad activada exitosamente."
                    )
            );
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(
                    Map.of("success", false, "error", e.getMessage())
            );
        } catch (Exception e) {
            return ResponseEntity.status(500).body(
                    Map.of("success", false, "error", "Ocurrió un error al activar la configuración.")
            );
        }
    }


}

package com.SIGMA.USCO.academic.controller;

import com.SIGMA.USCO.academic.dto.FacultyDTO;
import com.SIGMA.USCO.academic.entity.Faculty;
import com.SIGMA.USCO.academic.service.FacultyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/faculties")
public class FacultyController {

    private final FacultyService facultyService;

    @PostMapping("/create")
    @PreAuthorize("hasAuthority('PERM_CREATE_FACULTY')")
    public ResponseEntity<?> createFaculty(@RequestBody FacultyDTO request){

        try {
            Faculty faculty = facultyService.createFaculty(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(
                    Map.of(
                            "message", "Facultad creada exitosamente.",
                            "faculty", faculty
                    )
            );
        }catch (IllegalArgumentException e){
            return ResponseEntity.badRequest().body(
                    Map.of(
                            "error", e.getMessage()
                    )
            );
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    Map.of(
                            "error", "Ocurrió un error al crear la facultad."
                    )
            );
        }

    }

    @GetMapping("/all") //admin
    @PreAuthorize("hasAuthority('PERM_VIEW_FACULTIES')")
    public ResponseEntity<?> getAllFaculties(){
        return ResponseEntity.ok(
                Map.of(
                        "faculties", facultyService.getAllFaculties()
                )
        );

    }

    @GetMapping("/active")
    public ResponseEntity<?> getActiveFaculties() {
        return ResponseEntity.ok(
                Map.of(
                        "faculties", facultyService.getActiveFaculties()
                )
        );
    }

    @PutMapping("/update/{id}")
    @PreAuthorize("hasAuthority('PERM_UPDATE_FACULTY')")
    public ResponseEntity<?> updateFaculty(@PathVariable Long id, @RequestBody FacultyDTO request) {
        try {
            FacultyDTO updatedFaculty = facultyService.updateFaculty(id, request);
            return ResponseEntity.ok(
                    Map.of(
                            "message", "Facultad actualizada exitosamente.",
                            "faculty", updatedFaculty
                    )
            );
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(
                    Map.of(
                            "error", e.getMessage()
                    )
            );
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    Map.of(
                            "error", e.getMessage()
                    )
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    Map.of(
                            "error", "Ocurrió un error al actualizar la facultad."
                    )
            );
        }
    }

    @PutMapping("/desactive/{id}")
    @PreAuthorize("hasAuthority('PERM_DELETE_FACULTY')")
    public ResponseEntity<?> desactivate(@PathVariable Long id) {
        facultyService.deactivateFaculty(id);
        return ResponseEntity.ok(
                Map.of(
                        "message", "Facultad desactivada exitosamente."
                )
        );
    }

    @GetMapping("/detail/{id}") //admin
    @PreAuthorize("hasAuthority('PERM_VIEW_FACULTIES')")
    public ResponseEntity<?> getFacultyDetail(@PathVariable Long id) {
        return ResponseEntity.ok(facultyService.getFacultyDetail(id));
    }

}

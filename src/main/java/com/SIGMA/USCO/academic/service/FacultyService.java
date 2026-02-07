package com.SIGMA.USCO.academic.service;

import com.SIGMA.USCO.academic.dto.FacultyDTO;
import com.SIGMA.USCO.academic.dto.ProgramDTO;
import com.SIGMA.USCO.academic.entity.Faculty;
import com.SIGMA.USCO.academic.repository.FacultyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FacultyService {

    private final FacultyRepository facultyRepository;


    public Faculty createFaculty(FacultyDTO request){

        if (request.getDescription() == null || request.getDescription().isBlank()
        || request.getName() == null || request.getName().isBlank()) {
            throw new IllegalArgumentException("Nombre y descripción no puede ser vacío o nulo.");
        }

        if (request.getCode() == null || request.getCode().isBlank()) {
            throw new IllegalArgumentException("El código de la facultad es obligatorio.");
        }


        if (facultyRepository.existsByCodeIgnoreCase(request.getCode())){
            throw new IllegalArgumentException("El código de la facultad ya existe.");
        }

        if (facultyRepository.existsByNameIgnoreCase(request.getName())){
            throw new IllegalArgumentException("El nombre de la facultad ya existe.");
        }
        Faculty faculty = Faculty.builder()
                .name(request.getName().toUpperCase())
                .code(request.getCode().toUpperCase())
                .description(request.getDescription())
                .active(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        return facultyRepository.save(faculty);


    }

    public List<FacultyDTO> getAllFaculties() {
        return facultyRepository.findAll()
                .stream()
                .map(faculty -> FacultyDTO.builder()
                        .id(faculty.getId())
                        .name(faculty.getName())
                        .code(faculty.getCode())
                        .description(faculty.getDescription())
                        .active(faculty.isActive())
                        .build())
                .toList();

    }

    public List<FacultyDTO> getActiveFaculties() {
        return facultyRepository.findByActiveTrue()
                .stream()
                .map(faculty -> FacultyDTO.builder()
                        .id(faculty.getId())
                        .name(faculty.getName())
                        .code(faculty.getCode())
                        .description(faculty.getDescription())
                        .active(faculty.isActive())
                        .build())
                .toList();
    }

    public FacultyDTO updateFaculty(Long id, FacultyDTO request) {

        if (request.getName() == null || request.getName().isBlank()
                || request.getDescription() == null || request.getDescription().isBlank()) {
            throw new IllegalArgumentException("Nombre y descripción no pueden ser nulos o vacíos.");
        }

        if (request.getCode() == null || request.getCode().isBlank()) {
            throw new IllegalArgumentException("El código de la facultad es obligatorio.");
        }

        Faculty faculty = facultyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Facultad no encontrada"));

        if (!faculty.getCode().equalsIgnoreCase(request.getCode())
                && facultyRepository.existsByCodeIgnoreCase(request.getCode())) {
            throw new IllegalArgumentException("El código de la facultad ya existe.");
        }

        if (!faculty.getName().equalsIgnoreCase(request.getName())
                && facultyRepository.existsByNameIgnoreCase(request.getName())) {
            throw new IllegalArgumentException("El nombre de la facultad ya existe.");
        }

        faculty.setName(request.getName().toUpperCase());
        faculty.setCode(request.getCode().toUpperCase());
        faculty.setDescription(request.getDescription());
        faculty.setUpdatedAt(LocalDateTime.now());

        facultyRepository.save(faculty);

        return FacultyDTO.builder()
                .id(faculty.getId())
                .name(faculty.getName())
                .code(faculty.getCode())
                .description(faculty.getDescription())
                .active(faculty.isActive())
                .build();


    }

    public void deactivateFaculty(Long id) {
        Faculty faculty = facultyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Facultad no encontrada"));

        faculty.setActive(false);
        faculty.setUpdatedAt(LocalDateTime.now());

        facultyRepository.save(faculty);
    }

    public FacultyDTO getFacultyDetail(Long facultyId) {

        Faculty faculty = facultyRepository.findById(facultyId)
                .orElseThrow(() -> new RuntimeException("Facultad no encontrada"));

        List<ProgramDTO> programs =
                faculty.getPrograms()
                        .stream()
                        .map(p -> ProgramDTO.builder()
                                .id(p.getId())
                                .name(p.getName())
                                .code(p.getCode())
                                .totalCredits(p.getTotalCredits())
                                .active(p.isActive())
                                .build()
                        )
                        .toList();

        return FacultyDTO.builder()
                .id(faculty.getId())
                .name(faculty.getName())
                .code(faculty.getCode())
                .description(faculty.getDescription())
                .active(faculty.isActive())
                .academicPrograms(programs)
                .build();
    }



}

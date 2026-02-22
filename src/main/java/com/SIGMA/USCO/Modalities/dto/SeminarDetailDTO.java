package com.SIGMA.USCO.Modalities.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeminarDetailDTO {

    private Long id;
    private String name;
    private String description;
    private BigDecimal totalCost;
    private Integer minParticipants;
    private Integer maxParticipants;
    private Integer currentParticipants;
    private Integer totalHours;
    private boolean active;
    private String status;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Información del programa académico
    private Long academicProgramId;
    private String academicProgramName;
    private String facultyName;

    // Estadísticas
    private Integer availableSeats;
    private Double fillPercentage;
    private boolean hasMinimumParticipants;

    // Lista de estudiantes inscritos
    private List<EnrolledStudentDTO> enrolledStudents;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EnrolledStudentDTO {
        private Long studentId;
        private String studentCode;
        private String name;
        private String lastName;
        private String email;


        // Información académica
        private Integer approvedCredits;

    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ModalityInfoDTO {
        private Long modalityId;
        private String modalityName;
        private String modalityType;
        private String status;
        private LocalDateTime selectionDate;
    }
}


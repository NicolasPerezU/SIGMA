package com.SIGMA.USCO.Modalities.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeminarListDTO {

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

    // Estadísticas
    private Integer availableSeats;
    private Double fillPercentage;
    private boolean hasMinimumParticipants;
    private boolean isFull;
}


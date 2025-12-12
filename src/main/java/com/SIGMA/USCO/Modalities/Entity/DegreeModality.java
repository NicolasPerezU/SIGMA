package com.SIGMA.USCO.Modalities.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "degree_modalities")
public class DegreeModality {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    @Column(length = 4000)
    private String description;

    @Enumerated(EnumType.STRING)
    private ModalityStatus status;

    private Long creditsRequired;

    @Enumerated(EnumType.STRING)
    private ModalityType type;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}

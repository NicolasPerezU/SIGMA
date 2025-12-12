package com.SIGMA.USCO.Modalities.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "modality_requirements")
public class ModalityRequirements {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "modality_id")
    private DegreeModality modality;

    @Column(nullable = false)
    private String requirementName;

    @Column(length = 4000)
    private String description;

    @Enumerated(EnumType.STRING)
    private RuleType ruleType;

    @Column(nullable = false)
    private String expectedValue;

    private boolean active = true;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}

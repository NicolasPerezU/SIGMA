package com.SIGMA.USCO.Modalities.Entity;

import com.SIGMA.USCO.Modalities.Entity.enums.ExaminerDecision;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @deprecated Esta entidad ya no se usa como repositorio de evaluaciones.
 * La lógica fue migrada a {@link DefenseEvaluationCriteria} que incluye
 * los criterios de rúbrica, nota, decisión y observaciones directamente.
 * Esta entidad se mantiene por compatibilidad con la base de datos existente.
 */
@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "examiner_evaluations")
public class ExaminerEvaluation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "defense_examiner_id", nullable = false)
    private DefenseExaminer defenseExaminer;

    @Column(nullable = false)
    private Double grade;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private ExaminerDecision decision;

    @Column(length = 2000)
    private String observations;

    @Column(nullable = false)
    private LocalDateTime evaluationDate;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isFinalDecision = false;
}

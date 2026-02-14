package com.SIGMA.USCO.Modalities.Entity;

import com.SIGMA.USCO.Modalities.Entity.enums.MemberStatus;
import com.SIGMA.USCO.Users.Entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entidad que representa la relación entre estudiantes y modalidades.
 * Permite que múltiples estudiantes trabajen en una misma modalidad (modalidad grupal).
 */
@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "student_modality_members")
public class StudentModalityMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "student_modality_id")
    private StudentModality studentModality;

    @ManyToOne(optional = false)
    @JoinColumn(name = "student_id")
    private User student;

    @Builder.Default
    @Column(nullable = false)
    private Boolean isLeader = false;

    @Enumerated(EnumType.STRING)
    @Column(length = 50, nullable = false)
    private MemberStatus status;

    @Column(nullable = false)
    private LocalDateTime joinedAt;

}


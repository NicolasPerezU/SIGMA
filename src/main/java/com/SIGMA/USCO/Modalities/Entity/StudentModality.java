package com.SIGMA.USCO.Modalities.Entity;

import com.SIGMA.USCO.Users.Entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "student_modalities")
public class StudentModality {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private User student;

    @Enumerated(EnumType.STRING)
    private ModalityProcessStatus status;

    private LocalDateTime selectionDate;
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "studentModality", cascade = CascadeType.ALL)
    private List<ModalityProcessStatusHistory> statusHistory;

    @ManyToOne
    private User projectDirector;

    @Enumerated(EnumType.STRING)
    private AcademicDistinction academicDistinction;

    private LocalDateTime defenseDate;

    private LocalDateTime defenseLocation;



}

package com.SIGMA.USCO.Modalities.Entity;

import com.SIGMA.USCO.Modalities.Entity.enums.AcademicDistinction;
import com.SIGMA.USCO.Modalities.Entity.enums.ModalityProcessStatus;
import com.SIGMA.USCO.Users.Entity.User;
import com.SIGMA.USCO.academic.entity.AcademicProgram;
import com.SIGMA.USCO.academic.entity.ProgramDegreeModality;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

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
    @JoinColumn(name = "student_id")
    private User student;

    @ManyToOne(optional = false)
    @JoinColumn(name = "academic_program_id")
    private AcademicProgram academicProgram;

    @ManyToOne(optional = false)
    @JoinColumn(name = "program_degree_modality_id")
    private ProgramDegreeModality programDegreeModality;

    @OneToMany(mappedBy = "studentModality", cascade = CascadeType.ALL)
    private List<ModalityProcessStatusHistory> statusHistory;


    @Enumerated(EnumType.STRING)
    @Column(length = 100, nullable = false)
    private ModalityProcessStatus status;

    private LocalDateTime selectionDate;
    private LocalDateTime updatedAt;



    @ManyToOne
    private User projectDirector;


    @OneToMany(mappedBy = "studentModality", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<DefenseExaminer> defenseExaminers;


    @Enumerated(EnumType.STRING)
    @Column(length = 100)
    private AcademicDistinction academicDistinction;


    private Double finalGrade;

    private LocalDateTime defenseDate;

    private String defenseLocation;


    private LocalDateTime correctionRequestDate;
    private LocalDateTime correctionDeadline;
    private Boolean correctionReminderSent;

}

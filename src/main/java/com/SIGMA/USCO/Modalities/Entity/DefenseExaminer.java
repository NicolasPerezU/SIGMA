package com.SIGMA.USCO.Modalities.Entity;

import com.SIGMA.USCO.Modalities.Entity.enums.ExaminerType;
import com.SIGMA.USCO.Users.Entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;



@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "defense_examiners")
public class DefenseExaminer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "student_modality_id", nullable = false)
    private StudentModality studentModality;

    @ManyToOne(optional = false)
    @JoinColumn(name = "examiner_id", nullable = false)
    private User examiner;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private ExaminerType examinerType;

    @Column(nullable = false)
    private LocalDateTime assignmentDate;

    @ManyToOne
    @JoinColumn(name = "assigned_by_user_id")
    private User assignedBy;


}


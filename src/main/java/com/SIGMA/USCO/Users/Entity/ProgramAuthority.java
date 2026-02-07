package com.SIGMA.USCO.Users.Entity;

import com.SIGMA.USCO.Users.Entity.enums.ProgramRole;
import com.SIGMA.USCO.academic.entity.AcademicProgram;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "program_authorities",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"user_id", "academic_program_id", "role"}
        ))
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProgramAuthority {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(optional = false)
    @JoinColumn(name = "academic_program_id")
    private AcademicProgram academicProgram;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 100)
    private ProgramRole role;

}

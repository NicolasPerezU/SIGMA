package com.SIGMA.USCO.academic.entity;

import com.SIGMA.USCO.Modalities.Entity.DegreeModality;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "faculties")
@Entity
public class Faculty {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 150)
    private String name;

    private String description;

    @Column(nullable = false, unique = true, length = 30)
    private String code;

    @Column(nullable = false)
    private boolean active = true;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "faculty")
    private List<AcademicProgram> programs;

    @OneToMany(mappedBy = "faculty")
    private List<DegreeModality> modalities;

    @OneToMany(mappedBy = "faculty")
    private List<StudentProfile> studentProfiles;

    @Override
    public String toString() {
        return "Faculty{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", code='" + code + '\'' +
                ", active=" + active +
                '}';
    }
}

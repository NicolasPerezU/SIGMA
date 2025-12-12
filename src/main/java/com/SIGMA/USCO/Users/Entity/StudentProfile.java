package com.SIGMA.USCO.Users.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "student_profiles")
public class StudentProfile {
    @Id
    @Column(name = "user_id")
    private Long id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;

    private Long approvedCredits;
    private Double gpa;            // Grade Point Average (promedio)
    private Long semester;
    private String studentCode;

}

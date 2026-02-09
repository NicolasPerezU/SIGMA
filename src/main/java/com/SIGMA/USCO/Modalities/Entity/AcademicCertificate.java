package com.SIGMA.USCO.Modalities.Entity;

import com.SIGMA.USCO.Modalities.Entity.enums.CertificateStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "academic_certificates")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AcademicCertificate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "student_modality_id", nullable = false)
    private StudentModality studentModality;

    @Column(nullable = false, unique = true)
    private String certificateNumber;

    @Column(nullable = false)
    private LocalDateTime issueDate;

    @Column(nullable = false)
    private String filePath;

    @Column(nullable = false)
    private String fileHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CertificateStatus status;

    @Column
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}


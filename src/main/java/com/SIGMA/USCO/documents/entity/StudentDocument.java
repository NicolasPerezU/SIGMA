package com.SIGMA.USCO.documents.entity;

import com.SIGMA.USCO.Modalities.Entity.StudentModality;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "student_documents")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private StudentModality studentModality;

    @ManyToOne(optional = false)
    private RequiredDocument documentConfig;

    private String fileName;
    private String filePath;

    @Enumerated(EnumType.STRING)
    @Column(length = 60)
    private DocumentStatus status;

    @Column(length = 3000)
    private String notes;

    private LocalDateTime uploadDate;

}

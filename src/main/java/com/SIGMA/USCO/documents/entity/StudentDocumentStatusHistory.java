package com.SIGMA.USCO.documents.entity;

import com.SIGMA.USCO.Users.Entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "student_document_status_history")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentDocumentStatusHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private StudentDocument studentDocument;

    @Enumerated(EnumType.STRING)
    @Column(length = 100)
    private DocumentStatus status;

    private LocalDateTime changeDate;

    @ManyToOne
    private User responsible;

    @Column(length = 5000)
    private String observations;

}

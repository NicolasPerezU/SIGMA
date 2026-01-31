package com.SIGMA.USCO.documents.entity;

import com.SIGMA.USCO.Modalities.Entity.DegreeModality;
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
public class RequiredDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "modality_id")
    private DegreeModality modality;

    @Column(nullable = false)
    private String documentName;

    private String allowedFormat;

    private Integer maxFileSizeMB;

    @Column(name = "is_mandatory")
    private boolean isMandatory;

    @Column(length = 5000)
    private String description;

    private boolean active = true;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}

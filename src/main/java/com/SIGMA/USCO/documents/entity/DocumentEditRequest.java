package com.SIGMA.USCO.documents.entity;

import com.SIGMA.USCO.Users.Entity.User;
import com.SIGMA.USCO.documents.entity.enums.DocumentEditRequestStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Registra la solicitud de un estudiante para editar/resubir un documento
 * que ya fue previamente aprobado por los jurados evaluadores.
 */
@Entity
@Table(name = "document_edit_requests")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentEditRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Documento aprobado sobre el que se solicita la edición */
    @ManyToOne(optional = false)
    @JoinColumn(name = "student_document_id", nullable = false)
    private StudentDocument studentDocument;

    /** Estudiante que solicita la edición */
    @ManyToOne(optional = false)
    @JoinColumn(name = "requester_id", nullable = false)
    private User requester;

    /** Motivo o justificación de la solicitud de edición */
    @Column(name = "reason", nullable = false, length = 200000)
    private String reason;

    /** Estado actual de la solicitud */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    @Builder.Default
    private DocumentEditRequestStatus status = DocumentEditRequestStatus.PENDING;

    /** Jurado o responsable que resolvió la solicitud */
    @ManyToOne
    @JoinColumn(name = "resolved_by_id")
    private User resolvedBy;

    /** Notas del jurado al aprobar o rechazar la solicitud */
    @Column(name = "resolution_notes", length = 2000)
    private String resolutionNotes;

    /** Fecha en que se creó la solicitud */
    @Column(name = "created_at", nullable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    /** Fecha en que fue resuelta (aprobada o rechazada) */
    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt;
}


package com.SIGMA.USCO.documents.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * DTO de entrada para que un jurado vote sobre una solicitud de edición de documento.
 * Sigue la misma lógica de consenso que la revisión de documentos:
 * - approved=true  → voto de aprobación
 * - approved=false → voto de rechazo
 */
@Data
public class DocumentEditResolutionDTO {

    /**
     * Decisión del jurado:
     * true  = aprueba la solicitud de edición
     * false = rechaza la solicitud de edición
     */
    @NotNull(message = "Debe indicar si aprueba (true) o rechaza (false) la solicitud")
    private Boolean approved;

    /** Notas o justificación del jurado (obligatorio si rechaza) */
    private String resolutionNotes;
}

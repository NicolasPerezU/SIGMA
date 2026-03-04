package com.SIGMA.USCO.documents.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * DTO de entrada para que el estudiante solicite la edición de un documento aprobado.
 */
@Data
public class DocumentEditRequestDTO {

    /** Motivo o justificación de la solicitud de edición */
    @NotBlank(message = "El motivo de la solicitud es obligatorio")
    @Size(min = 20, max = 2000, message = "El motivo debe tener entre 20 y 2000 caracteres")
    private String reason;
}

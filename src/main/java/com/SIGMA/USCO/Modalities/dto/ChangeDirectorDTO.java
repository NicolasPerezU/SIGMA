package com.SIGMA.USCO.Modalities.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para la solicitud de cambio de director de proyecto
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChangeDirectorDTO {

    @NotNull(message = "El ID del nuevo director es requerido")
    private Long newDirectorId;

    @NotBlank(message = "La razón del cambio es requerida")
    private String reason;
}


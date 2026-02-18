package com.SIGMA.USCO.report.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO para el listado de tipos de modalidad disponibles
 * RF-46 - Filtrado por Tipo de Modalidad
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AvailableModalityTypesDTO {

    /**
     * Lista de tipos de modalidad disponibles para filtrar
     */
    private List<ModalityTypeInfo> availableTypes;

    /**
     * Programa académico al que pertenecen estos tipos
     */
    private Long academicProgramId;

    /**
     * Nombre del programa académico
     */
    private String academicProgramName;

    /**
     * Total de tipos disponibles
     */
    private Integer totalTypes;

    /**
     * Información de cada tipo de modalidad
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ModalityTypeInfo {
        private Long id;
        private String name;
        private String description;
        private Integer activeModalitiesCount;
        private Boolean requiresDirector;
        private String status;
    }
}


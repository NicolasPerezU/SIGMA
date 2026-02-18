package com.SIGMA.USCO.report.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

/**
 * DTO para el Reporte Global de Modalidades Activas
 * RF-45 - Generación de Reporte Global de Modalidades Activas
 *
 * Contiene información consolidada de todas las modalidades activas en el sistema,
 * incluyendo estadísticas generales y detalles por modalidad.
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class GlobalModalityReportDTO extends BaseReportDTO {

    /**
     * ID del programa académico del reporte
     */
    private Long academicProgramId;

    /**
     * Nombre del programa académico del reporte
     */
    private String academicProgramName;

    /**
     * Código del programa académico del reporte
     */
    private String academicProgramCode;

    /**
     * Resumen ejecutivo del reporte
     */
    private ExecutiveSummaryDTO executiveSummary;

    /**
     * Lista detallada de modalidades activas
     */
    private List<ModalityDetailReportDTO> modalities;

    /**
     * Estadísticas por programa académico
     */
    private List<ProgramStatisticsDTO> programStatistics;


}


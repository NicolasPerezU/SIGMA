package com.SIGMA.USCO.report.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

/**
 * DTO base para todos los reportes del sistema
 * Contiene información común a todos los tipos de reportes
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public abstract class BaseReportDTO {

    private LocalDateTime generatedAt;
    private String generatedBy;
    private ReportMetadataDTO metadata;
}


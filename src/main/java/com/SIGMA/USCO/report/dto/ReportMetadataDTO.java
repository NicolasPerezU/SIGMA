package com.SIGMA.USCO.report.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportMetadataDTO {


    private String reportVersion;


    private String reportType;


    private String generatedBySystem;


    private Integer totalRecords;


    private Long generationTimeMs;


    private String exportFormat;

}


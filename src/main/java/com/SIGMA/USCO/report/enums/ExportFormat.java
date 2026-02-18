package com.SIGMA.USCO.report.enums;

/**
 * Enumeración de formatos de exportación para reportes
 */
public enum ExportFormat {
    JSON("application/json", ".json"),
    PDF("application/pdf", ".pdf"),
    EXCEL("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", ".xlsx"),
    CSV("text/csv", ".csv");

    private final String mimeType;
    private final String extension;

    ExportFormat(String mimeType, String extension) {
        this.mimeType = mimeType;
        this.extension = extension;
    }

    public String getMimeType() {
        return mimeType;
    }

    public String getExtension() {
        return extension;
    }
}


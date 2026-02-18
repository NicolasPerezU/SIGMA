package com.SIGMA.USCO.report.service;

import com.SIGMA.USCO.report.dto.CompletedModalitiesReportDTO;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.itextpdf.text.pdf.draw.LineSeparator;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;

/**
 * Servicio para generar PDF del reporte de modalidades completadas
 * Diseño profesional e institucional con análisis completo de resultados
 */
@Service
public class CompletedModalitiesPdfGenerator {

    // COLORES INSTITUCIONALES - USO EXCLUSIVO
    private static final BaseColor INSTITUTIONAL_RED = new BaseColor(143, 30, 30); // #8F1E1E - Color primario
    private static final BaseColor INSTITUTIONAL_GOLD = new BaseColor(213, 203, 160); // #D5CBA0 - Color secundario
    private static final BaseColor WHITE = BaseColor.WHITE; // Color primario
    private static final BaseColor LIGHT_GOLD = new BaseColor(245, 242, 235); // Tono muy claro de dorado para fondos sutiles
    private static final BaseColor TEXT_BLACK = BaseColor.BLACK; // Texto principal
    private static final BaseColor TEXT_GRAY = new BaseColor(80, 80, 80); // Texto secundario

    // Fuentes con colores institucionales
    private static final Font TITLE_FONT = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 24, INSTITUTIONAL_RED);
    private static final Font SUBTITLE_FONT = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, INSTITUTIONAL_RED);
    private static final Font HEADER_FONT = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, INSTITUTIONAL_RED);
    private static final Font SUBHEADER_FONT = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 13, INSTITUTIONAL_RED);
    private static final Font BOLD_FONT = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, TEXT_BLACK);
    private static final Font NORMAL_FONT = FontFactory.getFont(FontFactory.HELVETICA, 10, TEXT_BLACK);
    private static final Font SMALL_FONT = FontFactory.getFont(FontFactory.HELVETICA, 9, TEXT_GRAY);
    private static final Font TINY_FONT = FontFactory.getFont(FontFactory.HELVETICA, 8, TEXT_GRAY);
    private static final Font TABLE_HEADER_FONT = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8, WHITE);
    private static final Font TABLE_FONT = FontFactory.getFont(FontFactory.HELVETICA, 7, TEXT_BLACK);

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public ByteArrayOutputStream generatePDF(CompletedModalitiesReportDTO report)
            throws DocumentException, IOException {

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4, 40, 40, 50, 50);
        PdfWriter writer = PdfWriter.getInstance(document, outputStream);

        // Agregar eventos de página
        CompletedModalitiesPageEventHelper pageEvent = new CompletedModalitiesPageEventHelper(report);
        writer.setPageEvent(pageEvent);

        document.open();

        // 1. Portada
        addCoverPage(document, report);

        // 2. Filtros y Resumen Ejecutivo
        document.newPage();
        addFiltersAndExecutiveSummary(document, report);

        // 3. Estadísticas Generales
        document.newPage();
        addGeneralStatistics(document, report);

        // 4. Análisis por Resultado
        document.newPage();
        addResultAnalysis(document, report);

        // 5. Análisis por Tipo de Modalidad
        document.newPage();
        addModalityTypeAnalysis(document, report);

        // 6. Listado Detallado de Modalidades Completadas
        document.newPage();
        addCompletedModalitiesListing(document, report);

        // 7. Análisis Temporal
        document.newPage();
        addTemporalAnalysis(document, report);

        // 8. Desempeño de Directores
        document.newPage();
        addDirectorPerformance(document, report);

        // 9. Análisis de Distinciones Académicas
        document.newPage();
        addDistinctionAnalysis(document, report);

        document.close();
        return outputStream;
    }

    /**
     * Portada del reporte
     */
    private void addCoverPage(Document document, CompletedModalitiesReportDTO report)
            throws DocumentException {

        // Banda superior institucional
        PdfPTable headerBand = new PdfPTable(1);
        headerBand.setWidthPercentage(100);
        headerBand.setSpacingAfter(40);

        PdfPCell bandCell = new PdfPCell();
        bandCell.setBackgroundColor(INSTITUTIONAL_RED);
        bandCell.setPadding(30);
        bandCell.setBorder(Rectangle.NO_BORDER);

        Paragraph bandContent = new Paragraph();
        bandContent.setAlignment(Element.ALIGN_CENTER);

        Chunk universityName = new Chunk("UNIVERSIDAD SURCOLOMBIANA\n",
            FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, WHITE));
        bandContent.add(universityName);

        Chunk programName = new Chunk(report.getAcademicProgramName() + "\n",
            FontFactory.getFont(FontFactory.HELVETICA, 14, WHITE));
        bandContent.add(programName);

        Chunk programCode = new Chunk("Código: " + report.getAcademicProgramCode(),
            FontFactory.getFont(FontFactory.HELVETICA, 12, new BaseColor(200, 200, 200)));
        bandContent.add(programCode);

        bandCell.addElement(bandContent);
        headerBand.addCell(bandCell);
        document.add(headerBand);

        // Espacio
        document.add(new Paragraph("\n\n"));

        // Título principal
        Paragraph title = new Paragraph("REPORTE DE MODALIDADES\nCOMPLETADAS", TITLE_FONT);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(10);
        document.add(title);

        Paragraph subtitle = new Paragraph("Análisis de Resultados Académicos", SUBTITLE_FONT);
        subtitle.setAlignment(Element.ALIGN_CENTER);
        subtitle.setSpacingAfter(40);
        document.add(subtitle);

        // Cuadro de información del reporte
        PdfPTable infoTable = new PdfPTable(2);
        infoTable.setWidthPercentage(80);
        infoTable.setWidths(new float[]{1.5f, 2f});
        infoTable.setSpacingBefore(30);
        infoTable.setHorizontalAlignment(Element.ALIGN_CENTER);

        addInfoRow(infoTable, "Fecha de Generación:",
            report.getGeneratedAt().format(DATETIME_FORMATTER));
        addInfoRow(infoTable, "Generado Por:", report.getGeneratedBy());

        if (report.getExecutiveSummary() != null) {
            addInfoRow(infoTable, "Total Completadas:",
                String.valueOf(report.getExecutiveSummary().getTotalCompleted()));
            addInfoRow(infoTable, "Exitosas:",
                String.valueOf(report.getExecutiveSummary().getTotalSuccessful()));
            addInfoRow(infoTable, "Fallidas:",
                String.valueOf(report.getExecutiveSummary().getTotalFailed()));
            addInfoRow(infoTable, "Tasa de Éxito:",
                String.format("%.1f%%", report.getExecutiveSummary().getSuccessRate()));
        }

        document.add(infoTable);

        // Línea separadora decorativa
        document.add(new Paragraph("\n\n"));
        LineSeparator line = new LineSeparator();
        line.setLineColor(INSTITUTIONAL_GOLD);
        line.setLineWidth(2);
        document.add(new Chunk(line));

        // Nota informativa
        document.add(new Paragraph("\n\n"));
        Paragraph disclaimer = new Paragraph(
            "Este reporte presenta un análisis completo de las modalidades de grado finalizadas, " +
            "incluyendo tanto las exitosas como las fallidas. Se incluyen estadísticas de calificaciones, " +
            "tiempos de completitud, distinciones académicas, desempeño de directores y tendencias temporales. " +
            "La información es generada automáticamente por el sistema SIGMA.",
            SMALL_FONT
        );
        disclaimer.setAlignment(Element.ALIGN_JUSTIFIED);
        disclaimer.setIndentationLeft(60);
        disclaimer.setIndentationRight(60);
        document.add(disclaimer);
    }

    /**
     * Filtros y resumen ejecutivo
     */
    private void addFiltersAndExecutiveSummary(Document document, CompletedModalitiesReportDTO report)
            throws DocumentException {

        addSectionTitle(document, "1. FILTROS APLICADOS Y RESUMEN EJECUTIVO");

        // Filtros aplicados
        if (report.getAppliedFilters() != null) {
            addSubsectionTitle(document, "Filtros Aplicados");

            PdfPTable filterTable = new PdfPTable(1);
            filterTable.setWidthPercentage(100);
            filterTable.setSpacingBefore(10);
            filterTable.setSpacingAfter(20);

            PdfPCell filterCell = new PdfPCell();
            filterCell.setBackgroundColor(LIGHT_GOLD);
            filterCell.setPadding(15);
            filterCell.setBorder(Rectangle.NO_BORDER);

            Paragraph filterText = new Paragraph(
                report.getAppliedFilters().getFilterDescription(),
                NORMAL_FONT
            );
            filterCell.addElement(filterText);
            filterTable.addCell(filterCell);
            document.add(filterTable);
        }

        // Resumen ejecutivo
        if (report.getExecutiveSummary() != null) {
            addSubsectionTitle(document, "Resumen Ejecutivo");

            CompletedModalitiesReportDTO.ExecutiveSummaryDTO summary = report.getExecutiveSummary();

            // Métricas principales en tarjetas
            PdfPTable metricsTable = new PdfPTable(5);
            metricsTable.setWidthPercentage(100);
            metricsTable.setSpacingBefore(10);
            metricsTable.setSpacingAfter(20);

            addMetricCard(metricsTable, "Total Completadas",
                String.valueOf(summary.getTotalCompleted()), INSTITUTIONAL_GOLD);
            addMetricCard(metricsTable, "Exitosas",
                String.valueOf(summary.getTotalSuccessful()), INSTITUTIONAL_GOLD);
            addMetricCard(metricsTable, "Fallidas",
                String.valueOf(summary.getTotalFailed()), INSTITUTIONAL_RED);
            addMetricCard(metricsTable, "Tasa de Éxito",
                String.format("%.1f%%", summary.getSuccessRate()), INSTITUTIONAL_GOLD);
            addMetricCard(metricsTable, "Con Distinción",
                String.valueOf(summary.getWithDistinction()), INSTITUTIONAL_GOLD);

            document.add(metricsTable);

            // Segunda fila de métricas
            PdfPTable metrics2Table = new PdfPTable(4);
            metrics2Table.setWidthPercentage(100);
            metrics2Table.setSpacingBefore(5);
            metrics2Table.setSpacingAfter(15);

            addMetricCard(metrics2Table, "Calificación Promedio",
                String.format("%.2f", summary.getAverageGrade()), INSTITUTIONAL_GOLD);
            addMetricCard(metrics2Table, "Días Promedio",
                String.format("%.0f", summary.getAverageCompletionDays()), INSTITUTIONAL_RED);
            addMetricCard(metrics2Table, "Total Estudiantes",
                String.valueOf(summary.getTotalStudents()), INSTITUTIONAL_GOLD);
            addMetricCard(metrics2Table, "Directores Únicos",
                String.valueOf(summary.getUniqueDirectors()), TEXT_GRAY);

            document.add(metrics2Table);
        }
    }

    /**
     * Estadísticas generales
     */
    private void addGeneralStatistics(Document document, CompletedModalitiesReportDTO report)
            throws DocumentException {

        addSectionTitle(document, "2. ESTADÍSTICAS GENERALES");

        if (report.getGeneralStatistics() == null) {
            document.add(new Paragraph("No hay estadísticas disponibles.", NORMAL_FONT));
            return;
        }

        CompletedModalitiesReportDTO.GeneralStatisticsDTO stats = report.getGeneralStatistics();

        // Resultados
        addSubsectionTitle(document, "Resultados Generales");

        PdfPTable resultsTable = new PdfPTable(4);
        resultsTable.setWidthPercentage(90);
        resultsTable.setSpacingBefore(10);
        resultsTable.setSpacingAfter(20);
        resultsTable.setHorizontalAlignment(Element.ALIGN_CENTER);

        addStatsCard(resultsTable, "Aprobadas",
            String.valueOf(stats.getApproved()), INSTITUTIONAL_GOLD);
        addStatsCard(resultsTable, "Reprobadas",
            String.valueOf(stats.getFailed()), INSTITUTIONAL_RED);
        addStatsCard(resultsTable, "Tasa Aprobación",
            String.format("%.1f%%", stats.getApprovalRate()), INSTITUTIONAL_GOLD);
        addStatsCard(resultsTable, "Total",
            String.valueOf(stats.getTotalCompleted()), INSTITUTIONAL_RED);

        document.add(resultsTable);

        // Tiempos de completitud
        addSubsectionTitle(document, "Tiempos de Completitud (días)");

        PdfPTable timeTable = new PdfPTable(2);
        timeTable.setWidthPercentage(80);
        timeTable.setWidths(new float[]{2f, 1f});
        timeTable.setSpacingBefore(10);
        timeTable.setSpacingAfter(15);
        timeTable.setHorizontalAlignment(Element.ALIGN_CENTER);

        addStatRow(timeTable, "Promedio:",
            String.format("%.0f días", stats.getAverageCompletionDays()));
        addStatRow(timeTable, "Más Rápida:",
            stats.getFastestCompletionDays() != null ?
            stats.getFastestCompletionDays() + " días" : "N/D");
        addStatRow(timeTable, "Más Lenta:",
            stats.getSlowestCompletionDays() != null ?
            stats.getSlowestCompletionDays() + " días" : "N/D");
        addStatRow(timeTable, "Mediana:",
            stats.getMedianCompletionDays() != null ?
            String.format("%.0f días", stats.getMedianCompletionDays()) : "N/D");

        document.add(timeTable);

        // Calificaciones
        addSubsectionTitle(document, "Calificaciones");

        PdfPTable gradeTable = new PdfPTable(2);
        gradeTable.setWidthPercentage(80);
        gradeTable.setWidths(new float[]{2f, 1f});
        gradeTable.setSpacingBefore(10);
        gradeTable.setSpacingAfter(15);
        gradeTable.setHorizontalAlignment(Element.ALIGN_CENTER);

        addStatRow(gradeTable, "Promedio General:",
            String.format("%.2f", stats.getAverageGrade()));
        addStatRow(gradeTable, "Calificación Más Alta:",
            stats.getHighestGrade() != null ?
            String.format("%.2f", stats.getHighestGrade()) : "N/D");
        addStatRow(gradeTable, "Calificación Más Baja:",
            stats.getLowestGrade() != null ?
            String.format("%.2f", stats.getLowestGrade()) : "N/D");
        addStatRow(gradeTable, "Mediana:",
            stats.getMedianGrade() != null ?
            String.format("%.2f", stats.getMedianGrade()) : "N/D");

        document.add(gradeTable);

        // Distinciones académicas
        addSubsectionTitle(document, "Distinciones Académicas");

        PdfPTable distinctionTable = new PdfPTable(3);
        distinctionTable.setWidthPercentage(90);
        distinctionTable.setSpacingBefore(10);
        distinctionTable.setSpacingAfter(15);
        distinctionTable.setHorizontalAlignment(Element.ALIGN_CENTER);

        addStatsCard(distinctionTable, "Meritoria",
            String.valueOf(stats.getWithMeritorious()), INSTITUTIONAL_GOLD);
        addStatsCard(distinctionTable, "Laureada",
            String.valueOf(stats.getWithLaudeate()), INSTITUTIONAL_GOLD);
        addStatsCard(distinctionTable, "Sin Distinción",
            String.valueOf(stats.getWithoutDistinction()), LIGHT_GOLD);

        document.add(distinctionTable);
    }

    /**
     * Análisis por resultado
     */
    private void addResultAnalysis(Document document, CompletedModalitiesReportDTO report)
            throws DocumentException {

        addSectionTitle(document, "3. ANÁLISIS POR RESULTADO");

        if (report.getResultAnalysis() == null) {
            document.add(new Paragraph("No hay análisis de resultado disponible.", NORMAL_FONT));
            return;
        }

        CompletedModalitiesReportDTO.ResultAnalysisDTO analysis = report.getResultAnalysis();

        // Comparativa visual
        PdfPTable comparisonTable = new PdfPTable(2);
        comparisonTable.setWidthPercentage(100);
        comparisonTable.setSpacingBefore(10);
        comparisonTable.setSpacingAfter(20);

        // Columna exitosas
        PdfPCell successCell = new PdfPCell();
        successCell.setBackgroundColor(new BaseColor(232, 245, 233));
        successCell.setPadding(15);
        successCell.setBorder(Rectangle.BOX);
        successCell.setBorderColor(INSTITUTIONAL_GOLD);
        successCell.setBorderWidth(2);

        Paragraph successTitle = new Paragraph("✓ EXITOSAS",
            FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, INSTITUTIONAL_GOLD));
        successTitle.setAlignment(Element.ALIGN_CENTER);
        successCell.addElement(successTitle);
        successCell.addElement(new Paragraph("\n"));

        successCell.addElement(new Paragraph("Cantidad: " + analysis.getSuccessfulCount(), BOLD_FONT));
        successCell.addElement(new Paragraph("Tasa: " + String.format("%.1f%%", analysis.getSuccessRate()), NORMAL_FONT));
        successCell.addElement(new Paragraph("Calificación Promedio: " +
            String.format("%.2f", analysis.getAverageSuccessGrade()), NORMAL_FONT));
        successCell.addElement(new Paragraph("Días Promedio: " +
            String.format("%.0f", analysis.getAverageSuccessCompletionDays()), NORMAL_FONT));

        if (analysis.getSuccessFactors() != null && !analysis.getSuccessFactors().isEmpty()) {
            successCell.addElement(new Paragraph("\nFactores de Éxito:",
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, INSTITUTIONAL_GOLD)));
            for (String factor : analysis.getSuccessFactors()) {
                successCell.addElement(new Paragraph("• " + factor, SMALL_FONT));
            }
        }

        comparisonTable.addCell(successCell);

        // Columna fallidas
        PdfPCell failedCell = new PdfPCell();
        failedCell.setBackgroundColor(new BaseColor(255, 235, 238));
        failedCell.setPadding(15);
        failedCell.setBorder(Rectangle.BOX);
        failedCell.setBorderColor(INSTITUTIONAL_RED);
        failedCell.setBorderWidth(2);

        Paragraph failedTitle = new Paragraph("✗ FALLIDAS",
            FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, INSTITUTIONAL_RED));
        failedTitle.setAlignment(Element.ALIGN_CENTER);
        failedCell.addElement(failedTitle);
        failedCell.addElement(new Paragraph("\n"));

        failedCell.addElement(new Paragraph("Cantidad: " + analysis.getFailedCount(), BOLD_FONT));
        failedCell.addElement(new Paragraph("Tasa: " + String.format("%.1f%%", analysis.getFailureRate()), NORMAL_FONT));
        failedCell.addElement(new Paragraph("Calificación Promedio: " +
            String.format("%.2f", analysis.getAverageFailureGrade()), NORMAL_FONT));
        failedCell.addElement(new Paragraph("Días Promedio: " +
            String.format("%.0f", analysis.getAverageFailureCompletionDays()), NORMAL_FONT));

        if (analysis.getFailureReasons() != null && !analysis.getFailureReasons().isEmpty()) {
            failedCell.addElement(new Paragraph("\nRazones de Fallo:",
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, INSTITUTIONAL_RED)));
            for (String reason : analysis.getFailureReasons()) {
                failedCell.addElement(new Paragraph("• " + reason, SMALL_FONT));
            }
        }

        comparisonTable.addCell(failedCell);
        document.add(comparisonTable);

        // Veredicto de desempeño
        if (analysis.getPerformanceVerdict() != null) {
            PdfPTable verdictTable = new PdfPTable(1);
            verdictTable.setWidthPercentage(80);
            verdictTable.setSpacingBefore(15);
            verdictTable.setSpacingAfter(15);
            verdictTable.setHorizontalAlignment(Element.ALIGN_CENTER);

            PdfPCell verdictCell = new PdfPCell();
            BaseColor verdictColor = getVerdictColor(analysis.getPerformanceVerdict());
            verdictCell.setBackgroundColor(verdictColor);
            verdictCell.setPadding(12);
            verdictCell.setBorder(Rectangle.NO_BORDER);

            Paragraph verdictText = new Paragraph(
                "DESEMPEÑO: " + translateVerdict(analysis.getPerformanceVerdict()),
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, WHITE)
            );
            verdictText.setAlignment(Element.ALIGN_CENTER);
            verdictCell.addElement(verdictText);
            verdictTable.addCell(verdictCell);
            document.add(verdictTable);
        }

        // Recomendaciones
        if (analysis.getRecommendations() != null && !analysis.getRecommendations().isEmpty()) {
            addSubsectionTitle(document, "Recomendaciones");

            PdfPTable recTable = new PdfPTable(1);
            recTable.setWidthPercentage(95);
            recTable.setSpacingBefore(10);

            for (String rec : analysis.getRecommendations()) {
                PdfPCell recCell = new PdfPCell();
                recCell.setBackgroundColor(new BaseColor(255, 248, 225));
                recCell.setPadding(10);
                recCell.setBorder(Rectangle.NO_BORDER);
                recCell.setPhrase(new Phrase("→ " + rec, NORMAL_FONT));
                recTable.addCell(recCell);
            }

            document.add(recTable);
        }
    }

    // Continúa en el siguiente mensaje debido a límites de longitud...

    private BaseColor getVerdictColor(String verdict) {
        switch (verdict) {
            case "EXCELLENT": return INSTITUTIONAL_GOLD;
            case "GOOD": return INSTITUTIONAL_GOLD;
            case "REGULAR": return INSTITUTIONAL_RED;
            default: return INSTITUTIONAL_RED;
        }
    }

    private String translateVerdict(String verdict) {
        switch (verdict) {
            case "EXCELLENT": return "EXCELENTE";
            case "GOOD": return "BUENO";
            case "REGULAR": return "REGULAR";
            case "NEEDS_IMPROVEMENT": return "NECESITA MEJORA";
            default: return verdict;
        }
    }

    // Continuará en siguiente archivo...

    /**
     * Análisis por tipo de modalidad
     */
    private void addModalityTypeAnalysis(Document document, CompletedModalitiesReportDTO report)
            throws DocumentException {

        addSectionTitle(document, "4. ANÁLISIS POR TIPO DE MODALIDAD");

        if (report.getModalityTypeAnalysis() == null || report.getModalityTypeAnalysis().isEmpty()) {
            document.add(new Paragraph("No hay análisis por tipo disponible.", NORMAL_FONT));
            return;
        }

        // Tabla de análisis
        PdfPTable table = new PdfPTable(6);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{2.5f, 1f, 1f, 1f, 1f, 1.2f});
        table.setSpacingBefore(10);
        table.setSpacingAfter(15);

        // Encabezados
        addTableHeader(table, "Tipo de Modalidad");
        addTableHeader(table, "Total");
        addTableHeader(table, "Exitosas");
        addTableHeader(table, "Fallidas");
        addTableHeader(table, "Tasa Éxito");
        addTableHeader(table, "Desempeño");

        // Datos
        boolean alternate = false;
        for (CompletedModalitiesReportDTO.ModalityTypeAnalysisDTO analysis : report.getModalityTypeAnalysis()) {
            addTableCell(table, analysis.getModalityType(), alternate);
            addTableCell(table, String.valueOf(analysis.getTotalCompleted()), alternate);
            addTableCell(table, String.valueOf(analysis.getSuccessful()), alternate);
            addTableCell(table, String.valueOf(analysis.getFailed()), alternate);
            addTableCell(table, String.format("%.1f%%", analysis.getSuccessRate()), alternate);
            addTableCell(table, translatePerformance(analysis.getPerformance()), alternate);

            alternate = !alternate;
        }

        document.add(table);
    }

    private String translatePerformance(String performance) {
        switch (performance) {
            case "EXCELLENT": return "Excelente";
            case "GOOD": return "Bueno";
            case "REGULAR": return "Regular";
            case "POOR": return "Bajo";
            default: return performance;
        }
    }

    /**
     * Listado detallado de modalidades completadas
     */
    private void addCompletedModalitiesListing(Document document, CompletedModalitiesReportDTO report)
            throws DocumentException {

        addSectionTitle(document, "5. LISTADO DETALLADO DE MODALIDADES COMPLETADAS");

        if (report.getCompletedModalities() == null || report.getCompletedModalities().isEmpty()) {
            document.add(new Paragraph("No hay modalidades para mostrar.", NORMAL_FONT));
            return;
        }

        // Tabla detallada
        PdfPTable table = new PdfPTable(7);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{1.8f, 0.8f, 1.2f, 0.8f, 0.8f, 1.5f, 1f});
        table.setSpacingBefore(10);
        table.setHeaderRows(1);

        // Encabezados
        addTableHeader(table, "Modalidad");
        addTableHeader(table, "Resultado");
        addTableHeader(table, "Estudiantes");
        addTableHeader(table, "Calif.");
        addTableHeader(table, "Días");
        addTableHeader(table, "Director");
        addTableHeader(table, "Distinción");

        // Datos (limitar a primeros 50 para no sobrecargar)
        boolean alternate = false;
        int count = 0;
        for (CompletedModalitiesReportDTO.CompletedModalityDetailDTO detail : report.getCompletedModalities()) {
            if (count++ >= 50) break;

            addTableCell(table, truncate(detail.getModalityTypeName(), 25), alternate);

            PdfPCell resultCell = new PdfPCell(new Phrase(
                "SUCCESS".equals(detail.getResult()) ? "✓" : "✗",
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9,
                    "SUCCESS".equals(detail.getResult()) ? INSTITUTIONAL_GOLD : INSTITUTIONAL_RED)
            ));
            resultCell.setBackgroundColor(alternate ? LIGHT_GOLD : WHITE);
            resultCell.setBorder(Rectangle.BOTTOM);
            resultCell.setBorderColor(LIGHT_GOLD);
            resultCell.setBorderWidth(0.3f);
            resultCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            resultCell.setPadding(5);
            table.addCell(resultCell);

            String studentNames = detail.getStudents() != null ?
                detail.getStudents().stream()
                    .map(s -> s.getFullName())
                    .collect(java.util.stream.Collectors.joining(", ")) : "N/D";
            addTableCell(table, truncate(studentNames, 25), alternate);

            addTableCell(table, detail.getFinalGrade() != null ?
                String.format("%.2f", detail.getFinalGrade()) : "N/D", alternate);
            addTableCell(table, detail.getCompletionDays() != null ?
                String.valueOf(detail.getCompletionDays()) : "N/D", alternate);
            addTableCell(table, detail.getDirectorName() != null ?
                truncate(detail.getDirectorName(), 20) : "Sin asignar", alternate);
            addTableCell(table, detail.getAcademicDistinction() != null ?
                translateDistinction(detail.getAcademicDistinction()) : "-", alternate);

            alternate = !alternate;
        }

        document.add(table);

        if (report.getCompletedModalities().size() > 50) {
            Paragraph note = new Paragraph(
                "* Se muestran las primeras 50 modalidades. Total: " + report.getCompletedModalities().size(),
                TINY_FONT
            );
            note.setSpacingBefore(5);
            document.add(note);
        }
    }

    private String translateDistinction(String distinction) {
        switch (distinction) {
            case "MERITORIOUS": return "Meritoria";
            case "LAUREATE": return "Laureada";
            default: return distinction;
        }
    }

    /**
     * Análisis temporal
     */
    private void addTemporalAnalysis(Document document, CompletedModalitiesReportDTO report)
            throws DocumentException {

        addSectionTitle(document, "6. ANÁLISIS TEMPORAL");

        if (report.getTemporalAnalysis() == null) {
            document.add(new Paragraph("No hay análisis temporal disponible.", NORMAL_FONT));
            return;
        }

        CompletedModalitiesReportDTO.TemporalAnalysisDTO temporal = report.getTemporalAnalysis();

        // Indicadores de tendencia
        PdfPTable trendTable = new PdfPTable(4);
        trendTable.setWidthPercentage(100);
        trendTable.setSpacingBefore(10);
        trendTable.setSpacingAfter(20);

        addStatsCard(trendTable, "Tendencia",
            translateTrend(temporal.getTrend()), getTrendColor(temporal.getTrend()));
        addStatsCard(trendTable, "Tasa de Crecimiento",
            String.format("%.1f%%", temporal.getGrowthRate()), INSTITUTIONAL_GOLD);
        addStatsCard(trendTable, "Mejor Periodo",
            temporal.getBestPeriod() != null ? temporal.getBestPeriod() : "N/D", INSTITUTIONAL_GOLD);
        addStatsCard(trendTable, "Peor Periodo",
            temporal.getWorstPeriod() != null ? temporal.getWorstPeriod() : "N/D", INSTITUTIONAL_RED);

        document.add(trendTable);

        // Tabla de datos por periodo
        if (temporal.getPeriodData() != null && !temporal.getPeriodData().isEmpty()) {
            addSubsectionTitle(document, "Datos por Periodo Académico");

            PdfPTable periodTable = new PdfPTable(6);
            periodTable.setWidthPercentage(100);
            periodTable.setWidths(new float[]{1.2f, 1f, 1f, 1f, 1.2f, 1f});
            periodTable.setSpacingBefore(10);

            addTableHeader(periodTable, "Periodo");
            addTableHeader(periodTable, "Completadas");
            addTableHeader(periodTable, "Exitosas");
            addTableHeader(periodTable, "Fallidas");
            addTableHeader(periodTable, "Tasa Éxito");
            addTableHeader(periodTable, "Calif. Prom.");

            boolean alternate = false;
            for (CompletedModalitiesReportDTO.PeriodDataDTO period : temporal.getPeriodData()) {
                addTableCell(periodTable, period.getPeriod(), alternate);
                addTableCell(periodTable, String.valueOf(period.getCompleted()), alternate);
                addTableCell(periodTable, String.valueOf(period.getSuccessful()), alternate);
                addTableCell(periodTable, String.valueOf(period.getFailed()), alternate);
                addTableCell(periodTable, String.format("%.1f%%", period.getSuccessRate()), alternate);
                addTableCell(periodTable, String.format("%.2f", period.getAverageGrade()), alternate);

                alternate = !alternate;
            }

            document.add(periodTable);
        }
    }

    private String translateTrend(String trend) {
        switch (trend) {
            case "IMPROVING": return "Mejorando";
            case "STABLE": return "Estable";
            case "DECLINING": return "Declinando";
            default: return trend;
        }
    }

    private BaseColor getTrendColor(String trend) {
        switch (trend) {
            case "IMPROVING": return INSTITUTIONAL_GOLD;
            case "STABLE": return INSTITUTIONAL_GOLD;
            case "DECLINING": return INSTITUTIONAL_RED;
            default: return LIGHT_GOLD;
        }
    }

    /**
     * Desempeño de directores
     */
    private void addDirectorPerformance(Document document, CompletedModalitiesReportDTO report)
            throws DocumentException {

        addSectionTitle(document, "7. DESEMPEÑO DE DIRECTORES");

        if (report.getDirectorPerformance() == null) {
            document.add(new Paragraph("No hay datos de desempeño de directores.", NORMAL_FONT));
            return;
        }

        CompletedModalitiesReportDTO.DirectorPerformanceDTO performance = report.getDirectorPerformance();

        // Indicadores generales
        PdfPTable indicatorsTable = new PdfPTable(3);
        indicatorsTable.setWidthPercentage(90);
        indicatorsTable.setSpacingBefore(10);
        indicatorsTable.setSpacingAfter(20);
        indicatorsTable.setHorizontalAlignment(Element.ALIGN_CENTER);

        addStatsCard(indicatorsTable, "Total Directores",
            String.valueOf(performance.getTotalDirectors()), INSTITUTIONAL_RED);
        addStatsCard(indicatorsTable, "Tasa Éxito Prom.",
            String.format("%.1f%%", performance.getAverageSuccessRateByDirector()), INSTITUTIONAL_GOLD);
        addStatsCard(indicatorsTable, "Mejor Director",
            performance.getBestDirector() != null ?
            truncate(performance.getBestDirector(), 15) : "N/D", INSTITUTIONAL_GOLD);

        document.add(indicatorsTable);

        // Top directores
        if (performance.getTopDirectors() != null && !performance.getTopDirectors().isEmpty()) {
            addSubsectionTitle(document, "Top 10 Directores por Desempeño");

            PdfPTable directorTable = new PdfPTable(6);
            directorTable.setWidthPercentage(100);
            directorTable.setWidths(new float[]{2.5f, 1f, 1f, 1f, 1.2f, 1f});
            directorTable.setSpacingBefore(10);

            addTableHeader(directorTable, "Director");
            addTableHeader(directorTable, "Total");
            addTableHeader(directorTable, "Exitosas");
            addTableHeader(directorTable, "Fallidas");
            addTableHeader(directorTable, "Tasa Éxito");
            addTableHeader(directorTable, "Distinciones");

            boolean alternate = false;
            for (CompletedModalitiesReportDTO.TopDirectorDTO director : performance.getTopDirectors()) {
                addTableCell(directorTable, truncate(director.getDirectorName(), 30), alternate);
                addTableCell(directorTable, String.valueOf(director.getTotalSupervised()), alternate);
                addTableCell(directorTable, String.valueOf(director.getSuccessful()), alternate);
                addTableCell(directorTable, String.valueOf(director.getFailed()), alternate);
                addTableCell(directorTable, String.format("%.1f%%", director.getSuccessRate()), alternate);
                addTableCell(directorTable, String.valueOf(director.getWithDistinction()), alternate);

                alternate = !alternate;
            }

            document.add(directorTable);
        }
    }

    /**
     * Análisis de distinciones académicas
     */
    private void addDistinctionAnalysis(Document document, CompletedModalitiesReportDTO report)
            throws DocumentException {

        addSectionTitle(document, "8. ANÁLISIS DE DISTINCIONES ACADÉMICAS");

        if (report.getDistinctionAnalysis() == null) {
            document.add(new Paragraph("No hay análisis de distinciones disponible.", NORMAL_FONT));
            return;
        }

        CompletedModalitiesReportDTO.DistinctionAnalysisDTO distinction = report.getDistinctionAnalysis();

        // Indicadores principales
        PdfPTable indicatorsTable = new PdfPTable(4);
        indicatorsTable.setWidthPercentage(100);
        indicatorsTable.setSpacingBefore(10);
        indicatorsTable.setSpacingAfter(20);

        addStatsCard(indicatorsTable, "Total con Distinción",
            String.valueOf(distinction.getTotalWithDistinction()), INSTITUTIONAL_GOLD);
        addStatsCard(indicatorsTable, "Meritorias",
            String.valueOf(distinction.getMeritorious()), new BaseColor(255, 152, 0));
        addStatsCard(indicatorsTable, "Laureadas",
            String.valueOf(distinction.getLaureate()), INSTITUTIONAL_GOLD);
        addStatsCard(indicatorsTable, "Tasa Distinción",
            String.format("%.1f%%", distinction.getDistinctionRate()), INSTITUTIONAL_GOLD);

        document.add(indicatorsTable);

        // Modalidades con más distinciones
        if (distinction.getModalitiesWithMostDistinctions() != null &&
            !distinction.getModalitiesWithMostDistinctions().isEmpty()) {

            addSubsectionTitle(document, "Modalidades con Más Distinciones");

            PdfPTable modalityTable = new PdfPTable(1);
            modalityTable.setWidthPercentage(90);
            modalityTable.setSpacingBefore(10);
            modalityTable.setSpacingAfter(15);
            modalityTable.setHorizontalAlignment(Element.ALIGN_CENTER);

            for (String modality : distinction.getModalitiesWithMostDistinctions()) {
                PdfPCell cell = new PdfPCell(new Phrase("★ " + modality, NORMAL_FONT));
                cell.setBackgroundColor(new BaseColor(255, 248, 225));
                cell.setPadding(8);
                cell.setBorder(Rectangle.NO_BORDER);
                modalityTable.addCell(cell);
            }

            document.add(modalityTable);
        }

        // Directores con más distinciones
        if (distinction.getDirectorsWithMostDistinctions() != null &&
            !distinction.getDirectorsWithMostDistinctions().isEmpty()) {

            addSubsectionTitle(document, "Directores con Más Distinciones");

            PdfPTable directorTable = new PdfPTable(1);
            directorTable.setWidthPercentage(90);
            directorTable.setSpacingBefore(10);
            directorTable.setHorizontalAlignment(Element.ALIGN_CENTER);

            for (String director : distinction.getDirectorsWithMostDistinctions()) {
                PdfPCell cell = new PdfPCell(new Phrase("★ " + director, NORMAL_FONT));
                cell.setBackgroundColor(LIGHT_GOLD);
                cell.setPadding(8);
                cell.setBorder(Rectangle.NO_BORDER);
                directorTable.addCell(cell);
            }

            document.add(directorTable);
        }
    }

    // ==================== MÉTODOS AUXILIARES ====================

    /**
     * Tarjeta de métrica
     */
    private void addMetricCard(PdfPTable table, String label, String value, BaseColor color) {
        PdfPCell cell = new PdfPCell();
        cell.setBackgroundColor(color);
        cell.setPadding(10);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setFixedHeight(60);

        Paragraph content = new Paragraph();
        content.add(new Chunk(value + "\n",
            FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, WHITE)));
        content.add(new Chunk(label,
            FontFactory.getFont(FontFactory.HELVETICA, 8, new BaseColor(240, 240, 240))));
        content.setAlignment(Element.ALIGN_CENTER);

        cell.addElement(content);
        table.addCell(cell);
    }

    /**
     * Tarjeta de estadística
     */
    private void addStatsCard(PdfPTable table, String label, String value, BaseColor color) {
        PdfPCell cell = new PdfPCell();
        cell.setBackgroundColor(color);
        cell.setPadding(12);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setFixedHeight(55);

        Paragraph content = new Paragraph();
        content.add(new Chunk(value + "\n",
            FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, WHITE)));
        content.add(new Chunk(label,
            FontFactory.getFont(FontFactory.HELVETICA, 8, new BaseColor(240, 240, 240))));
        content.setAlignment(Element.ALIGN_CENTER);

        cell.addElement(content);
        table.addCell(cell);
    }

    /**
     * Fila de información
     */
    private void addInfoRow(PdfPTable table, String label, String value) {
        PdfPCell labelCell = new PdfPCell(new Phrase(label, BOLD_FONT));
        labelCell.setPadding(8);
        labelCell.setBackgroundColor(LIGHT_GOLD);
        labelCell.setBorder(Rectangle.NO_BORDER);
        table.addCell(labelCell);

        PdfPCell valueCell = new PdfPCell(new Phrase(value, NORMAL_FONT));
        valueCell.setPadding(8);
        valueCell.setBackgroundColor(WHITE);
        valueCell.setBorder(Rectangle.NO_BORDER);
        table.addCell(valueCell);
    }

    /**
     * Fila de estadística
     */
    private void addStatRow(PdfPTable table, String label, String value) {
        PdfPCell labelCell = new PdfPCell(new Phrase(label, NORMAL_FONT));
        labelCell.setPadding(8);
        labelCell.setBorder(Rectangle.NO_BORDER);
        table.addCell(labelCell);

        PdfPCell valueCell = new PdfPCell(new Phrase(value, BOLD_FONT));
        valueCell.setPadding(8);
        valueCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        valueCell.setBackgroundColor(LIGHT_GOLD);
        valueCell.setBorder(Rectangle.NO_BORDER);
        table.addCell(valueCell);
    }

    /**
     * Encabezado de tabla
     */
    private void addTableHeader(PdfPTable table, String text) {
        PdfPCell cell = new PdfPCell(new Phrase(text, TABLE_HEADER_FONT));
        cell.setPadding(6);
        cell.setBackgroundColor(INSTITUTIONAL_RED);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setBorder(Rectangle.NO_BORDER);
        table.addCell(cell);
    }

    /**
     * Celda de tabla
     */
    private void addTableCell(PdfPTable table, String text, boolean alternate) {
        PdfPCell cell = new PdfPCell(new Phrase(text != null ? text : "", TABLE_FONT));
        cell.setPadding(5);
        cell.setBackgroundColor(alternate ? LIGHT_GOLD : WHITE);
        cell.setBorder(Rectangle.BOTTOM);
        cell.setBorderColor(LIGHT_GOLD);
        cell.setBorderWidth(0.3f);
        table.addCell(cell);
    }

    /**
     * Título de sección
     */
    private void addSectionTitle(Document document, String title) throws DocumentException {
        Paragraph section = new Paragraph(title, HEADER_FONT);
        section.setSpacingBefore(15);
        section.setSpacingAfter(10);

        LineSeparator line = new LineSeparator();
        line.setLineColor(INSTITUTIONAL_RED);
        line.setLineWidth(2);

        document.add(section);
        document.add(new Chunk(line));
        document.add(new Paragraph("\n"));
    }

    /**
     * Título de subsección
     */
    private void addSubsectionTitle(Document document, String title) throws DocumentException {
        Paragraph subsection = new Paragraph(title, SUBHEADER_FONT);
        subsection.setSpacingBefore(10);
        subsection.setSpacingAfter(8);
        document.add(subsection);
    }

    /**
     * Trunca texto
     */
    private String truncate(String text, int maxLength) {
        if (text == null) return "";
        return text.length() > maxLength ? text.substring(0, maxLength - 3) + "..." : text;
    }

    // ==================== CLASE INTERNA: PAGE EVENT HELPER ====================

    /**
     * Helper para eventos de página
     */
    private static class CompletedModalitiesPageEventHelper extends PdfPageEventHelper {

        private final CompletedModalitiesReportDTO report;
        private final Font headerFont = FontFactory.getFont(FontFactory.HELVETICA, 8, TEXT_GRAY);
        private final Font footerFont = FontFactory.getFont(FontFactory.HELVETICA, 7, TEXT_GRAY);

        public CompletedModalitiesPageEventHelper(CompletedModalitiesReportDTO report) {
            this.report = report;
        }

        @Override
        public void onEndPage(PdfWriter writer, Document document) {
            PdfContentByte cb = writer.getDirectContent();

            // Encabezado
            if (writer.getPageNumber() > 1) {
                Phrase header = new Phrase(
                    "Modalidades Completadas - " + report.getAcademicProgramName(),
                    headerFont
                );
                ColumnText.showTextAligned(cb, Element.ALIGN_LEFT, header,
                        document.left(), document.top() + 20, 0);
            }

            // Pie de página
            Phrase footer = new Phrase(
                "Página " + writer.getPageNumber() + " | " +
                java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
                footerFont
            );
            ColumnText.showTextAligned(cb, Element.ALIGN_CENTER, footer,
                    (document.right() + document.left()) / 2,
                    document.bottom() - 20, 0);

            // Sistema
            Phrase system = new Phrase("SIGMA - Universidad Surcolombiana", footerFont);
            ColumnText.showTextAligned(cb, Element.ALIGN_RIGHT, system,
                    document.right(), document.bottom() - 20, 0);
        }
    }
}



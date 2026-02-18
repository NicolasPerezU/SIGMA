package com.SIGMA.USCO.report.service;

import com.SIGMA.USCO.report.dto.StudentListingReportDTO;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.itextpdf.text.pdf.draw.LineSeparator;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio para generar PDF del reporte de listado de estudiantes con filtros
 * Diseño profesional y universitario con máxima información relevante
 */
@Service
public class StudentListingPdfGenerator {

    // Colores institucionales
    private static final BaseColor INSTITUTIONAL_RED = new BaseColor(143, 30, 30); // #8F1E1E
    private static final BaseColor INSTITUTIONAL_GOLD = new BaseColor(213, 203, 160); // #D5CBA0
    private static final BaseColor LIGHT_GOLD = new BaseColor(245, 242, 235); // Tono claro de dorado para fondos

    // Fuentes con colores institucionales
    private static final Font TITLE_FONT = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 22, INSTITUTIONAL_RED);
    private static final Font SUBTITLE_FONT = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, INSTITUTIONAL_RED);
    private static final Font HEADER_FONT = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 15, INSTITUTIONAL_RED);
    private static final Font SUBHEADER_FONT = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, INSTITUTIONAL_RED);
    private static final Font BOLD_FONT = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, BaseColor.BLACK);
    private static final Font NORMAL_FONT = FontFactory.getFont(FontFactory.HELVETICA, 10, BaseColor.BLACK);
    private static final Font SMALL_FONT = FontFactory.getFont(FontFactory.HELVETICA, 9, BaseColor.DARK_GRAY);
    private static final Font TINY_FONT = FontFactory.getFont(FontFactory.HELVETICA, 8, BaseColor.GRAY);
    private static final Font TABLE_HEADER_FONT = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8, BaseColor.WHITE);
    private static final Font TABLE_FONT = FontFactory.getFont(FontFactory.HELVETICA, 7, BaseColor.BLACK);

    // Colores auxiliares
    private static final BaseColor SUCCESS_COLOR = new BaseColor(40, 167, 69);
    private static final BaseColor WARNING_COLOR = new BaseColor(255, 193, 7);
    private static final BaseColor DANGER_COLOR = new BaseColor(220, 53, 69);
    private static final BaseColor INFO_COLOR = new BaseColor(23, 162, 184);
    private static final BaseColor TEXT_DARK = new BaseColor(33, 37, 41);

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public ByteArrayOutputStream generatePDF(StudentListingReportDTO report)
            throws DocumentException, IOException {

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4.rotate(), 40, 40, 50, 50); // Landscape para más espacio
        PdfWriter writer = PdfWriter.getInstance(document, outputStream);

        // Agregar eventos de página
        StudentListingPageEventHelper pageEvent = new StudentListingPageEventHelper(report);
        writer.setPageEvent(pageEvent);

        document.open();

        // 1. Portada
        addCoverPage(document, report);

        // 2. Filtros Aplicados y Resumen Ejecutivo
        document.newPage();
        addFiltersAndExecutiveSummary(document, report);

        // 3. Estadísticas Generales
        document.newPage();
        addGeneralStatistics(document, report);

        // 4. Análisis de Distribución
        addDistributionAnalysis(document, report);

        // 5. Listado Detallado de Estudiantes
        document.newPage();
        addStudentListing(document, report);

        // 6. Estadísticas por Modalidad
        document.newPage();
        addModalityStatistics(document, report);

        // 7. Estadísticas por Estado
        document.newPage();
        addStatusStatistics(document, report);

        // 8. Estadísticas por Semestre
        document.newPage();
        addSemesterStatistics(document, report);

        document.close();
        return outputStream;
    }

    /**
     * Portada del reporte
     */
    private void addCoverPage(Document document, StudentListingReportDTO report)
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
            FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, BaseColor.WHITE));
        bandContent.add(universityName);

        Chunk programName = new Chunk(report.getAcademicProgramName() + "\n",
            FontFactory.getFont(FontFactory.HELVETICA, 14, BaseColor.WHITE));
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
        Paragraph title = new Paragraph("REPORTE DE LISTADO DE ESTUDIANTES", TITLE_FONT);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(15);
        document.add(title);

        Paragraph subtitle = new Paragraph("Modalidades de Grado", SUBTITLE_FONT);
        subtitle.setAlignment(Element.ALIGN_CENTER);
        subtitle.setSpacingAfter(40);
        document.add(subtitle);

        // Cuadro de información del reporte
        PdfPTable infoTable = new PdfPTable(2);
        infoTable.setWidthPercentage(80);
        infoTable.setWidths(new float[]{1.2f, 1.8f});
        infoTable.setSpacingBefore(30);
        infoTable.setHorizontalAlignment(Element.ALIGN_CENTER);

        addInfoRow(infoTable, "Fecha de Generación:",
            report.getGeneratedAt().format(DATETIME_FORMATTER));
        addInfoRow(infoTable, "Generado Por:", report.getGeneratedBy());
        addInfoRow(infoTable, "Total de Estudiantes:",
            String.valueOf(report.getStudents() != null ? report.getStudents().size() : 0));

        if (report.getAppliedFilters() != null && report.getAppliedFilters().getHasFilters()) {
            addInfoRow(infoTable, "Filtros Aplicados:", "Sí");
        } else {
            addInfoRow(infoTable, "Filtros Aplicados:", "No - Listado Completo");
        }

        document.add(infoTable);

        // Línea separadora decorativa
        document.add(new Paragraph("\n\n\n"));
        LineSeparator line = new LineSeparator();
        line.setLineColor(INSTITUTIONAL_GOLD);
        line.setLineWidth(2);
        document.add(new Chunk(line));

        // Nota informativa
        document.add(new Paragraph("\n\n"));
        Paragraph disclaimer = new Paragraph(
            "Este reporte presenta un listado detallado de estudiantes con sus modalidades de grado, " +
            "incluyendo información académica, estado de avance, directores asignados y estadísticas " +
            "generales. La información es generada automáticamente por el sistema SIGMA.",
            SMALL_FONT
        );
        disclaimer.setAlignment(Element.ALIGN_JUSTIFIED);
        disclaimer.setIndentationLeft(60);
        disclaimer.setIndentationRight(60);
        document.add(disclaimer);
    }

    /**
     * Filtros aplicados y resumen ejecutivo
     */
    private void addFiltersAndExecutiveSummary(Document document, StudentListingReportDTO report)
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

            StudentListingReportDTO.ExecutiveSummaryDTO summary = report.getExecutiveSummary();

            // Métricas principales
            PdfPTable metricsTable = new PdfPTable(5);
            metricsTable.setWidthPercentage(100);
            metricsTable.setSpacingBefore(10);
            metricsTable.setSpacingAfter(20);

            addMetricCard(metricsTable, "Total Estudiantes",
                String.valueOf(summary.getTotalStudents()), INSTITUTIONAL_RED);
            addMetricCard(metricsTable, "Modalidades Activas",
                String.valueOf(summary.getActiveModalities()), SUCCESS_COLOR);
            addMetricCard(metricsTable, "Completadas",
                String.valueOf(summary.getCompletedModalities()), INFO_COLOR);
            addMetricCard(metricsTable, "Progreso Promedio",
                String.format("%.1f%%", summary.getAverageProgress()), INSTITUTIONAL_GOLD);
            addMetricCard(metricsTable, "Tipos de Modalidad",
                String.valueOf(summary.getDifferentModalityTypes()), INSTITUTIONAL_GOLD);

            document.add(metricsTable);

            // Información adicional
            PdfPTable detailTable = new PdfPTable(2);
            detailTable.setWidthPercentage(90);
            detailTable.setWidths(new float[]{1.5f, 2f});
            detailTable.setSpacingBefore(10);
            detailTable.setSpacingAfter(15);
            detailTable.setHorizontalAlignment(Element.ALIGN_CENTER);

            addDetailRow(detailTable, "Modalidad Más Común:", summary.getMostCommonModalityType());
            addDetailRow(detailTable, "Estado Más Común:", summary.getMostCommonStatus());
            addDetailRow(detailTable, "Estados Diferentes:", String.valueOf(summary.getDifferentStatuses()));

            document.add(detailTable);
        }
    }

    /**
     * Estadísticas generales
     */
    private void addGeneralStatistics(Document document, StudentListingReportDTO report)
            throws DocumentException {

        addSectionTitle(document, "2. ESTADÍSTICAS GENERALES");

        if (report.getGeneralStatistics() == null) {
            document.add(new Paragraph("No hay estadísticas disponibles.", NORMAL_FONT));
            return;
        }

        StudentListingReportDTO.GeneralStatisticsDTO stats = report.getGeneralStatistics();

        // Estadísticas de modalidades
        addSubsectionTitle(document, "Distribución por Tipo de Modalidad");

        PdfPTable modalityTypeTable = new PdfPTable(4);
        modalityTypeTable.setWidthPercentage(100);
        modalityTypeTable.setSpacingBefore(10);
        modalityTypeTable.setSpacingAfter(20);

        addStatsCard(modalityTypeTable, "Individuales",
            String.valueOf(stats.getIndividualModalities()), INFO_COLOR);
        addStatsCard(modalityTypeTable, "Grupales",
            String.valueOf(stats.getGroupModalities()), SUCCESS_COLOR);
        addStatsCard(modalityTypeTable, "Con Director",
            String.valueOf(stats.getStudentsWithDirector()), INSTITUTIONAL_GOLD);
        addStatsCard(modalityTypeTable, "Sin Director",
            String.valueOf(stats.getStudentsWithoutDirector()), WARNING_COLOR);

        document.add(modalityTypeTable);

        // Estado de avance
        addSubsectionTitle(document, "Estado de Avance Temporal");

        PdfPTable timelineTable = new PdfPTable(3);
        timelineTable.setWidthPercentage(90);
        timelineTable.setSpacingBefore(10);
        timelineTable.setSpacingAfter(20);
        timelineTable.setHorizontalAlignment(Element.ALIGN_CENTER);

        addStatsCard(timelineTable, "A Tiempo",
            String.valueOf(stats.getStudentsOnTime()), SUCCESS_COLOR);
        addStatsCard(timelineTable, "En Riesgo",
            String.valueOf(stats.getStudentsAtRisk()), WARNING_COLOR);
        addStatsCard(timelineTable, "Retrasados",
            String.valueOf(stats.getStudentsDelayed()), DANGER_COLOR);

        document.add(timelineTable);

        // Promedios académicos
        addSubsectionTitle(document, "Indicadores Académicos Promedio");

        PdfPTable avgTable = new PdfPTable(2);
        avgTable.setWidthPercentage(80);
        avgTable.setWidths(new float[]{2f, 1f});
        avgTable.setSpacingBefore(10);
        avgTable.setSpacingAfter(15);
        avgTable.setHorizontalAlignment(Element.ALIGN_CENTER);

        addStatRow(avgTable, "Promedio Acumulado:",
            String.format("%.2f", stats.getAverageCumulativeGPA()));
        addStatRow(avgTable, "Créditos Completados (Promedio):",
            String.format("%.0f", stats.getAverageCompletedCredits()));
        addStatRow(avgTable, "Días en Modalidad (Promedio):",
            String.format("%.0f días", stats.getAverageDaysInModality()));

        document.add(avgTable);
    }

    /**
     * Análisis de distribución
     */
    private void addDistributionAnalysis(Document document, StudentListingReportDTO report)
            throws DocumentException {

        addSectionTitle(document, "3. ANÁLISIS DE DISTRIBUCIÓN");

        if (report.getDistributionAnalysis() == null) {
            document.add(new Paragraph("No hay análisis de distribución disponible.", NORMAL_FONT));
            return;
        }

        StudentListingReportDTO.DistributionAnalysisDTO distribution = report.getDistributionAnalysis();

        // Distribución por modalidad
        if (distribution.getByModalityType() != null && !distribution.getByModalityType().isEmpty()) {
            addSubsectionTitle(document, "Distribución por Tipo de Modalidad");

            addDistributionChart(document, distribution.getByModalityType(),
                distribution.getByModalityTypePercentage(), INFO_COLOR);
        }

        // Distribución por estado
        if (distribution.getByStatus() != null && !distribution.getByStatus().isEmpty()) {
            document.add(new Paragraph("\n"));
            addSubsectionTitle(document, "Distribución por Estado");

            addDistributionChart(document, distribution.getByStatus(),
                distribution.getByStatusPercentage(), INSTITUTIONAL_GOLD);
        }

        // Distribución por estado temporal
        if (distribution.getByTimelineStatus() != null && !distribution.getByTimelineStatus().isEmpty()) {
            document.add(new Paragraph("\n"));
            addSubsectionTitle(document, "Distribución por Estado Temporal");

            addDistributionChart(document, distribution.getByTimelineStatus(),
                distribution.getByTimelineStatusPercentage(), WARNING_COLOR);
        }
    }

    /**
     * Listado detallado de estudiantes
     */
    private void addStudentListing(Document document, StudentListingReportDTO report)
            throws DocumentException {

        addSectionTitle(document, "4. LISTADO DETALLADO DE ESTUDIANTES");

        if (report.getStudents() == null || report.getStudents().isEmpty()) {
            document.add(new Paragraph("No hay estudiantes para mostrar.", NORMAL_FONT));
            return;
        }

        // Crear tabla detallada
        PdfPTable table = new PdfPTable(8);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{1.8f, 1f, 2f, 1.2f, 0.8f, 1.5f, 1f, 1.5f});
        table.setSpacingBefore(10);
        table.setHeaderRows(1);

        // Encabezados
        addTableHeader(table, "Estudiante");
        addTableHeader(table, "Código");
        addTableHeader(table, "Modalidad");
        addTableHeader(table, "Estado");
        addTableHeader(table, "Progreso");
        addTableHeader(table, "Director");
        addTableHeader(table, "Días");
        addTableHeader(table, "Estado Temp.");

        // Datos
        boolean alternate = false;
        for (StudentListingReportDTO.StudentDetailDTO student : report.getStudents()) {
            addTableCell(table, student.getFullName(), alternate);
            addTableCell(table, student.getStudentCode() != null ? student.getStudentCode() : "N/D", alternate);
            addTableCell(table, student.getModalityType(), alternate);
            addTableCell(table, truncate(student.getModalityStatusDescription(), 20), alternate);
            addTableCell(table, String.format("%.0f%%", student.getProgressPercentage()), alternate);
            addTableCell(table, student.getDirectorName() != null ?
                truncate(student.getDirectorName(), 20) : "Sin asignar", alternate);
            addTableCell(table, String.valueOf(student.getDaysInModality()), alternate);
            addTableCell(table, translateTimelineStatus(student.getTimelineStatus()), alternate);

            alternate = !alternate;
        }

        document.add(table);

        // Nota sobre tabla
        document.add(new Paragraph("\n"));
        Paragraph note = new Paragraph(
            "* Estado Temp.: Estado temporal de avance (A Tiempo, En Riesgo, Retrasado)",
            TINY_FONT
        );
        note.setIndentationLeft(10);
        document.add(note);
    }

    /**
     * Estadísticas por modalidad
     */
    private void addModalityStatistics(Document document, StudentListingReportDTO report)
            throws DocumentException {

        addSectionTitle(document, "5. ESTADÍSTICAS POR TIPO DE MODALIDAD");

        if (report.getModalityStatistics() == null || report.getModalityStatistics().isEmpty()) {
            document.add(new Paragraph("No hay estadísticas por modalidad disponibles.", NORMAL_FONT));
            return;
        }

        // Tabla de estadísticas
        PdfPTable table = new PdfPTable(5);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{2.5f, 1f, 1f, 1f, 1.2f});
        table.setSpacingBefore(10);
        table.setSpacingAfter(15);

        // Encabezados
        addTableHeader(table, "Tipo de Modalidad");
        addTableHeader(table, "Total Est.");
        addTableHeader(table, "Activos");
        addTableHeader(table, "Completados");
        addTableHeader(table, "Tasa Complet.");

        // Datos
        boolean alternate = false;
        for (StudentListingReportDTO.ModalityStatisticsDTO stat : report.getModalityStatistics()) {
            addTableCell(table, stat.getModalityType(), alternate);
            addTableCell(table, String.valueOf(stat.getTotalStudents()), alternate);
            addTableCell(table, String.valueOf(stat.getActiveStudents()), alternate);
            addTableCell(table, String.valueOf(stat.getCompletedStudents()), alternate);
            addTableCell(table, String.format("%.1f%%", stat.getCompletionRate()), alternate);

            alternate = !alternate;
        }

        document.add(table);
    }

    /**
     * Estadísticas por estado
     */
    private void addStatusStatistics(Document document, StudentListingReportDTO report)
            throws DocumentException {

        addSectionTitle(document, "6. ESTADÍSTICAS POR ESTADO");

        if (report.getStatusStatistics() == null || report.getStatusStatistics().isEmpty()) {
            document.add(new Paragraph("No hay estadísticas por estado disponibles.", NORMAL_FONT));
            return;
        }

        // Gráfico de barras
        addSubsectionTitle(document, "Distribución de Estudiantes por Estado");

        // Encontrar máximo para escalar
        int maxValue = report.getStatusStatistics().stream()
            .mapToInt(StudentListingReportDTO.StatusStatisticsDTO::getStudentCount)
            .max()
            .orElse(1);

        // Crear gráfico
        PdfPTable chartTable = new PdfPTable(1);
        chartTable.setWidthPercentage(100);
        chartTable.setSpacingBefore(10);
        chartTable.setSpacingAfter(15);

        for (StudentListingReportDTO.StatusStatisticsDTO stat : report.getStatusStatistics()) {
            PdfPCell cell = new PdfPCell();
            cell.setPadding(3);
            cell.setBorder(Rectangle.NO_BORDER);

            // Tabla interna para la barra
            PdfPTable barTable = new PdfPTable(2);
            barTable.setWidthPercentage(100);

            try {
                barTable.setWidths(new float[]{2f, 5f});
            } catch (DocumentException e) {
                // Ignorar
            }

            // Etiqueta
            PdfPCell labelCell = new PdfPCell(new Phrase(
                truncate(stat.getStatusDescription(), 30),
                SMALL_FONT
            ));
            labelCell.setBorder(Rectangle.NO_BORDER);
            labelCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            labelCell.setPadding(3);
            barTable.addCell(labelCell);

            // Barra
            float percentage = maxValue > 0 ? (float) stat.getStudentCount() / maxValue : 0;
            PdfPCell barCell = createBarCell(
                stat.getStudentCount() + " (" + String.format("%.1f%%", stat.getPercentage()) + ")",
                percentage,
                INSTITUTIONAL_GOLD
            );
            barTable.addCell(barCell);

            cell.addElement(barTable);
            chartTable.addCell(cell);
        }

        document.add(chartTable);
    }

    /**
     * Estadísticas por semestre
     */
    private void addSemesterStatistics(Document document, StudentListingReportDTO report)
            throws DocumentException {

        addSectionTitle(document, "7. ESTADÍSTICAS POR SEMESTRE");

        if (report.getSemesterStatistics() == null || report.getSemesterStatistics().isEmpty()) {
            document.add(new Paragraph("No hay estadísticas por semestre disponibles.", NORMAL_FONT));
            return;
        }

        // Tabla de estadísticas
        PdfPTable table = new PdfPTable(5);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{1.2f, 1f, 1.2f, 1.2f, 1.2f});
        table.setSpacingBefore(10);
        table.setSpacingAfter(15);

        // Encabezados
        addTableHeader(table, "Semestre");
        addTableHeader(table, "Estudiantes");
        addTableHeader(table, "Iniciadas");
        addTableHeader(table, "Completadas");
        addTableHeader(table, "Tasa Complet.");

        // Datos
        boolean alternate = false;
        for (StudentListingReportDTO.SemesterStatisticsDTO stat : report.getSemesterStatistics()) {
            addTableCell(table, stat.getSemester(), alternate);
            addTableCell(table, String.valueOf(stat.getStudentCount()), alternate);
            addTableCell(table, String.valueOf(stat.getModalitiesStarted()), alternate);
            addTableCell(table, String.valueOf(stat.getModalitiesCompleted()), alternate);
            addTableCell(table, String.format("%.1f%%", stat.getCompletionRate()), alternate);

            alternate = !alternate;
        }

        document.add(table);
    }

    // ==================== MÉTODOS AUXILIARES ====================

    /**
     * Gráfico de distribución
     */
    private void addDistributionChart(Document document, Map<String, Integer> data,
                                      Map<String, Double> percentages, BaseColor color)
            throws DocumentException {

        if (data == null || data.isEmpty()) return;

        int maxValue = data.values().stream().mapToInt(Integer::intValue).max().orElse(1);

        PdfPTable chartTable = new PdfPTable(1);
        chartTable.setWidthPercentage(100);
        chartTable.setSpacingBefore(10);
        chartTable.setSpacingAfter(15);

        // Ordenar por valor descendente
        data.entrySet().stream()
            .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
            .limit(10) // Top 10
            .forEach(entry -> {
                try {
                    PdfPCell cell = new PdfPCell();
                    cell.setPadding(3);
                    cell.setBorder(Rectangle.NO_BORDER);

                    PdfPTable barTable = new PdfPTable(2);
                    barTable.setWidthPercentage(100);
                    barTable.setWidths(new float[]{2f, 5f});

                    // Etiqueta
                    PdfPCell labelCell = new PdfPCell(new Phrase(
                        truncate(entry.getKey(), 35),
                        SMALL_FONT
                    ));
                    labelCell.setBorder(Rectangle.NO_BORDER);
                    labelCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    labelCell.setPadding(3);
                    barTable.addCell(labelCell);

                    // Barra
                    float percentage = maxValue > 0 ? (float) entry.getValue() / maxValue : 0;
                    Double pct = percentages != null ? percentages.get(entry.getKey()) : null;
                    String label = entry.getValue() + (pct != null ?
                        " (" + String.format("%.1f%%", pct) + ")" : "");

                    PdfPCell barCell = createBarCell(label, percentage, color);
                    barTable.addCell(barCell);

                    cell.addElement(barTable);
                    chartTable.addCell(cell);
                } catch (DocumentException e) {
                    // Ignorar errores individuales
                }
            });

        document.add(chartTable);
    }

    /**
     * Crear celda de barra
     */
    private PdfPCell createBarCell(String label, float percentage, BaseColor color) {
        PdfPTable barContainer = new PdfPTable(2);
        try {
            float barWidth = Math.max(percentage * 100, 1); // Mínimo 1%
            float emptyWidth = Math.max((1 - percentage) * 100, 1);
            barContainer.setWidths(new float[]{barWidth, emptyWidth});
        } catch (DocumentException e) {
            // Ignorar
        }
        barContainer.setWidthPercentage(100);

        // Parte coloreada
        PdfPCell filledCell = new PdfPCell(new Phrase(label,
            FontFactory.getFont(FontFactory.HELVETICA_BOLD, 7, BaseColor.WHITE)));
        filledCell.setBackgroundColor(color);
        filledCell.setBorder(Rectangle.NO_BORDER);
        filledCell.setPadding(3);
        filledCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        barContainer.addCell(filledCell);

        // Parte vacía
        PdfPCell emptyCell = new PdfPCell();
        emptyCell.setBackgroundColor(LIGHT_GOLD);
        emptyCell.setBorder(Rectangle.NO_BORDER);
        barContainer.addCell(emptyCell);

        PdfPCell containerCell = new PdfPCell();
        containerCell.addElement(barContainer);
        containerCell.setBorder(Rectangle.NO_BORDER);
        containerCell.setPadding(0);

        return containerCell;
    }

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
            FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, BaseColor.WHITE)));
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
            FontFactory.getFont(FontFactory.HELVETICA_BOLD, 15, BaseColor.WHITE)));
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
        valueCell.setBackgroundColor(BaseColor.WHITE);
        valueCell.setBorder(Rectangle.NO_BORDER);
        table.addCell(valueCell);
    }

    /**
     * Fila de detalle
     */
    private void addDetailRow(PdfPTable table, String label, String value) {
        PdfPCell labelCell = new PdfPCell(new Phrase(label, BOLD_FONT));
        labelCell.setPadding(8);
        labelCell.setBorder(Rectangle.BOTTOM);
        labelCell.setBorderColor(LIGHT_GOLD);
        labelCell.setBorderWidth(0.5f);
        table.addCell(labelCell);

        PdfPCell valueCell = new PdfPCell(new Phrase(value, NORMAL_FONT));
        valueCell.setPadding(8);
        valueCell.setBorder(Rectangle.BOTTOM);
        valueCell.setBorderColor(LIGHT_GOLD);
        valueCell.setBorderWidth(0.5f);
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
        cell.setBackgroundColor(alternate ? LIGHT_GOLD : BaseColor.WHITE);
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
     * Traduce estado temporal
     */
    private String translateTimelineStatus(String status) {
        if (status == null) return "N/D";
        switch (status) {
            case "ON_TIME": return "A Tiempo";
            case "AT_RISK": return "En Riesgo";
            case "DELAYED": return "Retrasado";
            case "COMPLETED": return "Completado";
            default: return status;
        }
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
    private static class StudentListingPageEventHelper extends PdfPageEventHelper {

        private final StudentListingReportDTO report;
        private final Font headerFont = FontFactory.getFont(FontFactory.HELVETICA, 8, BaseColor.GRAY);
        private final Font footerFont = FontFactory.getFont(FontFactory.HELVETICA, 7, BaseColor.GRAY);

        public StudentListingPageEventHelper(StudentListingReportDTO report) {
            this.report = report;
        }

        @Override
        public void onEndPage(PdfWriter writer, Document document) {
            PdfContentByte cb = writer.getDirectContent();

            // Encabezado
            if (writer.getPageNumber() > 1) {
                Phrase header = new Phrase(
                    "Listado de Estudiantes - " + report.getAcademicProgramName(),
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


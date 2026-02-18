package com.SIGMA.USCO.report.service;

import com.SIGMA.USCO.report.dto.DefenseCalendarReportDTO;
import com.SIGMA.USCO.report.dto.DefenseCalendarReportDTO.*;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Generador de PDF para reportes de calendario de sustentaciones
 * Estilo profesional e institucional usando iText 5
 */
@Service
@RequiredArgsConstructor
public class DefenseCalendarPdfGenerator {

    // COLORES INSTITUCIONALES - USO EXCLUSIVO
    private static final BaseColor INSTITUTIONAL_RED = new BaseColor(143, 30, 30); // #8F1E1E - Color primario
    private static final BaseColor INSTITUTIONAL_GOLD = new BaseColor(213, 203, 160); // #D5CBA0 - Color secundario
    private static final BaseColor WHITE = BaseColor.WHITE; // Color primario
    private static final BaseColor LIGHT_GOLD = new BaseColor(245, 242, 235); // Tono muy claro de dorado para fondos sutiles
    private static final BaseColor TEXT_BLACK = BaseColor.BLACK; // Texto principal
    private static final BaseColor TEXT_GRAY = new BaseColor(80, 80, 80); // Texto secundario
    private static final BaseColor COLOR_BORDER = INSTITUTIONAL_GOLD; // Bordes institucionales

    // Fuentes con colores institucionales
    private static final Font TITLE_FONT = new Font(Font.FontFamily.HELVETICA, 20, Font.BOLD, INSTITUTIONAL_RED);
    private static final Font SUBTITLE_FONT = new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD, INSTITUTIONAL_RED);
    private static final Font SECTION_FONT = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD, INSTITUTIONAL_RED);
    private static final Font SUBSECTION_FONT = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, INSTITUTIONAL_RED);
    private static final Font NORMAL_FONT = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL, TEXT_BLACK);
    private static final Font BOLD_FONT = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD, TEXT_BLACK);
    private static final Font SMALL_FONT = new Font(Font.FontFamily.HELVETICA, 8, Font.NORMAL, TEXT_GRAY);
    private static final Font HEADER_TABLE_FONT = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD, WHITE);

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final DateTimeFormatter DATE_ONLY_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public byte[] generatePdf(DefenseCalendarReportDTO report) throws DocumentException, IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4, 40, 40, 40, 40);

        PdfWriter.getInstance(document, outputStream);
        document.open();

        // Portada
        addCoverPage(document, report);
        document.newPage();

        // Resumen Ejecutivo
        addExecutiveSummary(document, report);

        // Sustentaciones Próximas
        if (report.getUpcomingDefenses() != null && !report.getUpcomingDefenses().isEmpty()) {
            document.newPage();
            addUpcomingDefenses(document, report.getUpcomingDefenses());
        }

        // Sustentaciones en Progreso
        if (report.getInProgressDefenses() != null && !report.getInProgressDefenses().isEmpty()) {
            document.newPage();
            addInProgressDefenses(document, report.getInProgressDefenses());
        }

        // Sustentaciones Completadas
        if (report.getRecentCompletedDefenses() != null && !report.getRecentCompletedDefenses().isEmpty()) {
            document.newPage();
            addCompletedDefenses(document, report.getRecentCompletedDefenses());
        }

        // Estadísticas
        document.newPage();
        addStatistics(document, report.getStatistics());

        // Análisis Mensual
        if (report.getMonthlyAnalysis() != null && !report.getMonthlyAnalysis().isEmpty()) {
            document.newPage();
            addMonthlyAnalysis(document, report.getMonthlyAnalysis());
        }

        // Alertas
        if (report.getAlerts() != null && !report.getAlerts().isEmpty()) {
            document.newPage();
            addAlerts(document, report.getAlerts());
        }

        // Footer con metadata
        document.newPage();
        addFooter(document, report);

        document.close();
        return outputStream.toByteArray();
    }

    private void addCoverPage(Document document, DefenseCalendarReportDTO report) throws DocumentException {
        // Logo y título principal
        Paragraph title = new Paragraph("UNIVERSIDAD SURCOLOMBIANA", TITLE_FONT);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(10);
        document.add(title);

        Paragraph subtitle = new Paragraph("Facultad de Ingeniería", SUBTITLE_FONT);
        subtitle.setAlignment(Element.ALIGN_CENTER);
        subtitle.setSpacingAfter(40);
        document.add(subtitle);

        // Título del reporte
        Paragraph reportTitle = new Paragraph("REPORTE DE CALENDARIO DE\nSUSTENTACIONES Y EVALUACIONES",
                new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD, INSTITUTIONAL_RED));
        reportTitle.setAlignment(Element.ALIGN_CENTER);
        reportTitle.setSpacingAfter(30);
        document.add(reportTitle);

        // Información del programa
        PdfPTable infoTable = new PdfPTable(2);
        infoTable.setWidthPercentage(60);
        infoTable.setHorizontalAlignment(Element.ALIGN_CENTER);
        infoTable.setSpacingBefore(30);

        addInfoRow(infoTable, "Programa Académico:", report.getAcademicProgramName());
        addInfoRow(infoTable, "Código:", report.getAcademicProgramCode());
        addInfoRow(infoTable, "Fecha de Generación:", report.getGeneratedAt().format(DATE_FORMATTER));
        addInfoRow(infoTable, "Generado por:", report.getGeneratedBy());

        document.add(infoTable);

        // Filtros aplicados
        if (report.getAppliedFilters() != null && report.getAppliedFilters().getHasFilters()) {
            Paragraph filtersTitle = new Paragraph("Filtros Aplicados", BOLD_FONT);
            filtersTitle.setAlignment(Element.ALIGN_CENTER);
            filtersTitle.setSpacingBefore(30);
            document.add(filtersTitle);

            Paragraph filtersDesc = new Paragraph(report.getAppliedFilters().getFilterDescription(), NORMAL_FONT);
            filtersDesc.setAlignment(Element.ALIGN_CENTER);
            filtersDesc.setSpacingAfter(10);
            document.add(filtersDesc);
        }

        // Pie de portada
        Paragraph footer = new Paragraph("Sistema SIGMA - Gestión Integral de Modalidades Académicas", SMALL_FONT);
        footer.setAlignment(Element.ALIGN_CENTER);
        footer.setSpacingBefore(100);
        document.add(footer);
    }

    private void addExecutiveSummary(Document document, DefenseCalendarReportDTO report) throws DocumentException {
        addSectionTitle(document, "1. RESUMEN EJECUTIVO");

        ExecutiveSummaryDTO summary = report.getExecutiveSummary();

        // Crear tabla de resumen con 3 columnas
        PdfPTable summaryGrid = new PdfPTable(3);
        summaryGrid.setWidthPercentage(100);
        summaryGrid.setSpacingAfter(20);

        // Fila 1
        addSummaryCard(summaryGrid, "TOTAL PROGRAMADAS", String.valueOf(summary.getTotalScheduled()), INSTITUTIONAL_RED);
        addSummaryCard(summaryGrid, "ESTA SEMANA", String.valueOf(summary.getUpcomingThisWeek()), INSTITUTIONAL_RED);
        addSummaryCard(summaryGrid, "HOY", String.valueOf(summary.getDefensesToday()), INSTITUTIONAL_RED);

        // Fila 2
        addSummaryCard(summaryGrid, "ESTE MES", String.valueOf(summary.getUpcomingThisMonth()), INSTITUTIONAL_GOLD);
        addSummaryCard(summaryGrid, "EN PROGRESO", String.valueOf(summary.getPendingScheduling()), INSTITUTIONAL_RED);
        addSummaryCard(summaryGrid, "COMPLETADAS (MES)", String.valueOf(summary.getCompletedThisMonth()), INSTITUTIONAL_GOLD);

        document.add(summaryGrid);

        // Información adicional
        addSubsectionTitle(document, "Información Clave");

        PdfPTable detailsTable = new PdfPTable(new float[]{1, 2});
        detailsTable.setWidthPercentage(100);
        detailsTable.setSpacingAfter(20);

        addDetailRow(detailsTable, "Próxima Sustentación:", summary.getNextDefenseDate());
        addDetailRow(detailsTable, "Tasa de Éxito Promedio:", String.format("%.2f%%", summary.getAverageSuccessRate()));
        addDetailRow(detailsTable, "Total Jurados Involucrados:", String.valueOf(summary.getTotalExaminersInvolved()));
        addDetailRow(detailsTable, "Pendientes Vencidas:", String.valueOf(summary.getOverduePending()));

        document.add(detailsTable);
    }

    private void addUpcomingDefenses(Document document, List<UpcomingDefenseDTO> defenses) throws DocumentException {
        addSectionTitle(document, "2. CALENDARIO DE SUSTENTACIONES PRÓXIMAS");

        for (UpcomingDefenseDTO defense : defenses) {
            // Card de sustentación
            PdfPTable card = new PdfPTable(1);
            card.setWidthPercentage(100);
            card.setSpacingAfter(15);

            // Header con urgencia
            BaseColor urgencyColor = defense.getUrgency().equals("URGENT") ? INSTITUTIONAL_RED
                    : defense.getUrgency().equals("SOON") ? INSTITUTIONAL_RED : INSTITUTIONAL_GOLD;

            PdfPCell headerCell = new PdfPCell();
            headerCell.setBackgroundColor(urgencyColor);
            headerCell.setPadding(10);
            headerCell.setBorder(Rectangle.NO_BORDER);

            Paragraph headerText = new Paragraph();
            headerText.add(new Chunk(defense.getModalityTypeName() + " - ", new Font(Font.FontFamily.HELVETICA, 11, Font.BOLD, BaseColor.WHITE)));
            headerText.add(new Chunk("ID: " + defense.getModalityId(), new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL, BaseColor.WHITE)));
            headerText.add(Chunk.NEWLINE);
            headerText.add(new Chunk(defense.getDefenseDate().format(DATE_FORMATTER), new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, BaseColor.WHITE)));
            headerCell.addElement(headerText);
            card.addCell(headerCell);

            // Contenido
            PdfPCell contentCell = new PdfPCell();
            contentCell.setPadding(15);
            contentCell.setBorder(Rectangle.BOX);
            contentCell.setBorderColor(COLOR_BORDER);

            // Información básica
            Paragraph info = new Paragraph();
            info.add(new Chunk("Días hasta sustentación: ", BOLD_FONT));
            info.add(new Chunk(defense.getDaysUntilDefense() + " días", NORMAL_FONT));
            info.add(Chunk.NEWLINE);
            info.add(new Chunk("Ubicación: ", BOLD_FONT));
            info.add(new Chunk(defense.getDefenseLocation() != null ? defense.getDefenseLocation() : "Por definir", NORMAL_FONT));
            info.add(Chunk.NEWLINE);
            info.add(new Chunk("Preparación: ", BOLD_FONT));
            info.add(new Chunk(String.format("%.0f%%", defense.getReadinessPercentage()), NORMAL_FONT));
            info.add(Chunk.NEWLINE);
            info.add(Chunk.NEWLINE);

            // Estudiantes
            info.add(new Chunk("Estudiantes:", BOLD_FONT));
            info.add(Chunk.NEWLINE);
            for (StudentBasicInfoDTO student : defense.getStudents()) {
                info.add(new Chunk("  • " + student.getFullName() + (student.getIsLeader() ? " (Líder)" : ""), SMALL_FONT));
                info.add(Chunk.NEWLINE);
            }
            info.add(Chunk.NEWLINE);

            // Director
            info.add(new Chunk("Director: ", BOLD_FONT));
            info.add(new Chunk(defense.getDirectorName(), NORMAL_FONT));
            info.add(Chunk.NEWLINE);
            info.add(Chunk.NEWLINE);

            // Jurados
            if (!defense.getExaminers().isEmpty()) {
                info.add(new Chunk("Jurados:", BOLD_FONT));
                info.add(Chunk.NEWLINE);
                for (ExaminerInfoDTO examiner : defense.getExaminers()) {
                    info.add(new Chunk("  • " + examiner.getFullName() + " - " + examiner.getExaminerType(), SMALL_FONT));
                    info.add(Chunk.NEWLINE);
                }
            } else {
                info.add(new Chunk("⚠ Sin jurados asignados", new Font(Font.FontFamily.HELVETICA, 10, Font.ITALIC, INSTITUTIONAL_RED)));
                info.add(Chunk.NEWLINE);
            }

            // Tareas pendientes
            if (!defense.getPendingTasks().isEmpty()) {
                info.add(Chunk.NEWLINE);
                info.add(new Chunk("Tareas Pendientes:", new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD, INSTITUTIONAL_RED)));
                info.add(Chunk.NEWLINE);
                for (String task : defense.getPendingTasks()) {
                    info.add(new Chunk("  • " + task, SMALL_FONT));
                    info.add(Chunk.NEWLINE);
                }
            }

            contentCell.addElement(info);
            card.addCell(contentCell);

            document.add(card);
        }
    }

    private void addInProgressDefenses(Document document, List<InProgressDefenseDTO> defenses) throws DocumentException {
        addSectionTitle(document, "3. SUSTENTACIONES EN PROGRESO");

        PdfPTable table = new PdfPTable(new float[]{2, 2, 2, 1, 2, 1});
        table.setWidthPercentage(100);
        table.setSpacingAfter(20);

        addTableHeader(table, "Modalidad", "Estudiantes", "Director", "Estado", "Siguiente Acción", "Progreso");

        for (InProgressDefenseDTO defense : defenses) {
            String students = defense.getStudents().stream()
                    .map(StudentBasicInfoDTO::getFullName)
                    .collect(java.util.stream.Collectors.joining(", "));

            addTableCell(table, defense.getModalityType());
            addTableCell(table, students);
            addTableCell(table, defense.getDirectorName());
            addTableCell(table, defense.getCurrentStatus());
            addTableCell(table, defense.getNextAction());
            addTableCell(table, String.format("%.0f%%", defense.getProgressPercentage()));
        }

        document.add(table);
    }

    private void addCompletedDefenses(Document document, List<CompletedDefenseDTO> defenses) throws DocumentException {
        addSectionTitle(document, "4. SUSTENTACIONES COMPLETADAS RECIENTES");

        PdfPTable table = new PdfPTable(new float[]{2, 2, 2, 1, 1, 2, 1});
        table.setWidthPercentage(100);
        table.setSpacingAfter(20);

        addTableHeader(table, "Modalidad", "Estudiantes", "Director", "Fecha", "Nota", "Resultado", "Días");

        for (CompletedDefenseDTO defense : defenses) {
            String students = defense.getStudents().stream()
                    .map(StudentBasicInfoDTO::getFullName)
                    .collect(java.util.stream.Collectors.joining(", "));

            addTableCell(table, defense.getModalityType());
            addTableCell(table, students);
            addTableCell(table, defense.getDirectorName());
            addTableCell(table, defense.getDefenseDate().format(DATE_ONLY_FORMATTER));
            addTableCell(table, defense.getFinalGrade() != null ? String.format("%.2f", defense.getFinalGrade()) : "N/A");

            BaseColor resultColor = defense.getResult().equals("APROBADO") ? INSTITUTIONAL_GOLD
                    : defense.getResult().equals("REPROBADO") ? INSTITUTIONAL_RED : INSTITUTIONAL_RED;
            PdfPCell resultCell = new PdfPCell(new Phrase(defense.getResult(), SMALL_FONT));
            resultCell.setBackgroundColor(resultColor);
            resultCell.setPadding(5);
            resultCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(resultCell);

            addTableCell(table, defense.getDaysAgo() + "d");
        }

        document.add(table);
    }

    private void addStatistics(Document document, DefenseStatisticsDTO statistics) throws DocumentException {
        addSectionTitle(document, "5. ESTADÍSTICAS GENERALES");

        addSubsectionTitle(document, "5.1 Resumen de Sustentaciones");

        PdfPTable statsTable = new PdfPTable(4);
        statsTable.setWidthPercentage(100);
        statsTable.setSpacingAfter(20);

        addStatCard(statsTable, "Total Programadas", String.valueOf(statistics.getTotalScheduled()), INSTITUTIONAL_RED);
        addStatCard(statsTable, "Completadas", String.valueOf(statistics.getTotalCompleted()), INSTITUTIONAL_GOLD);
        addStatCard(statsTable, "Pendientes", String.valueOf(statistics.getTotalPending()), INSTITUTIONAL_RED);
        addStatCard(statsTable, "Aprobadas", String.valueOf(statistics.getApproved()), INSTITUTIONAL_GOLD);

        document.add(statsTable);

        // Tasas y promedios
        addSubsectionTitle(document, "5.2 Tasas de Éxito y Calificaciones");

        PdfPTable ratesTable = new PdfPTable(new float[]{1, 1});
        ratesTable.setWidthPercentage(100);
        ratesTable.setSpacingAfter(20);

        addDetailRow(ratesTable, "Tasa de Aprobación:", String.format("%.2f%%", statistics.getApprovalRate()));
        addDetailRow(ratesTable, "Tasa de Distinción:", String.format("%.2f%%", statistics.getDistinctionRate()));
        addDetailRow(ratesTable, "Calificación Promedio:", String.format("%.2f", statistics.getAverageGrade()));
        addDetailRow(ratesTable, "Calificación Más Alta:", String.format("%.2f", statistics.getHighestGrade()));
        addDetailRow(ratesTable, "Calificación Más Baja:", String.format("%.2f", statistics.getLowestGrade()));

        document.add(ratesTable);
    }

    private void addMonthlyAnalysis(Document document, List<MonthlyDefenseAnalysisDTO> monthlyData) throws DocumentException {
        addSectionTitle(document, "6. ANÁLISIS MENSUAL");

        PdfPTable table = new PdfPTable(new float[]{2, 1, 1, 1, 1, 1, 1});
        table.setWidthPercentage(100);
        table.setSpacingAfter(20);

        addTableHeader(table, "Periodo", "Programadas", "Completadas", "Pendientes", "Aprobadas", "Tasa Éxito", "Nota Prom.");

        for (MonthlyDefenseAnalysisDTO month : monthlyData) {
            addTableCell(table, month.getPeriodLabel());
            addTableCell(table, String.valueOf(month.getTotalScheduled()));
            addTableCell(table, String.valueOf(month.getCompleted()));
            addTableCell(table, String.valueOf(month.getPending()));
            addTableCell(table, String.valueOf(month.getApproved()));
            addTableCell(table, String.format("%.2f%%", month.getSuccessRate()));
            addTableCell(table, String.format("%.2f", month.getAverageGrade()));
        }

        document.add(table);
    }

    private void addAlerts(Document document, List<DefenseAlertDTO> alerts) throws DocumentException {
        addSectionTitle(document, "7. ALERTAS Y RECOMENDACIONES");

        for (DefenseAlertDTO alert : alerts) {
            BaseColor alertColor = alert.getAlertType().equals("URGENT") ? INSTITUTIONAL_RED
                    : alert.getAlertType().equals("WARNING") ? INSTITUTIONAL_RED : INSTITUTIONAL_GOLD;

            PdfPTable alertBox = new PdfPTable(1);
            alertBox.setWidthPercentage(100);
            alertBox.setSpacingAfter(15);

            PdfPCell alertCell = new PdfPCell();
            alertCell.setBorderColor(alertColor);
            alertCell.setBorderWidth(2);
            alertCell.setPadding(15);

            Paragraph alertContent = new Paragraph();
            alertContent.add(new Chunk("⚠ " + alert.getTitle(), new Font(Font.FontFamily.HELVETICA, 11, Font.BOLD, alertColor)));
            alertContent.add(Chunk.NEWLINE);
            alertContent.add(new Chunk(alert.getDescription(), NORMAL_FONT));
            alertContent.add(Chunk.NEWLINE);

            if (alert.getStudentName() != null) {
                alertContent.add(new Chunk("Estudiante: " + alert.getStudentName(), BOLD_FONT));
                alertContent.add(Chunk.NEWLINE);
            }

            if (alert.getDefenseDate() != null) {
                alertContent.add(new Chunk("Fecha de Sustentación: " + alert.getDefenseDate().format(DATE_FORMATTER), NORMAL_FONT));
                alertContent.add(Chunk.NEWLINE);
            }

            if (alert.getActionRequired() != null) {
                alertContent.add(Chunk.NEWLINE);
                alertContent.add(new Chunk("Acción Requerida: " + alert.getActionRequired(), new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD, INSTITUTIONAL_RED)));
            }

            alertCell.addElement(alertContent);
            alertBox.addCell(alertCell);

            document.add(alertBox);
        }
    }

    private void addFooter(Document document, DefenseCalendarReportDTO report) throws DocumentException {
        addSectionTitle(document, "INFORMACIÓN DEL REPORTE");

        PdfPTable metaTable = new PdfPTable(new float[]{1, 2});
        metaTable.setWidthPercentage(100);

        if (report.getMetadata() != null) {
            addDetailRow(metaTable, "Total de Registros:", String.valueOf(report.getMetadata().getTotalRecords()));
            addDetailRow(metaTable, "Versión del Reporte:", report.getMetadata().getReportVersion());
        }

        addDetailRow(metaTable, "Generado por:", report.getGeneratedBy());
        addDetailRow(metaTable, "Fecha y Hora:", report.getGeneratedAt().format(DATE_FORMATTER));
        addDetailRow(metaTable, "Programa:", report.getAcademicProgramName());

        document.add(metaTable);
    }

    // Métodos auxiliares

    private void addSectionTitle(Document document, String title) throws DocumentException {
        Paragraph p = new Paragraph(title, SECTION_FONT);
        p.setSpacingBefore(20);
        p.setSpacingAfter(15);
        document.add(p);

        LineSeparator line = new LineSeparator();
        line.setLineColor(INSTITUTIONAL_RED);
        document.add(new Chunk(line));
        document.add(Chunk.NEWLINE);
    }

    private void addSubsectionTitle(Document document, String title) throws DocumentException {
        Paragraph p = new Paragraph(title, SUBSECTION_FONT);
        p.setSpacingBefore(15);
        p.setSpacingAfter(10);
        document.add(p);
    }

    private void addInfoRow(PdfPTable table, String label, String value) {
        PdfPCell labelCell = new PdfPCell(new Phrase(label, BOLD_FONT));
        labelCell.setBorder(Rectangle.NO_BORDER);
        labelCell.setPadding(5);
        table.addCell(labelCell);

        PdfPCell valueCell = new PdfPCell(new Phrase(value, NORMAL_FONT));
        valueCell.setBorder(Rectangle.NO_BORDER);
        valueCell.setPadding(5);
        table.addCell(valueCell);
    }

    private void addDetailRow(PdfPTable table, String label, String value) {
        PdfPCell labelCell = new PdfPCell(new Phrase(label, BOLD_FONT));
        labelCell.setBackgroundColor(LIGHT_GOLD);
        labelCell.setPadding(8);
        table.addCell(labelCell);

        PdfPCell valueCell = new PdfPCell(new Phrase(value, NORMAL_FONT));
        valueCell.setPadding(8);
        table.addCell(valueCell);
    }

    private void addSummaryCard(PdfPTable table, String label, String value, BaseColor color) {
        PdfPCell cell = new PdfPCell();
        cell.setBorder(Rectangle.BOX);
        cell.setBorderColor(color);
        cell.setBorderWidth(2);
        cell.setPadding(15);
        cell.setBackgroundColor(new BaseColor(color.getRed(), color.getGreen(), color.getBlue(), 25));

        Paragraph content = new Paragraph();
        content.setAlignment(Element.ALIGN_CENTER);

        Chunk valueChunk = new Chunk(value, new Font(Font.FontFamily.HELVETICA, 20, Font.BOLD, color));
        content.add(valueChunk);
        content.add(Chunk.NEWLINE);
        content.add(new Chunk(label, new Font(Font.FontFamily.HELVETICA, 9, Font.NORMAL, TEXT_GRAY)));

        cell.addElement(content);
        table.addCell(cell);
    }

    private void addStatCard(PdfPTable table, String label, String value, BaseColor color) {
        PdfPCell cell = new PdfPCell();
        cell.setBorder(Rectangle.BOX);
        cell.setBorderColor(color);
        cell.setPadding(15);
        cell.setBackgroundColor(new BaseColor(color.getRed(), color.getGreen(), color.getBlue(), 25));

        Paragraph content = new Paragraph();
        content.setAlignment(Element.ALIGN_CENTER);

        Chunk valueChunk = new Chunk(value, new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD, color));
        content.add(valueChunk);
        content.add(Chunk.NEWLINE);
        content.add(new Chunk(label, SMALL_FONT));

        cell.addElement(content);
        table.addCell(cell);
    }

    private void addTableHeader(PdfPTable table, String... headers) {
        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header, new Font(Font.FontFamily.HELVETICA, 9, Font.BOLD, BaseColor.WHITE)));
            cell.setBackgroundColor(INSTITUTIONAL_RED);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setPadding(8);
            table.addCell(cell);
        }
    }

    private void addTableCell(PdfPTable table, String text) {
        PdfPCell cell = new PdfPCell(new Phrase(text, SMALL_FONT));
        cell.setPadding(6);
        cell.setBorderColor(COLOR_BORDER);
        table.addCell(cell);
    }
}



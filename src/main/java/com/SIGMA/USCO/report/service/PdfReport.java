package com.SIGMA.USCO.report.service;

import com.SIGMA.USCO.report.dto.ExecutiveSummaryDTO;
import com.SIGMA.USCO.report.dto.GlobalModalityReportDTO;
import com.SIGMA.USCO.report.dto.ModalityDetailReportDTO;
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
import java.util.Map;


@Service
@RequiredArgsConstructor
public class PdfReport {


    private static final BaseColor INSTITUTIONAL_RED = new BaseColor(143, 30, 30); // #8F1E1E
    private static final BaseColor INSTITUTIONAL_GOLD = new BaseColor(213, 203, 160); // #D5CBA0
    private static final BaseColor LIGHT_GOLD = new BaseColor(245, 242, 235); // Tono claro de dorado para fondos


    private static final Font TITLE_FONT = new Font(Font.FontFamily.HELVETICA, 20, Font.BOLD, INSTITUTIONAL_RED);
    private static final Font SUBTITLE_FONT = new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD, INSTITUTIONAL_RED);
    private static final Font SECTION_FONT = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD, INSTITUTIONAL_RED);
    private static final Font NORMAL_FONT = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL, BaseColor.BLACK);
    private static final Font BOLD_FONT = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD, BaseColor.BLACK);
    private static final Font SMALL_FONT = new Font(Font.FontFamily.HELVETICA, 8, Font.NORMAL, BaseColor.DARK_GRAY);
    private static final Font HEADER_TABLE_FONT = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD, BaseColor.WHITE);

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");


    public ByteArrayOutputStream generatePDF(GlobalModalityReportDTO report) throws DocumentException, IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4, 50, 50, 50, 50);

        PdfWriter.getInstance(document, outputStream);
        document.open();


        addHeader(document);
        addReportInfo(document, report);
        addExecutiveSummary(document, report.getExecutiveSummary());
        addManagementIndicators(document, report.getExecutiveSummary(), report.getModalities());
        addVisualDistributions(document, report.getExecutiveSummary());
        addDirectorAnalysis(document, report.getModalities());
        addModalityDetails(document, report.getModalities());
        addFooter(document, report);

        document.close();
        return outputStream;
    }


    private void addHeader(Document document) throws DocumentException {

        PdfPTable headerTable = new PdfPTable(1);
        headerTable.setWidthPercentage(100);
        PdfPCell headerCell = new PdfPCell();
        headerCell.setBorder(Rectangle.NO_BORDER);
        headerCell.setBackgroundColor(LIGHT_GOLD);
        headerCell.setPadding(20);

        Paragraph header = new Paragraph();
        header.setAlignment(Element.ALIGN_CENTER);

        Chunk systemName = new Chunk("SIGMA", TITLE_FONT);
        header.add(systemName);
        header.add(Chunk.NEWLINE);

        Chunk subtitle = new Chunk("Sistema de Gestión de Modalidades de Grado", NORMAL_FONT);
        header.add(subtitle);
        header.add(Chunk.NEWLINE);

        Chunk university = new Chunk("Universidad Surcolombiana", BOLD_FONT);
        header.add(university);
        header.add(Chunk.NEWLINE);

        headerCell.addElement(header);
        headerTable.addCell(headerCell);
        document.add(headerTable);


        LineSeparator line = new LineSeparator();
        line.setLineColor(INSTITUTIONAL_RED);
        line.setLineWidth(2);
        document.add(new Chunk(line));
        document.add(Chunk.NEWLINE);
    }


    private void addReportInfo(Document document, GlobalModalityReportDTO report) throws DocumentException {
        Paragraph title = new Paragraph("REPORTE GENERAL DE MODALIDADES ACTIVAS", SUBTITLE_FONT);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(10);
        document.add(title);


        if (report.getAcademicProgramName() != null) {
            Paragraph programInfo = new Paragraph(
                String.format("Programa: %s (%s)",
                    report.getAcademicProgramName(),
                    report.getAcademicProgramCode() != null ? report.getAcademicProgramCode() : "N/A"),
                BOLD_FONT
            );
            programInfo.setAlignment(Element.ALIGN_CENTER);
            programInfo.setSpacingAfter(20);
            document.add(programInfo);
        }


        PdfPTable infoTable = new PdfPTable(2);
        infoTable.setWidthPercentage(100);
        infoTable.setSpacingAfter(20);

        addInfoRow(infoTable, "Fecha de Generación:",
                report.getGeneratedAt().format(DATE_FORMATTER));
        addInfoRow(infoTable, "Generado por:", report.getGeneratedBy());
        addInfoRow(infoTable, "Total de Registros:",
                String.valueOf(report.getMetadata().getTotalRecords()));

        document.add(infoTable);
    }


    private void addExecutiveSummary(Document document, ExecutiveSummaryDTO summary) throws DocumentException {
        addSectionTitle(document, "1. RESUMEN EJECUTIVO");


        PdfPTable metricsTable = new PdfPTable(2);
        metricsTable.setWidthPercentage(100);
        metricsTable.setSpacingAfter(15);


        addMetricRow(metricsTable, "Total de Modalidades Activas",
                summary.getTotalActiveModalities().toString(), INSTITUTIONAL_RED);
        addMetricRow(metricsTable, "Total de Estudiantes Activos en Modalidades",
                summary.getTotalActiveStudents().toString(), INSTITUTIONAL_GOLD);
        addMetricRow(metricsTable, "Total de Directores Asignados",
                summary.getTotalActiveDirectors().toString(), INSTITUTIONAL_RED);
        addMetricRow(metricsTable, "Modalidades Individuales",
                summary.getIndividualModalities().toString(), INSTITUTIONAL_GOLD);
        addMetricRow(metricsTable, "Modalidades Grupales",
                summary.getGroupModalities().toString(), INSTITUTIONAL_RED);
        addMetricRow(metricsTable, "En Proceso de Revisión",
                summary.getModalitiesInReview().toString(), INSTITUTIONAL_GOLD);

        document.add(metricsTable);

        // Métricas adicionales de eficiencia
        addSubsectionTitle(document, "1.1 Indicadores de Eficiencia");
        PdfPTable efficiencyTable = new PdfPTable(2);
        efficiencyTable.setWidthPercentage(100);
        efficiencyTable.setSpacingAfter(15);

        // Agregar métricas disponibles en el DTO
        if (summary.getAverageStudentsPerGroup() != null) {
            addMetricRow(efficiencyTable, "Promedio de Estudiantes por Modalidad Grupal",
                    String.format("%.2f", summary.getAverageStudentsPerGroup()), INSTITUTIONAL_GOLD);
        }

        if (summary.getModalitiesWithoutDirector() != null) {
            BaseColor alertColor = summary.getModalitiesWithoutDirector() > 0 ?
                new BaseColor(180, 50, 50) : INSTITUTIONAL_GOLD;
            addMetricRow(efficiencyTable, "⚠ Modalidades sin Director Asignado",
                    summary.getModalitiesWithoutDirector().toString(), alertColor);
        }

        if (summary.getOverallProgressRate() != null) {
            addMetricRow(efficiencyTable, "Tasa de Progreso General",
                    String.format("%.1f%%", summary.getOverallProgressRate()), INSTITUTIONAL_RED);
        }

        document.add(efficiencyTable);

        // Distribución por tipo con visualización mejorada
        addSubsectionTitle(document, "1.2 Distribución por Tipo de Modalidad");
        PdfPTable typeTable = createEnhancedDistributionTable(
            summary.getModalitiesByType(),
            summary.getTotalActiveModalities()
        );
        document.add(typeTable);

        // Distribución por estado con visualización mejorada
        addSubsectionTitle(document, "1.3 Distribución por Estado");
        PdfPTable statusTable = createEnhancedDistributionTable(
            summary.getModalitiesByStatus(),
            summary.getTotalActiveModalities()
        );
        document.add(statusTable);
    }

    private void addModalityDetails(Document document, java.util.List<ModalityDetailReportDTO> modalities)
            throws DocumentException {


        document.newPage();

        addSectionTitle(document, "5. DETALLE DE MODALIDADES ACTIVAS");

        Paragraph totalPara = new Paragraph(
            String.format("Total: %d modalidades activas", modalities.size()),
            BOLD_FONT
        );
        totalPara.setSpacingAfter(15);
        document.add(totalPara);


        PdfPTable detailTable = new PdfPTable(7);
        detailTable.setWidthPercentage(100);
        detailTable.setWidths(new int[]{5, 15, 20, 12, 18, 15, 15});
        detailTable.setSpacingAfter(10);



        addTableHeader(detailTable, "ID");
        addTableHeader(detailTable, "Modalidad");
        addTableHeader(detailTable, "Estudiante(s)");
        addTableHeader(detailTable, "Programa");
        addTableHeader(detailTable, "Estado");
        addTableHeader(detailTable, "Director");
        addTableHeader(detailTable, "Días desde Inicio");


        for (ModalityDetailReportDTO modality : modalities) {
            addDetailRow(detailTable, modality);
        }

        document.add(detailTable);
    }


    private void addFooter(Document document, GlobalModalityReportDTO report) throws DocumentException {
        document.newPage();

        addSectionTitle(document, "6. OBSERVACIONES Y NOTAS");

        Paragraph notes = new Paragraph();
        notes.setFont(NORMAL_FONT);
        notes.setAlignment(Element.ALIGN_JUSTIFIED);


        if (report.getAcademicProgramName() != null) {
            notes.add(String.format("• Este reporte contiene únicamente las modalidades del programa académico: %s (%s).\n\n",
                    report.getAcademicProgramName(),
                    report.getAcademicProgramCode() != null ? report.getAcademicProgramCode() : "N/A"));
        }

        notes.add("• Para más información sobre modalidades específicas, consulte el sistema.\n\n");

        document.add(notes);


        Paragraph systemInfo = new Paragraph();
        systemInfo.setFont(SMALL_FONT);
        systemInfo.setAlignment(Element.ALIGN_CENTER);
        systemInfo.setSpacingBefore(30);
        systemInfo.add("Generado por SIGMA - Sistema de Gestión de Modalidades de Grado\n");
        systemInfo.add("Universidad Surcolombiana\n");
        systemInfo.add(report.getGeneratedAt().format(DATE_FORMATTER));

        document.add(systemInfo);
    }

    private void addSectionTitle(Document document, String title) throws DocumentException {
        Paragraph section = new Paragraph(title, SECTION_FONT);
        section.setSpacingBefore(15);
        section.setSpacingAfter(10);
        document.add(section);
    }

    private void addSubsectionTitle(Document document, String title) throws DocumentException {
        Paragraph subsection = new Paragraph(title, BOLD_FONT);
        subsection.setSpacingBefore(10);
        subsection.setSpacingAfter(5);
        document.add(subsection);
    }

    private void addInfoRow(PdfPTable table, String label, String value) {
        PdfPCell labelCell = new PdfPCell(new Phrase(label, BOLD_FONT));
        labelCell.setBorder(Rectangle.NO_BORDER);
        labelCell.setPaddingBottom(5);
        table.addCell(labelCell);

        PdfPCell valueCell = new PdfPCell(new Phrase(value, NORMAL_FONT));
        valueCell.setBorder(Rectangle.NO_BORDER);
        valueCell.setPaddingBottom(5);
        table.addCell(valueCell);
    }

    private void addMetricRow(PdfPTable table, String label, String value, BaseColor color) {
        PdfPCell labelCell = new PdfPCell(new Phrase(label, BOLD_FONT));
        labelCell.setPadding(8);
        labelCell.setBackgroundColor(BaseColor.WHITE);
        labelCell.setBorder(Rectangle.BOX);
        labelCell.setBorderColor(INSTITUTIONAL_GOLD);
        table.addCell(labelCell);

        PdfPCell valueCell = new PdfPCell(new Phrase(value, HEADER_TABLE_FONT));
        valueCell.setPadding(8);
        valueCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        valueCell.setBackgroundColor(color);
        valueCell.setBorder(Rectangle.BOX);
        valueCell.setBorderColor(INSTITUTIONAL_GOLD);
        table.addCell(valueCell);
    }

    private void addTableHeader(PdfPTable table, String header) {
        PdfPCell cell = new PdfPCell(new Phrase(header, HEADER_TABLE_FONT));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setBackgroundColor(INSTITUTIONAL_RED);
        cell.setBorder(Rectangle.BOX);
        cell.setBorderColor(INSTITUTIONAL_GOLD);
        cell.setPadding(8);
        table.addCell(cell);
    }

    private void addDetailRow(PdfPTable table, ModalityDetailReportDTO modality) {
        // ID
        table.addCell(createCell(modality.getStudentModalityId().toString(), Element.ALIGN_CENTER));

        // Modalidad
        table.addCell(createCell(modality.getModalityName(), Element.ALIGN_LEFT));

        // Estudiantes
        String students = modality.getStudents().stream()
                .map(s -> s.getFullName() + (s.getIsLeader() ? " (L)" : ""))
                .collect(java.util.stream.Collectors.joining(", "));
        table.addCell(createCell(students, Element.ALIGN_LEFT));

        // Programa
        table.addCell(createCell(modality.getAcademicProgram(), Element.ALIGN_LEFT));

        // Estado
        table.addCell(createCell(modality.getStatusDescription(), Element.ALIGN_LEFT));

        // Director
        String director;
        if (modality.getDirector() != null) {
            director = modality.getDirector().getFullName();
        } else {
            // Verificar si la modalidad no requiere director
            director = isDirectorNotRequired(modality.getModalityName())
                    ? "No requerido"
                    : "Sin asignar";
        }
        table.addCell(createCell(director, Element.ALIGN_LEFT));

        // Días
        table.addCell(createCell(modality.getDaysSinceStart() + " días", Element.ALIGN_CENTER));
    }


    private boolean isDirectorNotRequired(String modalityName) {
        if (modalityName == null) {
            return false;
        }

        String normalizedName = modalityName.toUpperCase().trim();

        return normalizedName.contains("PLAN COMPLEMENTARIO") ||
               normalizedName.contains("PRODUCCIÓN ACADEMICA") ||
               normalizedName.contains("PRODUCCION ACADEMICA") ||
               normalizedName.contains("SEMINARIO");
    }

    private PdfPCell createCell(String text, int alignment) {
        PdfPCell cell = new PdfPCell(new Phrase(text, SMALL_FONT));
        cell.setHorizontalAlignment(alignment);
        cell.setPadding(5);
        cell.setBackgroundColor(BaseColor.WHITE);
        cell.setBorder(Rectangle.BOX);
        cell.setBorderColor(INSTITUTIONAL_GOLD);
        return cell;
    }


    private PdfPTable createEnhancedDistributionTable(Map<String, Long> distribution, Integer total) {
        PdfPTable table = new PdfPTable(3);
        table.setWidthPercentage(95);
        table.setSpacingAfter(15);

        try {
            table.setWidths(new int[]{40, 15, 45});
        } catch (DocumentException e) {
            // Usar anchos por defecto si falla
        }

        // Encabezados
        addTableHeader(table, "Categoría");
        addTableHeader(table, "Cantidad");
        addTableHeader(table, "Distribución Visual");

        // Datos ordenados por valor descendente
        boolean alternate = false;
        for (Map.Entry<String, Long> entry : distribution.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .toList()) {

            double percentage = total > 0 ? (entry.getValue() * 100.0 / total) : 0;

            // Categoría
            PdfPCell categoryCell = new PdfPCell(new Phrase(entry.getKey(), NORMAL_FONT));
            categoryCell.setPadding(8);
            categoryCell.setBackgroundColor(alternate ? LIGHT_GOLD : BaseColor.WHITE);
            categoryCell.setBorder(Rectangle.BOX);
            categoryCell.setBorderColor(INSTITUTIONAL_GOLD);
            table.addCell(categoryCell);

            // Cantidad con porcentaje
            String quantityText = String.format("%d (%.1f%%)", entry.getValue(), percentage);
            PdfPCell quantityCell = new PdfPCell(new Phrase(quantityText, BOLD_FONT));
            quantityCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            quantityCell.setPadding(8);
            quantityCell.setBackgroundColor(alternate ? LIGHT_GOLD : BaseColor.WHITE);
            quantityCell.setBorder(Rectangle.BOX);
            quantityCell.setBorderColor(INSTITUTIONAL_GOLD);
            table.addCell(quantityCell);

            // Barra visual
            PdfPCell barCell = createProgressBar(percentage);
            barCell.setBackgroundColor(alternate ? LIGHT_GOLD : BaseColor.WHITE);
            table.addCell(barCell);

            alternate = !alternate;
        }

        return table;
    }


    private PdfPCell createProgressBar(double percentage) {
        PdfPCell cell = new PdfPCell();
        cell.setPadding(5);
        cell.setBorder(Rectangle.BOX);
        cell.setBorderColor(INSTITUTIONAL_GOLD);


        PdfPTable barTable = new PdfPTable(2);
        barTable.setWidthPercentage(100);

        try {
            int filledWidth = (int) percentage;
            int emptyWidth = 100 - filledWidth;

            if (filledWidth > 0) {
                barTable.setWidths(new int[]{filledWidth, emptyWidth > 0 ? emptyWidth : 1});
            } else {
                barTable.setWidths(new int[]{1, 99});
            }
        } catch (DocumentException e) {
            // Usar anchos por defecto
        }


        PdfPCell filledCell = new PdfPCell();
        filledCell.setBackgroundColor(percentage > 50 ? INSTITUTIONAL_RED : INSTITUTIONAL_GOLD);
        filledCell.setBorder(Rectangle.NO_BORDER);
        filledCell.setMinimumHeight(12);
        barTable.addCell(filledCell);


        if (percentage < 100) {
            PdfPCell emptyCell = new PdfPCell();
            emptyCell.setBackgroundColor(new BaseColor(240, 240, 240));
            emptyCell.setBorder(Rectangle.NO_BORDER);
            emptyCell.setMinimumHeight(12);
            barTable.addCell(emptyCell);
        }

        cell.addElement(barTable);
        return cell;
    }

    private void addManagementIndicators(Document document, ExecutiveSummaryDTO summary,
            java.util.List<ModalityDetailReportDTO> modalities) throws DocumentException {

        document.newPage();
        addSectionTitle(document, "2. INDICADORES DE GESTIÓN");


        addSubsectionTitle(document, "2.1 Alertas y Observaciones");

        PdfPTable alertsTable = new PdfPTable(2);
        alertsTable.setWidthPercentage(100);
        alertsTable.setSpacingAfter(15);


        java.util.List<ModalityDetailReportDTO> topLongest = modalities.stream()
                .sorted(java.util.Comparator.comparing(ModalityDetailReportDTO::getDaysSinceStart,
                        java.util.Comparator.reverseOrder()))
                .limit(5)
                .toList();

        if (!topLongest.isEmpty()) {
            addMetricRow(alertsTable, "Modalidad más antigua",
                    topLongest.getFirst().getDaysSinceStart() + " días",
                    new BaseColor(180, 50, 50));
            addMetricRow(alertsTable, "Promedio días modalidades top 5",
                    String.format("%.0f días", topLongest.stream()
                            .mapToLong(ModalityDetailReportDTO::getDaysSinceStart)
                            .average().orElse(0)),
                    INSTITUTIONAL_GOLD);
        }


        long withoutDirector = modalities.stream()
                .filter(m -> m.getDirector() == null && !isDirectorNotRequired(m.getModalityName()))
                .count();

        if (withoutDirector > 0) {
            addMetricRow(alertsTable, "⚠ Modalidades requieren director",
                    String.valueOf(withoutDirector),
                    new BaseColor(200, 100, 50));
        }

        document.add(alertsTable);


        addSubsectionTitle(document, "2.2 Eficiencia Operativa");

        PdfPTable efficiencyTable = new PdfPTable(2);
        efficiencyTable.setWidthPercentage(100);
        efficiencyTable.setSpacingAfter(15);


        double avgDays = modalities.stream()
                .mapToLong(ModalityDetailReportDTO::getDaysSinceStart)
                .average()
                .orElse(0);

        addMetricRow(efficiencyTable, "Promedio de Días en Proceso",
                String.format("%.0f días", avgDays), INSTITUTIONAL_RED);


        if (summary.getTotalActiveDirectors() > 0) {
            double ratio = (double) summary.getTotalActiveStudents() / summary.getTotalActiveDirectors();
            addMetricRow(efficiencyTable, "Ratio Estudiantes/Director",
                    String.format("%.2f", ratio), INSTITUTIONAL_GOLD);
        }

        document.add(efficiencyTable);


        if (!topLongest.isEmpty()) {
            addSubsectionTitle(document, "2.3 Modalidades con Mayor Tiempo Activo");

            PdfPTable topTable = new PdfPTable(4);
            topTable.setWidthPercentage(100);
            topTable.setSpacingAfter(15);

            try {
                topTable.setWidths(new int[]{30, 30, 20, 20});
            } catch (DocumentException e) {

            }

            addTableHeader(topTable, "Modalidad");
            addTableHeader(topTable, "Estudiante");
            addTableHeader(topTable, "Estado");
            addTableHeader(topTable, "Días Activo");

            for (ModalityDetailReportDTO modality : topLongest) {
                topTable.addCell(createCell(modality.getModalityName(), Element.ALIGN_LEFT));

                String studentName = modality.getStudents().isEmpty() ? "N/A" :
                        modality.getStudents().getFirst().getFullName();
                topTable.addCell(createCell(studentName, Element.ALIGN_LEFT));
                topTable.addCell(createCell(modality.getStatusDescription(), Element.ALIGN_CENTER));
                topTable.addCell(createCell(modality.getDaysSinceStart() + " días", Element.ALIGN_CENTER));
            }

            document.add(topTable);
        }
    }


    private void addVisualDistributions(Document document, ExecutiveSummaryDTO summary)
            throws DocumentException {

        addSectionTitle(document, "3. ANÁLISIS VISUAL DE DISTRIBUCIÓN");


        addSubsectionTitle(document, "3.1 Comparativa Individual vs Grupal");

        PdfPTable comparisonTable = new PdfPTable(3);
        comparisonTable.setWidthPercentage(90);
        comparisonTable.setSpacingAfter(20);

        try {
            comparisonTable.setWidths(new int[]{40, 30, 30});
        } catch (DocumentException e) {

        }

        addTableHeader(comparisonTable, "Tipo");
        addTableHeader(comparisonTable, "Cantidad");
        addTableHeader(comparisonTable, "Porcentaje");

        int total = summary.getIndividualModalities() + summary.getGroupModalities();
        double individualPct = total > 0 ? (summary.getIndividualModalities() * 100.0 / total) : 0;
        double groupPct = total > 0 ? (summary.getGroupModalities() * 100.0 / total) : 0;


        comparisonTable.addCell(createHighlightedCell("Individual", LIGHT_GOLD));
        comparisonTable.addCell(createHighlightedCell(
                summary.getIndividualModalities().toString(), BaseColor.WHITE));
        comparisonTable.addCell(createHighlightedCell(
                String.format("%.1f%%", individualPct), INSTITUTIONAL_GOLD));


        comparisonTable.addCell(createHighlightedCell("Grupal", LIGHT_GOLD));
        comparisonTable.addCell(createHighlightedCell(
                summary.getGroupModalities().toString(), BaseColor.WHITE));
        comparisonTable.addCell(createHighlightedCell(
                String.format("%.1f%%", groupPct), INSTITUTIONAL_RED));

        document.add(comparisonTable);
    }


    private void addDirectorAnalysis(Document document, java.util.List<ModalityDetailReportDTO> modalities)
            throws DocumentException {

        addSectionTitle(document, "4. ANÁLISIS DE DIRECTORES");


        java.util.Map<String, Long> directorCount = modalities.stream()
                .filter(m -> m.getDirector() != null)
                .collect(java.util.stream.Collectors.groupingBy(
                        m -> m.getDirector().getFullName(),
                        java.util.stream.Collectors.counting()
                ));

        if (directorCount.isEmpty()) {
            Paragraph noDirectors = new Paragraph(
                    "No hay directores asignados actualmente.",
                    NORMAL_FONT
            );
            noDirectors.setSpacingAfter(15);
            document.add(noDirectors);
            return;
        }


        addSubsectionTitle(document, "4.1 Directores con Mayor Carga de Trabajo");

        java.util.List<java.util.Map.Entry<String, Long>> topDirectors = directorCount.entrySet().stream()
                .sorted(java.util.Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(5)
                .toList();

        PdfPTable directorTable = new PdfPTable(3);
        directorTable.setWidthPercentage(95);
        directorTable.setSpacingAfter(15);

        try {
            directorTable.setWidths(new int[]{50, 20, 30});
        } catch (DocumentException e) {

        }

        addTableHeader(directorTable, "Director");
        addTableHeader(directorTable, "Modalidades");
        addTableHeader(directorTable, "Carga Relativa");

        long maxLoad = topDirectors.isEmpty() ? 1 : topDirectors.getFirst().getValue();

        for (java.util.Map.Entry<String, Long> entry : topDirectors) {
            double loadPercentage = (entry.getValue() * 100.0) / maxLoad;

            directorTable.addCell(createCell(entry.getKey(), Element.ALIGN_LEFT));
            directorTable.addCell(createCell(entry.getValue().toString(), Element.ALIGN_CENTER));

            PdfPCell barCell = createProgressBar(loadPercentage);
            directorTable.addCell(barCell);
        }

        document.add(directorTable);


        addSubsectionTitle(document, "4.2 Estadísticas de Distribución");

        PdfPTable statsTable = new PdfPTable(2);
        statsTable.setWidthPercentage(100);
        statsTable.setSpacingAfter(15);

        addMetricRow(statsTable, "Total de Directores Activos",
                String.valueOf(directorCount.size()), INSTITUTIONAL_RED);

        double avgModalitiesPerDirector = directorCount.values().stream()
                .mapToLong(Long::longValue)
                .average()
                .orElse(0);

        addMetricRow(statsTable, "Promedio Modalidades por Director",
                String.format("%.2f", avgModalitiesPerDirector), INSTITUTIONAL_GOLD);

        addMetricRow(statsTable, "Director con Mayor Carga",
                maxLoad + " modalidades", INSTITUTIONAL_RED);

        document.add(statsTable);
    }


    private PdfPCell createHighlightedCell(String text, BaseColor bgColor) {
        PdfPCell cell = new PdfPCell(new Phrase(text, BOLD_FONT));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setPadding(8);
        cell.setBackgroundColor(bgColor);
        cell.setBorder(Rectangle.BOX);
        cell.setBorderColor(INSTITUTIONAL_GOLD);
        return cell;
    }
}


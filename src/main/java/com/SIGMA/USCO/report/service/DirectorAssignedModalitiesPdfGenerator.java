package com.SIGMA.USCO.report.service;

import com.SIGMA.USCO.report.dto.DirectorAssignedModalitiesReportDTO;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.itextpdf.text.pdf.draw.LineSeparator;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * Servicio para generar PDF del reporte de modalidades por director asignado
 * RF-49 - Generación de Reportes por Director Asignado
 */
@Service
public class DirectorAssignedModalitiesPdfGenerator {

    // Colores institucionales
    private static final BaseColor INSTITUTIONAL_RED = new BaseColor(143, 30, 30); // #8F1E1E
    private static final BaseColor INSTITUTIONAL_GOLD = new BaseColor(213, 203, 160); // #D5CBA0
    private static final BaseColor LIGHT_GOLD = new BaseColor(245, 242, 235); // Tono claro de dorado para fondos

    // Fuentes con colores institucionales
    private static final Font TITLE_FONT = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20, INSTITUTIONAL_RED);
    private static final Font SUBTITLE_FONT = FontFactory.getFont(FontFactory.HELVETICA, 16, INSTITUTIONAL_RED);
    private static final Font HEADER_FONT = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 15, INSTITUTIONAL_RED);
    private static final Font SUBHEADER_FONT = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, INSTITUTIONAL_RED);
    private static final Font BOLD_FONT = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, BaseColor.BLACK);
    private static final Font NORMAL_FONT = FontFactory.getFont(FontFactory.HELVETICA, 10, BaseColor.BLACK);
    private static final Font SMALL_FONT = FontFactory.getFont(FontFactory.HELVETICA, 9, BaseColor.DARK_GRAY);
    private static final Font TINY_FONT = FontFactory.getFont(FontFactory.HELVETICA, 8, BaseColor.GRAY);
    private static final Font HEADER_TABLE_FONT = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, BaseColor.WHITE);

    // Colores auxiliares
    private static final BaseColor SUCCESS_COLOR = new BaseColor(40, 167, 69);
    private static final BaseColor WARNING_COLOR = new BaseColor(255, 193, 7);
    private static final BaseColor DANGER_COLOR = new BaseColor(220, 53, 69);
    private static final BaseColor INFO_COLOR = new BaseColor(23, 162, 184);
    private static final BaseColor TEXT_DARK = new BaseColor(33, 37, 41);

    public ByteArrayOutputStream generatePDF(DirectorAssignedModalitiesReportDTO report)
            throws DocumentException, IOException {

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4, 50, 50, 50, 50);
        PdfWriter writer = PdfWriter.getInstance(document, outputStream);

        // Agregar eventos de página (encabezado y pie de página)
        PageEventHelper pageEvent = new PageEventHelper(report);
        writer.setPageEvent(pageEvent);

        document.open();

        // 1. Portada
        addCoverPage(document, report);

        // 2. Resumen Ejecutivo
        document.newPage();
        addExecutiveSummary(document, report);

        // 3. Análisis de Carga de Trabajo (si está disponible)
        if (report.getWorkloadAnalysis() != null) {
            document.newPage();
            addWorkloadAnalysis(document, report);
        }

        // 4. Directores y sus Modalidades
        document.newPage();
        addDirectorsAndModalities(document, report);

        // 5. Estadísticas por Estado y Tipo
        document.newPage();
        addStatisticsByStatusAndType(document, report);

        // 6. Recomendaciones
        document.newPage();
        addRecommendations(document, report);

        document.close();
        return outputStream;
    }

    /**
     * Portada del reporte con diseño profesional
     */
    private void addCoverPage(Document document, DirectorAssignedModalitiesReportDTO report)
            throws DocumentException {

        // Banda superior institucional
        PdfPTable headerBand = new PdfPTable(1);
        headerBand.setWidthPercentage(100);
        headerBand.setSpacingAfter(30);

        PdfPCell bandCell = new PdfPCell();
        bandCell.setBackgroundColor(INSTITUTIONAL_RED);
        bandCell.setPadding(20);
        bandCell.setBorder(Rectangle.NO_BORDER);

        // Nombre de la institución
        Paragraph institution = new Paragraph("UNIVERSIDAD SURCOLOMBIANA",
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 22, BaseColor.WHITE));
        institution.setAlignment(Element.ALIGN_CENTER);
        bandCell.addElement(institution);

        Paragraph faculty = new Paragraph("Facultad de Ingeniería",
                FontFactory.getFont(FontFactory.HELVETICA, 14, BaseColor.WHITE));
        faculty.setAlignment(Element.ALIGN_CENTER);
        faculty.setSpacingBefore(5);
        bandCell.addElement(faculty);

        headerBand.addCell(bandCell);
        document.add(headerBand);

        // Programa académico
        PdfPTable programBox = new PdfPTable(1);
        programBox.setWidthPercentage(85);
        programBox.setHorizontalAlignment(Element.ALIGN_CENTER);
        programBox.setSpacingAfter(30);

        PdfPCell programCell = new PdfPCell();
        programCell.setBackgroundColor(LIGHT_GOLD);
        programCell.setPadding(12);
        programCell.setBorder(Rectangle.BOX);
        programCell.setBorderColor(INSTITUTIONAL_GOLD);
        programCell.setBorderWidth(1.5f);

        Paragraph program = new Paragraph(report.getAcademicProgramName().toUpperCase(),
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, INSTITUTIONAL_RED));
        program.setAlignment(Element.ALIGN_CENTER);
        programCell.addElement(program);

        programBox.addCell(programCell);
        document.add(programBox);

        // Título del reporte
        Paragraph title = new Paragraph("REPORTE DE MODALIDADES\nPOR DIRECTOR ASIGNADO",
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20, INSTITUTIONAL_RED));
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(35);
        document.add(title);

        // Si es reporte de un director específico
        if (report.getDirectorInfo() != null) {
            PdfPTable directorBox = new PdfPTable(1);
            directorBox.setWidthPercentage(70);
            directorBox.setHorizontalAlignment(Element.ALIGN_CENTER);
            directorBox.setSpacingAfter(35);

            PdfPCell directorCell = new PdfPCell();
            directorCell.setBackgroundColor(INSTITUTIONAL_GOLD);
            directorCell.setPadding(10);
            directorCell.setBorder(Rectangle.NO_BORDER);

            Paragraph directorPara = new Paragraph("Director: " + report.getDirectorInfo().getFullName(),
                    FontFactory.getFont(FontFactory.HELVETICA_BOLD, 13, BaseColor.WHITE));
            directorPara.setAlignment(Element.ALIGN_CENTER);
            directorCell.addElement(directorPara);

            directorBox.addCell(directorCell);
            document.add(directorBox);
        }

        // Información del reporte en tabla profesional
        PdfPTable infoTable = new PdfPTable(2);
        infoTable.setWidthPercentage(75);
        infoTable.setHorizontalAlignment(Element.ALIGN_CENTER);
        infoTable.setSpacingBefore(40);
        infoTable.setWidths(new float[]{45, 55});

        addCoverInfoRow(infoTable, "Programa:", report.getAcademicProgramName());
        addCoverInfoRow(infoTable, "Código:", report.getAcademicProgramCode());
        addCoverInfoRow(infoTable, "Fecha de Generación:",
                report.getGeneratedAt().format(DateTimeFormatter.ofPattern("dd 'de' MMMM 'de' yyyy - HH:mm")));
        addCoverInfoRow(infoTable, "Generado por:", report.getGeneratedBy().split(" \\(")[0]);
        addCoverInfoRow(infoTable, "Total de Directores:", String.valueOf(report.getSummary().getTotalDirectors()));
        addCoverInfoRow(infoTable, "Total de Modalidades:", String.valueOf(report.getSummary().getTotalModalitiesAssigned()));

        document.add(infoTable);

        // Footer institucional de portada
        PdfPTable footerTable = new PdfPTable(1);
        footerTable.setWidthPercentage(100);
        footerTable.setSpacingBefore(70);

        PdfPCell footerCell = new PdfPCell();
        footerCell.setBackgroundColor(LIGHT_GOLD);
        footerCell.setPadding(12);
        footerCell.setBorder(Rectangle.TOP);
        footerCell.setBorderColor(INSTITUTIONAL_RED);

        Paragraph footer = new Paragraph("Sistema SIGMA - Sistema Integral de Gestión de Modalidades de Grado",
                FontFactory.getFont(FontFactory.HELVETICA, 9, new BaseColor(52, 73, 94)));
        footer.setAlignment(Element.ALIGN_CENTER);
        footerCell.addElement(footer);

        footerTable.addCell(footerCell);
        document.add(footerTable);
    }

    /**
     * Agregar fila de información en la portada con diseño mejorado
     */
    private void addCoverInfoRow(PdfPTable table, String label, String value) {
        PdfPCell labelCell = new PdfPCell(new Phrase(label,
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, new BaseColor(52, 73, 94))));
        labelCell.setBackgroundColor(LIGHT_GOLD);
        labelCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        labelCell.setPadding(8);
        labelCell.setBorder(Rectangle.NO_BORDER);
        table.addCell(labelCell);

        PdfPCell valueCell = new PdfPCell(new Phrase(value,
                FontFactory.getFont(FontFactory.HELVETICA, 10, BaseColor.BLACK)));
        valueCell.setHorizontalAlignment(Element.ALIGN_LEFT);
        valueCell.setPadding(8);
        valueCell.setBorder(Rectangle.BOTTOM);
        valueCell.setBorderColor(LIGHT_GOLD);
        valueCell.setBorderWidth(0.5f);
        table.addCell(valueCell);
    }

    /**
     * Resumen ejecutivo con diseño mejorado
     */
    private void addExecutiveSummary(Document document, DirectorAssignedModalitiesReportDTO report)
            throws DocumentException {

        addSectionTitle(document, "1. RESUMEN EJECUTIVO");

        DirectorAssignedModalitiesReportDTO.DirectorSummaryDTO summary = report.getSummary();

        // Tabla de resumen principal con diseño mejorado
        PdfPTable summaryTable = new PdfPTable(4);
        summaryTable.setWidthPercentage(100);
        summaryTable.setWidths(new int[]{25, 25, 25, 25});
        summaryTable.setSpacingAfter(25);

        // Tarjeta 1: Total de directores
        addMetricCard(summaryTable, "Directores",
                String.valueOf(summary.getTotalDirectors()), INSTITUTIONAL_GOLD);

        // Tarjeta 2: Total de modalidades
        addMetricCard(summaryTable, "Modalidades Asignadas",
                String.valueOf(summary.getTotalModalitiesAssigned()), INSTITUTIONAL_RED);

        // Tarjeta 3: Modalidades activas
        addMetricCard(summaryTable, "Modalidades Activas",
                String.valueOf(summary.getTotalActiveModalities()), SUCCESS_COLOR);

        // Tarjeta 4: Estudiantes supervisados
        addMetricCard(summaryTable, "Estudiantes Supervisados",
                String.valueOf(summary.getTotalStudentsSupervised()), INFO_COLOR);

        document.add(summaryTable);

        // Director con más modalidades
        if (summary.getDirectorWithMostModalities() != null) {
            PdfPTable mostTable = new PdfPTable(1);
            mostTable.setWidthPercentage(100);
            mostTable.setSpacingAfter(12);

            PdfPCell mostCell = new PdfPCell();
            mostCell.setBackgroundColor(new BaseColor(255, 243, 224)); // Naranja claro
            mostCell.setPadding(12);
            mostCell.setBorderColor(WARNING_COLOR);
            mostCell.setBorderWidth(2f);

            Paragraph mostText = new Paragraph();
            mostText.add(new Chunk("📊 DIRECTOR CON MÁS MODALIDADES: ",
                    FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, new BaseColor(255, 152, 0))));
            mostText.add(new Chunk(summary.getDirectorWithMostModalities() + " ",
                    FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, TEXT_DARK)));
            mostText.add(new Chunk("(" + summary.getMaxModalitiesCount() + " modalidades)",
                    FontFactory.getFont(FontFactory.HELVETICA, 10, new BaseColor(255, 152, 0))));
            mostCell.addElement(mostText);
            mostTable.addCell(mostCell);

            document.add(mostTable);
        }

        // Promedio de modalidades por director
        PdfPTable avgTable = new PdfPTable(1);
        avgTable.setWidthPercentage(100);
        avgTable.setSpacingAfter(20);

        PdfPCell avgCell = new PdfPCell();
        avgCell.setBackgroundColor(LIGHT_GOLD);
        avgCell.setPadding(12);
        avgCell.setBorderColor(INSTITUTIONAL_GOLD);
        avgCell.setBorderWidth(1.5f);

        Paragraph avgText = new Paragraph();
        avgText.add(new Chunk("📈 PROMEDIO DE MODALIDADES POR DIRECTOR: ",
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, INSTITUTIONAL_RED)));
        avgText.add(new Chunk(String.valueOf(summary.getAverageModalitiesPerDirector()),
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, INSTITUTIONAL_RED)));
        avgCell.addElement(avgText);
        avgTable.addCell(avgCell);

        document.add(avgTable);

        // Directores sobrecargados y disponibles
        PdfPTable statusTable = new PdfPTable(2);
        statusTable.setWidthPercentage(100);
        statusTable.setSpacingAfter(10);

        addDirectorStatusCard(statusTable, "Directores con Carga Alta/Sobrecarga",
                String.valueOf(summary.getDirectorsOverloaded()), DANGER_COLOR);

        addDirectorStatusCard(statusTable, "Directores Disponibles",
                String.valueOf(summary.getDirectorsAvailable()), SUCCESS_COLOR);

        document.add(statusTable);
    }

    /**
     * Agregar tarjeta de métrica con diseño mejorado
     */
    private void addMetricCard(PdfPTable table, String label, String value, BaseColor color) {
        PdfPCell card = new PdfPCell();
        card.setPadding(15);
        card.setBorderColor(color);
        card.setBorderWidth(1.5f);
        card.setBackgroundColor(new BaseColor(255, 255, 255));

        // Valor grande
        Paragraph valuePara = new Paragraph(value,
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 28, color));
        valuePara.setAlignment(Element.ALIGN_CENTER);
        valuePara.setSpacingAfter(5);
        card.addElement(valuePara);

        // Etiqueta
        Paragraph labelPara = new Paragraph(label,
                FontFactory.getFont(FontFactory.HELVETICA, 9, new BaseColor(96, 125, 139)));
        labelPara.setAlignment(Element.ALIGN_CENTER);
        card.addElement(labelPara);

        table.addCell(card);
    }

    /**
     * Agregar tarjeta de estado de directores
     */
    private void addDirectorStatusCard(PdfPTable table, String label, String value, BaseColor color) {
        PdfPCell card = new PdfPCell();
        card.setPadding(12);
        card.setBorderColor(color);
        card.setBorderWidth(1.5f);
        card.setBackgroundColor(new BaseColor(255, 255, 255));

        Paragraph text = new Paragraph();
        text.add(new Chunk(label + ": ", NORMAL_FONT));
        text.add(new Chunk(value, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, color)));
        text.setAlignment(Element.ALIGN_CENTER);
        card.addElement(text);

        table.addCell(card);
    }

    /**
     * Análisis de carga de trabajo
     */
    private void addWorkloadAnalysis(Document document, DirectorAssignedModalitiesReportDTO report)
            throws DocumentException {

        addSectionTitle(document, "2. ANÁLISIS DE CARGA DE TRABAJO");

        DirectorAssignedModalitiesReportDTO.WorkloadAnalysisDTO workload = report.getWorkloadAnalysis();

        // Estado general
        PdfPTable overallTable = new PdfPTable(1);
        overallTable.setWidthPercentage(100);
        overallTable.setSpacingAfter(20);

        BaseColor statusColor = "BALANCED".equals(workload.getOverallWorkloadStatus()) ?
                SUCCESS_COLOR : DANGER_COLOR;
        String statusIcon = "BALANCED".equals(workload.getOverallWorkloadStatus()) ? "✓" : "⚠";

        PdfPCell statusCell = new PdfPCell();
        statusCell.setBackgroundColor(statusColor);
        statusCell.setPadding(12);
        statusCell.setBorder(Rectangle.NO_BORDER);

        Paragraph statusText = new Paragraph();
        statusText.add(new Chunk(statusIcon + " ESTADO GENERAL DE CARGA: ",
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 13, BaseColor.WHITE)));
        statusText.add(new Chunk(
                "BALANCED".equals(workload.getOverallWorkloadStatus()) ? "EQUILIBRADA" : "DESEQUILIBRADA",
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 13, BaseColor.WHITE)));
        statusCell.addElement(statusText);
        overallTable.addCell(statusCell);

        document.add(overallTable);

        // Información clave
        PdfPTable infoTable = new PdfPTable(2);
        infoTable.setWidthPercentage(90);
        infoTable.setSpacingAfter(20);

        addWorkloadInfoRow(infoTable, "Máximo Recomendado por Director:",
                workload.getRecommendedMaxModalities() + " modalidades");
        addWorkloadInfoRow(infoTable, "Carga Promedio Actual:",
                workload.getAverageWorkload() + " modalidades");

        document.add(infoTable);

        // Distribución de carga
        Paragraph distTitle = new Paragraph("Distribución de Carga de Trabajo:", SUBHEADER_FONT);
        distTitle.setSpacingBefore(10);
        distTitle.setSpacingAfter(10);
        document.add(distTitle);

        Map<String, Integer> distribution = workload.getWorkloadDistribution();
        int total = distribution.values().stream().mapToInt(Integer::intValue).sum();

        PdfPTable distTable = new PdfPTable(3);
        distTable.setWidthPercentage(100);
        distTable.setWidths(new int[]{40, 40, 20});
        distTable.setSpacingAfter(20);

        addWorkloadDistributionRow(distTable, "Baja (< 2 modalidades)",
                distribution.getOrDefault("LOW", 0), total, SUCCESS_COLOR);
        addWorkloadDistributionRow(distTable, "Normal (2-4 modalidades)",
                distribution.getOrDefault("NORMAL", 0), total, INFO_COLOR);
        addWorkloadDistributionRow(distTable, "Alta (5-7 modalidades)",
                distribution.getOrDefault("HIGH", 0), total, WARNING_COLOR);
        addWorkloadDistributionRow(distTable, "Sobrecarga (≥ 8 modalidades)",
                distribution.getOrDefault("OVERLOADED", 0), total, DANGER_COLOR);

        document.add(distTable);

        // Directores sobrecargados
        if (workload.getDirectorsOverloaded() != null && !workload.getDirectorsOverloaded().isEmpty()) {
            Paragraph overloadedTitle = new Paragraph("⚠ Directores con Sobrecarga:", SUBHEADER_FONT);
            overloadedTitle.setSpacingBefore(10);
            overloadedTitle.setSpacingAfter(5);
            document.add(overloadedTitle);

            for (String director : workload.getDirectorsOverloaded()) {
                Paragraph dirPara = new Paragraph("• " + director, NORMAL_FONT);
                dirPara.setIndentationLeft(20);
                dirPara.setSpacingAfter(3);
                document.add(dirPara);
            }
            document.add(Chunk.NEWLINE);
        }

        // Directores disponibles
        if (workload.getDirectorsAvailable() != null && !workload.getDirectorsAvailable().isEmpty()) {
            Paragraph availableTitle = new Paragraph("✓ Directores Disponibles para Nuevas Asignaciones:",
                    SUBHEADER_FONT);
            availableTitle.setSpacingBefore(10);
            availableTitle.setSpacingAfter(5);
            document.add(availableTitle);

            for (String director : workload.getDirectorsAvailable()) {
                Paragraph dirPara = new Paragraph("• " + director, NORMAL_FONT);
                dirPara.setIndentationLeft(20);
                dirPara.setSpacingAfter(3);
                document.add(dirPara);
            }
        }
    }

    /**
     * Agregar fila de información de carga
     */
    private void addWorkloadInfoRow(PdfPTable table, String label, String value) {
        PdfPCell labelCell = new PdfPCell(new Phrase(label, BOLD_FONT));
        labelCell.setBackgroundColor(LIGHT_GOLD);
        labelCell.setPadding(8);
        table.addCell(labelCell);

        PdfPCell valueCell = new PdfPCell(new Phrase(value,
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, INSTITUTIONAL_RED)));
        valueCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        valueCell.setPadding(8);
        table.addCell(valueCell);
    }

    /**
     * Agregar fila de distribución de carga
     */
    private void addWorkloadDistributionRow(PdfPTable table, String label, int count, int total, BaseColor color) {
        // Etiqueta
        PdfPCell labelCell = new PdfPCell(new Phrase(label, SMALL_FONT));
        labelCell.setPadding(8);
        labelCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        table.addCell(labelCell);

        // Barra visual
        PdfPCell barCell = new PdfPCell();
        barCell.setPadding(5);
        barCell.setVerticalAlignment(Element.ALIGN_MIDDLE);

        PdfPTable innerTable = new PdfPTable(2);
        float percentage = total > 0 ? ((float) count / total * 100) : 0;
        float barWidth = percentage > 0 ? percentage : 0.1f;
        float emptyWidth = 100 - barWidth;

        try {
            innerTable.setWidths(new float[]{barWidth, emptyWidth});
        } catch (Exception e) {
            try {
                innerTable.setWidths(new float[]{50, 50});
            } catch (Exception ex) {
                // Ignorar
            }
        }

        PdfPCell filledCell = new PdfPCell();
        filledCell.setBackgroundColor(color);
        filledCell.setBorder(Rectangle.NO_BORDER);
        filledCell.setPadding(4);
        innerTable.addCell(filledCell);

        PdfPCell emptyCell = new PdfPCell();
        emptyCell.setBackgroundColor(LIGHT_GOLD);
        emptyCell.setBorder(Rectangle.NO_BORDER);
        emptyCell.setPadding(4);
        innerTable.addCell(emptyCell);

        barCell.addElement(innerTable);
        table.addCell(barCell);

        // Cantidad y porcentaje
        PdfPCell countCell = new PdfPCell(new Phrase(count + " (" + String.format("%.1f", percentage) + "%)",
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, color)));
        countCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        countCell.setPadding(8);
        table.addCell(countCell);
    }

    /**
     * Directores y sus modalidades asignadas
     */
    private void addDirectorsAndModalities(Document document, DirectorAssignedModalitiesReportDTO report)
            throws DocumentException {

        addSectionTitle(document, "3. DIRECTORES Y MODALIDADES ASIGNADAS");

        List<DirectorAssignedModalitiesReportDTO.DirectorWithModalitiesDTO> directors = report.getDirectors();

        for (int i = 0; i < directors.size(); i++) {
            DirectorAssignedModalitiesReportDTO.DirectorWithModalitiesDTO director = directors.get(i);

            // Si no es el primero, agregar nueva página para cada director
            if (i > 0) {
                document.newPage();
            }

            // Encabezado del director
            PdfPTable directorHeader = new PdfPTable(1);
            directorHeader.setWidthPercentage(100);
            directorHeader.setSpacingAfter(15);

            PdfPCell headerCell = new PdfPCell();
            BaseColor workloadColor = getWorkloadColor(director.getWorkloadStatus());
            headerCell.setBackgroundColor(workloadColor);
            headerCell.setPadding(12);
            headerCell.setBorder(Rectangle.NO_BORDER);

            Paragraph directorName = new Paragraph(director.getFullName(),
                    FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, BaseColor.WHITE));
            directorName.setAlignment(Element.ALIGN_CENTER);
            headerCell.addElement(directorName);

            if (director.getAcademicTitle() != null) {
                Paragraph title = new Paragraph(director.getAcademicTitle(),
                        FontFactory.getFont(FontFactory.HELVETICA, 10, BaseColor.WHITE));
                title.setAlignment(Element.ALIGN_CENTER);
                title.setSpacingBefore(3);
                headerCell.addElement(title);
            }

            directorHeader.addCell(headerCell);
            document.add(directorHeader);

            // Estadísticas del director
            PdfPTable statsTable = new PdfPTable(4);
            statsTable.setWidthPercentage(100);
            statsTable.setSpacingAfter(15);

            addDirectorStatCell(statsTable, "Total Asignadas",
                    String.valueOf(director.getTotalAssignedModalities()), INFO_COLOR);
            addDirectorStatCell(statsTable, "Activas",
                    String.valueOf(director.getActiveModalities()), SUCCESS_COLOR);
            addDirectorStatCell(statsTable, "Completadas",
                    String.valueOf(director.getCompletedModalities()), new BaseColor(108, 117, 125));
            addDirectorStatCell(statsTable, "En Revisión",
                    String.valueOf(director.getPendingApprovalModalities()), WARNING_COLOR);

            document.add(statsTable);

            // Modalidades del director
            if (director.getModalities() != null && !director.getModalities().isEmpty()) {
                Paragraph modalitiesTitle = new Paragraph("Modalidades Asignadas:", SUBHEADER_FONT);
                modalitiesTitle.setSpacingBefore(10);
                modalitiesTitle.setSpacingAfter(10);
                document.add(modalitiesTitle);

                // Limitar a las primeras modalidades si hay muchas
                int maxToShow = Math.min(director.getModalities().size(), 10);
                for (int j = 0; j < maxToShow; j++) {
                    DirectorAssignedModalitiesReportDTO.ModalityDetailDTO modality = director.getModalities().get(j);
                    addModalityDetail(document, modality, j + 1);
                }

                if (director.getModalities().size() > maxToShow) {
                    Paragraph moreInfo = new Paragraph(
                            "... y " + (director.getModalities().size() - maxToShow) + " modalidades más.",
                            FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 9, BaseColor.GRAY));
                    moreInfo.setAlignment(Element.ALIGN_CENTER);
                    moreInfo.setSpacingBefore(10);
                    document.add(moreInfo);
                }
            }
        }
    }

    /**
     * Obtener color según carga de trabajo
     */
    private BaseColor getWorkloadColor(String workloadStatus) {
        switch (workloadStatus) {
            case "LOW":
                return SUCCESS_COLOR;
            case "NORMAL":
                return INFO_COLOR;
            case "HIGH":
                return WARNING_COLOR;
            case "OVERLOADED":
                return DANGER_COLOR;
            default:
                return INSTITUTIONAL_GOLD;
        }
    }

    /**
     * Agregar celda de estadística del director
     */
    private void addDirectorStatCell(PdfPTable table, String label, String value, BaseColor color) {
        PdfPCell cell = new PdfPCell();
        cell.setPadding(10);
        cell.setBorderColor(color);
        cell.setBorderWidth(1f);

        Paragraph valuePara = new Paragraph(value,
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20, color));
        valuePara.setAlignment(Element.ALIGN_CENTER);
        cell.addElement(valuePara);

        Paragraph labelPara = new Paragraph(label,
                FontFactory.getFont(FontFactory.HELVETICA, 8, new BaseColor(96, 125, 139)));
        labelPara.setAlignment(Element.ALIGN_CENTER);
        labelPara.setSpacingBefore(3);
        cell.addElement(labelPara);

        table.addCell(cell);
    }

    /**
     * Agregar detalle de una modalidad
     */
    private void addModalityDetail(Document document, DirectorAssignedModalitiesReportDTO.ModalityDetailDTO modality, int number)
            throws DocumentException {

        PdfPTable modalityTable = new PdfPTable(1);
        modalityTable.setWidthPercentage(100);
        modalityTable.setSpacingAfter(10);

        PdfPCell modalityCell = new PdfPCell();
        modalityCell.setBackgroundColor(LIGHT_GOLD);
        modalityCell.setPadding(10);
        modalityCell.setBorder(Rectangle.BOX);
        modalityCell.setBorderColor(LIGHT_GOLD);

        // Título
        Paragraph title = new Paragraph(number + ". " + modality.getModalityTypeName(),
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, TEXT_DARK));
        modalityCell.addElement(title);

        // Estudiantes
        StringBuilder students = new StringBuilder("Estudiantes: ");
        for (int i = 0; i < modality.getStudents().size(); i++) {
            if (i > 0) students.append(", ");
            DirectorAssignedModalitiesReportDTO.StudentBasicInfoDTO student = modality.getStudents().get(i);
            students.append(student.getFullName());
            if (student.getIsLeader()) students.append(" (Líder)");
        }
        Paragraph studentsPara = new Paragraph(students.toString(), SMALL_FONT);
        studentsPara.setSpacingBefore(3);
        modalityCell.addElement(studentsPara);

        // Estado
        Paragraph status = new Paragraph("Estado: " + modality.getStatusDescription(),
                FontFactory.getFont(FontFactory.HELVETICA, 9, INSTITUTIONAL_GOLD));
        status.setSpacingBefore(3);
        modalityCell.addElement(status);

        // Observaciones si hay
        if (modality.getObservations() != null && !"Sin observaciones".equals(modality.getObservations())) {
            Paragraph obs = new Paragraph("⚡ " + modality.getObservations(),
                    FontFactory.getFont(FontFactory.HELVETICA, 8, new BaseColor(220, 53, 69)));
            obs.setSpacingBefore(3);
            modalityCell.addElement(obs);
        }

        modalityTable.addCell(modalityCell);
        document.add(modalityTable);
    }

    /**
     * Estadísticas por estado y tipo
     */
    private void addStatisticsByStatusAndType(Document document, DirectorAssignedModalitiesReportDTO report)
            throws DocumentException {

        addSectionTitle(document, "4. ESTADÍSTICAS POR ESTADO Y TIPO");

        // Por estado
        Paragraph statusTitle = new Paragraph("Distribución por Estado:", SUBHEADER_FONT);
        statusTitle.setSpacingAfter(10);
        document.add(statusTitle);

        Map<String, Integer> byStatus = report.getModalitiesByStatus();
        int totalByStatus = byStatus.values().stream().mapToInt(Integer::intValue).sum();

        // Ordenar por cantidad
        byStatus.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .forEach(entry -> {
                    try {
                        addStatisticBar(document, entry.getKey(), entry.getValue(), totalByStatus, INSTITUTIONAL_GOLD);
                    } catch (DocumentException e) {
                        // Ignorar
                    }
                });

        document.add(Chunk.NEWLINE);
        document.add(Chunk.NEWLINE);

        // Por tipo
        Paragraph typeTitle = new Paragraph("Distribución por Tipo de Modalidad:", SUBHEADER_FONT);
        typeTitle.setSpacingAfter(10);
        document.add(typeTitle);

        Map<String, Integer> byType = report.getModalitiesByType();
        int totalByType = byType.values().stream().mapToInt(Integer::intValue).sum();

        byType.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .forEach(entry -> {
                    try {
                        addStatisticBar(document, entry.getKey(), entry.getValue(), totalByType, INSTITUTIONAL_RED);
                    } catch (DocumentException e) {
                        // Ignorar
                    }
                });
    }

    /**
     * Agregar barra de estadística
     */
    private void addStatisticBar(Document document, String label, int count, int total, BaseColor color)
            throws DocumentException {

        PdfPTable barTable = new PdfPTable(3);
        barTable.setWidthPercentage(100);
        barTable.setWidths(new int[]{35, 50, 15});
        barTable.setSpacingAfter(5);

        // Etiqueta
        PdfPCell labelCell = new PdfPCell(new Phrase(label, SMALL_FONT));
        labelCell.setBorder(Rectangle.NO_BORDER);
        labelCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        labelCell.setPadding(5);
        barTable.addCell(labelCell);

        // Barra
        PdfPCell barCell = new PdfPCell();
        barCell.setBorder(Rectangle.NO_BORDER);
        barCell.setPadding(5);

        PdfPTable innerTable = new PdfPTable(2);
        float percentage = total > 0 ? ((float) count / total * 100) : 0;
        float barWidth = Math.max(percentage, 0.1f);
        float emptyWidth = 100 - barWidth;

        try {
            innerTable.setWidths(new float[]{barWidth, emptyWidth});
        } catch (Exception e) {
            try {
                innerTable.setWidths(new float[]{50, 50});
            } catch (Exception ex) {
                // Ignorar
            }
        }

        PdfPCell filledCell = new PdfPCell(new Phrase(String.valueOf(count),
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8, BaseColor.WHITE)));
        filledCell.setBackgroundColor(color);
        filledCell.setBorder(Rectangle.NO_BORDER);
        filledCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        filledCell.setPadding(3);
        innerTable.addCell(filledCell);

        PdfPCell emptyCell = new PdfPCell();
        emptyCell.setBackgroundColor(LIGHT_GOLD);
        emptyCell.setBorder(Rectangle.NO_BORDER);
        innerTable.addCell(emptyCell);

        barCell.addElement(innerTable);
        barTable.addCell(barCell);

        // Porcentaje
        PdfPCell percentCell = new PdfPCell(new Phrase(String.format("%.1f%%", percentage),
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, color)));
        percentCell.setBorder(Rectangle.NO_BORDER);
        percentCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        percentCell.setPadding(5);
        barTable.addCell(percentCell);

        document.add(barTable);
    }

    /**
     * Recomendaciones
     */
    private void addRecommendations(Document document, DirectorAssignedModalitiesReportDTO report)
            throws DocumentException {

        addSectionTitle(document, "5. RECOMENDACIONES");

        List<String> recommendations = generateRecommendations(report);

        for (int i = 0; i < recommendations.size(); i++) {
            Paragraph recommendation = new Paragraph((i + 1) + ". " + recommendations.get(i), NORMAL_FONT);
            recommendation.setSpacingAfter(10);
            recommendation.setIndentationLeft(20);
            document.add(recommendation);
        }

        // Footer informativo
        PdfPTable footerTable = new PdfPTable(1);
        footerTable.setWidthPercentage(100);
        footerTable.setSpacingBefore(30);

        PdfPCell footerCell = new PdfPCell();
        footerCell.setBackgroundColor(LIGHT_GOLD);
        footerCell.setPadding(10);
        footerCell.setBorder(Rectangle.BOX);
        footerCell.setBorderColor(INSTITUTIONAL_RED);

        Paragraph footerText = new Paragraph();
        footerText.add(new Chunk("ℹ NOTA: ",
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, new BaseColor(52, 73, 94))));
        footerText.add(new Chunk(
                "Este reporte fue generado automáticamente por el Sistema SIGMA. " +
                        "Los datos presentados corresponden al programa académico " +
                        report.getAcademicProgramName() + ". " +
                        "Para consultas o asignaciones de directores, contacte con la coordinación del programa.",
                FontFactory.getFont(FontFactory.HELVETICA, 8, BaseColor.GRAY)));
        footerCell.addElement(footerText);
        footerTable.addCell(footerCell);

        document.add(footerTable);
    }

    /**
     * Generar recomendaciones automáticas
     */
    private List<String> generateRecommendations(DirectorAssignedModalitiesReportDTO report) {
        List<String> recommendations = new java.util.ArrayList<>();

        DirectorAssignedModalitiesReportDTO.DirectorSummaryDTO summary = report.getSummary();

        // Recomendación sobre distribución
        recommendations.add("El programa cuenta con " + summary.getTotalDirectors() +
                " directores activos supervisando " + summary.getTotalModalitiesAssigned() +
                " modalidades de grado.");

        // Recomendación sobre promedio
        if (summary.getAverageModalitiesPerDirector() > 6) {
            recommendations.add("La carga promedio de " + summary.getAverageModalitiesPerDirector() +
                    " modalidades por director está por encima del recomendado (6). " +
                    "Se sugiere considerar la asignación de nuevos directores.");
        } else if (summary.getAverageModalitiesPerDirector() < 3) {
            recommendations.add("La carga promedio es de " + summary.getAverageModalitiesPerDirector() +
                    " modalidades por director, lo cual es apropiado y permite una supervisión de calidad.");
        }

        // Recomendación sobre sobrecarga
        if (summary.getDirectorsOverloaded() > 0) {
            recommendations.add("Se identificaron " + summary.getDirectorsOverloaded() +
                    " director(es) con carga alta o sobrecarga. Se recomienda redistribuir modalidades " +
                    "o asignar co-directores para equilibrar la carga de trabajo.");
        }

        // Recomendación sobre disponibilidad
        if (summary.getDirectorsAvailable() > 0) {
            recommendations.add("Existen " + summary.getDirectorsAvailable() +
                    " director(es) disponibles con capacidad para supervisar nuevas modalidades.");
        }

        // Recomendación sobre análisis de carga
        if (report.getWorkloadAnalysis() != null) {
            DirectorAssignedModalitiesReportDTO.WorkloadAnalysisDTO workload = report.getWorkloadAnalysis();
            if ("UNBALANCED".equals(workload.getOverallWorkloadStatus())) {
                recommendations.add("La distribución de carga de trabajo es desequilibrada. " +
                        "Se recomienda implementar un plan de redistribución de modalidades para " +
                        "optimizar la supervisión académica.");
            }
        }

        // Recomendación general
        recommendations.add("Se recomienda realizar seguimiento continuo de la carga de trabajo de " +
                "los directores y mantener una comunicación fluida para identificar necesidades de apoyo.");

        return recommendations;
    }

    // ==================== MÉTODOS HELPER ====================

    private void addSectionTitle(Document document, String title) throws DocumentException {
        Paragraph titlePara = new Paragraph(title, HEADER_FONT);
        titlePara.setSpacingBefore(10);
        titlePara.setSpacingAfter(15);
        document.add(titlePara);

        LineSeparator line = new LineSeparator();
        line.setLineColor(INSTITUTIONAL_RED);
        line.setLineWidth(2);
        document.add(new Chunk(line));
        document.add(Chunk.NEWLINE);
    }

    /**
     * Clase para manejar encabezados y pies de página
     */
    private static class PageEventHelper extends PdfPageEventHelper {
        private final DirectorAssignedModalitiesReportDTO report;

        public PageEventHelper(DirectorAssignedModalitiesReportDTO report) {
            this.report = report;
        }

        @Override
        public void onEndPage(PdfWriter writer, Document document) {
            PdfContentByte cb = writer.getDirectContent();

            // Pie de página
            Phrase footer = new Phrase(
                    "Página " + writer.getPageNumber() + " | " +
                            report.getAcademicProgramName() + " | " +
                            "Generado: " + report.getGeneratedAt().format(
                            DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                    FontFactory.getFont(FontFactory.HELVETICA, 8, BaseColor.GRAY));

            ColumnText.showTextAligned(cb, Element.ALIGN_CENTER, footer,
                    (document.right() - document.left()) / 2 + document.leftMargin(),
                    document.bottom() - 10, 0);
        }
    }
}


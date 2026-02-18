package com.SIGMA.USCO.report.service;

import com.SIGMA.USCO.report.dto.ModalityTypeComparisonReportDTO;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.itextpdf.text.pdf.draw.LineSeparator;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

/**
 * Servicio para generar PDF del reporte comparativo de modalidades por tipo
 * RF-48 - Comparativa de Modalidades por Tipo de Grado
 */
@Service
public class ModalityComparisonPdfGenerator {

    // COLORES INSTITUCIONALES - USO EXCLUSIVO
    private static final BaseColor INSTITUTIONAL_RED = new BaseColor(143, 30, 30); // #8F1E1E - Color primario
    private static final BaseColor INSTITUTIONAL_GOLD = new BaseColor(213, 203, 160); // #D5CBA0 - Color secundario
    private static final BaseColor WHITE = BaseColor.WHITE; // Color primario
    private static final BaseColor LIGHT_GOLD = new BaseColor(245, 242, 235); // Tono muy claro de dorado para fondos sutiles
    private static final BaseColor TEXT_BLACK = BaseColor.BLACK; // Texto principal
    private static final BaseColor TEXT_GRAY = new BaseColor(80, 80, 80); // Texto secundario

    // Fuentes con colores institucionales
    private static final Font TITLE_FONT = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20, INSTITUTIONAL_RED);
    private static final Font SUBTITLE_FONT = FontFactory.getFont(FontFactory.HELVETICA, 16, INSTITUTIONAL_RED);
    private static final Font HEADER_FONT = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 15, INSTITUTIONAL_RED);
    private static final Font SUBHEADER_FONT = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, INSTITUTIONAL_RED);
    private static final Font BOLD_FONT = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, TEXT_BLACK);
    private static final Font NORMAL_FONT = FontFactory.getFont(FontFactory.HELVETICA, 10, TEXT_BLACK);
    private static final Font SMALL_FONT = FontFactory.getFont(FontFactory.HELVETICA, 9, TEXT_GRAY);
    private static final Font TINY_FONT = FontFactory.getFont(FontFactory.HELVETICA, 8, TEXT_GRAY);
    private static final Font HEADER_TABLE_FONT = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, WHITE);

    public ByteArrayOutputStream generatePDF(ModalityTypeComparisonReportDTO report)
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

        // 3. Estadísticas Detalladas por Tipo
        document.newPage();
        addDetailedStatistics(document, report);

        // 4. Distribución de Estudiantes
        if (report.getStudentDistributionByType() != null && !report.getStudentDistributionByType().isEmpty()) {
            document.newPage();
            addStudentDistribution(document, report);
        }

        // 5. Comparación Histórica
        if (report.getHistoricalComparison() != null && !report.getHistoricalComparison().isEmpty()) {
            document.newPage();
            addHistoricalComparison(document, report);
        }

        // 6. Análisis de Tendencias
        if (report.getTrendsAnalysis() != null) {
            document.newPage();
            addTrendsAnalysis(document, report);
        }

        // 7. Conclusiones
        document.newPage();
        addConclusions(document, report);

        document.close();
        return outputStream;
    }

    /**
     * Portada del reporte con diseño profesional
     */
    private void addCoverPage(Document document, ModalityTypeComparisonReportDTO report)
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
        Paragraph title = new Paragraph("REPORTE COMPARATIVO DE\nMODALIDADES POR TIPO DE GRADO",
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20, INSTITUTIONAL_RED));
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(35);
        document.add(title);

        // Información del periodo (si existe)
        if (report.getYear() != null) {
            PdfPTable periodBox = new PdfPTable(1);
            periodBox.setWidthPercentage(60);
            periodBox.setHorizontalAlignment(Element.ALIGN_CENTER);
            periodBox.setSpacingAfter(35);

            PdfPCell periodCell = new PdfPCell();
            periodCell.setBackgroundColor(INSTITUTIONAL_GOLD);
            periodCell.setPadding(10);
            periodCell.setBorder(Rectangle.BOX);
            periodCell.setBorderColor(INSTITUTIONAL_RED);

            String periodo = "Periodo: " + report.getYear();
            if (report.getSemester() != null) {
                periodo += " - Semestre " + report.getSemester();
            }

            Paragraph periodoPara = new Paragraph(periodo,
                    FontFactory.getFont(FontFactory.HELVETICA_BOLD, 13, INSTITUTIONAL_RED));
            periodoPara.setAlignment(Element.ALIGN_CENTER);
            periodCell.addElement(periodoPara);

            periodBox.addCell(periodCell);
            document.add(periodBox);
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
        addCoverInfoRow(infoTable, "Tipos de Modalidad:", String.valueOf(report.getSummary().getTotalModalityTypes()));
        addCoverInfoRow(infoTable, "Total de Modalidades:", String.valueOf(report.getSummary().getTotalModalities()));

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
                FontFactory.getFont(FontFactory.HELVETICA, 9, BaseColor.DARK_GRAY));
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
    private void addExecutiveSummary(Document document, ModalityTypeComparisonReportDTO report)
            throws DocumentException {

        addSectionTitle(document, "1. RESUMEN EJECUTIVO");

        ModalityTypeComparisonReportDTO.ComparisonSummaryDTO summary = report.getSummary();

        // Tabla de resumen principal con diseño mejorado
        PdfPTable summaryTable = new PdfPTable(3);
        summaryTable.setWidthPercentage(100);
        summaryTable.setWidths(new int[]{34, 33, 33});
        summaryTable.setSpacingAfter(25);

        // Tarjeta 1: Total de tipos
        addMetricCard(summaryTable, "Tipos de Modalidad",
                String.valueOf(summary.getTotalModalityTypes()), INSTITUTIONAL_GOLD);

        // Tarjeta 2: Total de modalidades
        addMetricCard(summaryTable, "Total Modalidades",
                String.valueOf(summary.getTotalModalities()), INSTITUTIONAL_RED);

        // Tarjeta 3: Total de estudiantes
        addMetricCard(summaryTable, "Total Estudiantes",
                String.valueOf(summary.getTotalStudents()), INSTITUTIONAL_GOLD);

        document.add(summaryTable);

        // Tipo más popular con diseño mejorado
        if (summary.getMostPopularType() != null) {
            PdfPTable popularTable = new PdfPTable(1);
            popularTable.setWidthPercentage(100);
            popularTable.setSpacingAfter(12);

            PdfPCell popularCell = new PdfPCell();
            popularCell.setBackgroundColor(new BaseColor(232, 245, 233)); // Verde claro
            popularCell.setPadding(12);
            popularCell.setBorderColor(INSTITUTIONAL_GOLD);
            popularCell.setBorderWidth(2f);

            Paragraph popularText = new Paragraph();
            popularText.add(new Chunk("⭐ TIPO MÁS POPULAR: ",
                    FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, INSTITUTIONAL_GOLD)));
            popularText.add(new Chunk(summary.getMostPopularType() + " ",
                    FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, TEXT_BLACK)));
            popularText.add(new Chunk("(" + summary.getMostPopularTypeCount() + " modalidades)",
                    FontFactory.getFont(FontFactory.HELVETICA, 10, new BaseColor(76, 175, 80))));
            popularCell.addElement(popularText);
            popularTable.addCell(popularCell);

            document.add(popularTable);
        }

        // Tipo menos popular con diseño mejorado
        if (summary.getLeastPopularType() != null && summary.getLeastPopularTypeCount() > 0) {
            PdfPTable leastPopularTable = new PdfPTable(1);
            leastPopularTable.setWidthPercentage(100);
            leastPopularTable.setSpacingAfter(20);

            PdfPCell leastCell = new PdfPCell();
            leastCell.setBackgroundColor(new BaseColor(255, 243, 224)); // Naranja claro
            leastCell.setPadding(12);
            leastCell.setBorderColor(INSTITUTIONAL_RED);
            leastCell.setBorderWidth(2f);

            Paragraph leastText = new Paragraph();
            leastText.add(new Chunk("📊 TIPO MENOS POPULAR: ",
                    FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, new BaseColor(255, 152, 0))));
            leastText.add(new Chunk(summary.getLeastPopularType() + " ",
                    FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, TEXT_BLACK)));
            leastText.add(new Chunk("(" + summary.getLeastPopularTypeCount() + " modalidades)",
                    FontFactory.getFont(FontFactory.HELVETICA, 10, new BaseColor(255, 152, 0))));
            leastCell.addElement(leastText);
            leastPopularTable.addCell(leastCell);

            document.add(leastPopularTable);
        }
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
                FontFactory.getFont(FontFactory.HELVETICA, 10, new BaseColor(96, 125, 139)));
        labelPara.setAlignment(Element.ALIGN_CENTER);
        card.addElement(labelPara);

        table.addCell(card);
    }

    /**
     * Estadísticas detalladas por tipo
     */
    private void addDetailedStatistics(Document document, ModalityTypeComparisonReportDTO report)
            throws DocumentException {

        addSectionTitle(document, "2. ESTADÍSTICAS DETALLADAS POR TIPO DE MODALIDAD");

        List<ModalityTypeComparisonReportDTO.ModalityTypeStatisticsDTO> statistics =
                report.getModalityTypeStatistics();

        for (int i = 0; i < statistics.size(); i++) {
            ModalityTypeComparisonReportDTO.ModalityTypeStatisticsDTO stat = statistics.get(i);

            // Título del tipo
            Paragraph typeTitle = new Paragraph((i + 1) + ". " + stat.getModalityTypeName(), SUBHEADER_FONT);
            typeTitle.setSpacingBefore(i > 0 ? 15 : 5);
            typeTitle.setSpacingAfter(5);
            document.add(typeTitle);

            if (stat.getDescription() != null && !stat.getDescription().isEmpty()) {
                Paragraph description = new Paragraph(stat.getDescription(), SMALL_FONT);
                description.setSpacingAfter(10);
                document.add(description);
            }

            // Tabla de estadísticas con diseño mejorado
            PdfPTable statsTable = new PdfPTable(4);
            statsTable.setWidthPercentage(100);
            statsTable.setWidths(new int[]{25, 25, 25, 25});
            statsTable.setSpacingAfter(10);

            addStatCell(statsTable, "Total Modalidades:", String.valueOf(stat.getTotalModalities()));
            addStatCell(statsTable, "Total Estudiantes:", String.valueOf(stat.getTotalStudents()));
            addStatCell(statsTable, "Porcentaje del Total:", stat.getPercentageOfTotal() + "%");
            addStatCell(statsTable, "Requiere Director:", stat.getRequiresDirector() ? "Sí" : "No");

            document.add(statsTable);

            // Información de directores si aplica
            if (stat.getRequiresDirector()) {
                PdfPTable directorTable = new PdfPTable(2);
                directorTable.setWidthPercentage(80);
                directorTable.setSpacingBefore(5);
                directorTable.setSpacingAfter(10);

                PdfPCell withDirectorCell = new PdfPCell(new Phrase("Con Director Asignado: " +
                        stat.getModalitiesWithDirector(), SMALL_FONT));
                withDirectorCell.setBackgroundColor(new BaseColor(232, 245, 233));
                withDirectorCell.setPadding(6);
                withDirectorCell.setBorder(Rectangle.NO_BORDER);
                directorTable.addCell(withDirectorCell);

                PdfPCell withoutDirectorCell = new PdfPCell(new Phrase("Sin Director: " +
                        stat.getModalitiesWithoutDirector(), SMALL_FONT));
                withoutDirectorCell.setBackgroundColor(stat.getModalitiesWithoutDirector() > 0 ?
                        new BaseColor(255, 243, 224) : new BaseColor(248, 249, 250));
                withoutDirectorCell.setPadding(6);
                withoutDirectorCell.setBorder(Rectangle.NO_BORDER);
                directorTable.addCell(withoutDirectorCell);

                document.add(directorTable);
            }

            // Distribución por estado
            if (stat.getDistributionByStatus() != null && !stat.getDistributionByStatus().isEmpty()) {
                Paragraph statusTitle = new Paragraph("Distribución por Estado:", SMALL_FONT);
                statusTitle.setSpacingBefore(5);
                statusTitle.setSpacingAfter(3);
                document.add(statusTitle);

                PdfPTable statusTable = new PdfPTable(2);
                statusTable.setWidthPercentage(80);
                statusTable.setSpacingAfter(5);

                for (Map.Entry<String, Integer> entry : stat.getDistributionByStatus().entrySet()) {
                    PdfPCell labelCell = new PdfPCell(new Phrase(entry.getKey(), SMALL_FONT));
                    labelCell.setBorder(Rectangle.NO_BORDER);
                    labelCell.setHorizontalAlignment(Element.ALIGN_LEFT);
                    labelCell.setPaddingLeft(20);
                    statusTable.addCell(labelCell);

                    PdfPCell valueCell = new PdfPCell(new Phrase(entry.getValue() + " modalidades", SMALL_FONT));
                    valueCell.setBorder(Rectangle.NO_BORDER);
                    valueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    statusTable.addCell(valueCell);
                }

                document.add(statusTable);
            }

            // Línea separadora
            if (i < statistics.size() - 1) {
                LineSeparator separator = new LineSeparator();
                separator.setLineColor(INSTITUTIONAL_GOLD);
                document.add(new Chunk(separator));
            }
        }
    }

    /**
     * Distribución de estudiantes con diseño mejorado
     */
    private void addStudentDistribution(Document document, ModalityTypeComparisonReportDTO report)
            throws DocumentException {

        addSectionTitle(document, "3. DISTRIBUCIÓN DE ESTUDIANTES POR TIPO");

        Paragraph intro = new Paragraph(
                "Visualización de la cantidad de estudiantes únicos por tipo de modalidad:",
                NORMAL_FONT);
        intro.setSpacingAfter(20);
        document.add(intro);

        Map<String, Integer> distribution = report.getStudentDistributionByType();

        // Encontrar el máximo para escalar las barras
        int maxStudents = distribution.values().stream().max(Integer::compare).orElse(1);
        int totalStudents = distribution.values().stream().mapToInt(Integer::intValue).sum();

        // Ordenar por cantidad de estudiantes (descendente)
        List<Map.Entry<String, Integer>> sortedEntries = distribution.entrySet().stream()
                .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                .collect(java.util.stream.Collectors.toList());

        // Crear gráfico de barras mejorado
        for (Map.Entry<String, Integer> entry : sortedEntries) {
            // Contenedor de cada barra
            PdfPTable barContainer = new PdfPTable(1);
            barContainer.setWidthPercentage(100);
            barContainer.setSpacingAfter(12);

            // Encabezado con el nombre del tipo
            PdfPCell headerCell = new PdfPCell();
            headerCell.setBackgroundColor(INSTITUTIONAL_RED);
            headerCell.setPadding(6);
            headerCell.setBorder(Rectangle.NO_BORDER);

            Paragraph headerText = new Paragraph(entry.getKey(),
                    FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, BaseColor.WHITE));
            headerCell.addElement(headerText);
            barContainer.addCell(headerCell);

            // Contenedor de la barra y estadísticas
            PdfPCell barCell = new PdfPCell();
            barCell.setPadding(0);
            barCell.setBorder(Rectangle.BOX);
            barCell.setBorderColor(LIGHT_GOLD);
            barCell.setBorderWidth(0.5f);

            // Tabla interna para la barra
            PdfPTable innerTable = new PdfPTable(2);
            innerTable.setWidthPercentage(100);

            // Calcular el ancho de la barra (máximo 85%)
            float barWidth = (float) entry.getValue() / maxStudents * 85;
            float emptyWidth = 100 - barWidth;

            try {
                innerTable.setWidths(new float[]{barWidth > 0 ? barWidth : 0.1f, emptyWidth > 0 ? emptyWidth : 0.1f});
            } catch (DocumentException e) {
                // Si hay error, usar valores por defecto
                innerTable.setWidths(new float[]{50, 50});
            }

            // Celda con color de la barra
            PdfPCell filledCell = new PdfPCell();
            filledCell.setBackgroundColor(INSTITUTIONAL_GOLD);
            filledCell.setBorder(Rectangle.NO_BORDER);
            filledCell.setPadding(8);

            Paragraph barText = new Paragraph();
            barText.add(new Chunk(entry.getValue() + " estudiantes",
                    FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, BaseColor.WHITE)));
            filledCell.addElement(barText);
            innerTable.addCell(filledCell);

            // Celda vacía con el porcentaje
            PdfPCell emptyCell = new PdfPCell();
            emptyCell.setBackgroundColor(LIGHT_GOLD);
            emptyCell.setBorder(Rectangle.NO_BORDER);
            emptyCell.setPadding(8);

            double percentage = (double) entry.getValue() / totalStudents * 100;
            Paragraph percentText = new Paragraph(String.format("%.1f%% del total", percentage),
                    FontFactory.getFont(FontFactory.HELVETICA, 9, INSTITUTIONAL_RED));
            percentText.setAlignment(Element.ALIGN_LEFT);
            emptyCell.addElement(percentText);
            innerTable.addCell(emptyCell);

            barCell.addElement(innerTable);
            barContainer.addCell(barCell);

            document.add(barContainer);
        }

        // Agregar resumen total al final
        PdfPTable totalTable = new PdfPTable(1);
        totalTable.setWidthPercentage(100);
        totalTable.setSpacingBefore(15);

        PdfPCell totalCell = new PdfPCell();
        totalCell.setBackgroundColor(INSTITUTIONAL_GOLD);
        totalCell.setPadding(10);
        totalCell.setBorder(Rectangle.NO_BORDER);

        Paragraph totalText = new Paragraph("TOTAL DE ESTUDIANTES: " + totalStudents,
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.WHITE));
        totalText.setAlignment(Element.ALIGN_CENTER);
        totalCell.addElement(totalText);
        totalTable.addCell(totalCell);

        document.add(totalTable);
    }

    /**
     * Comparación histórica
     */
    private void addHistoricalComparison(Document document, ModalityTypeComparisonReportDTO report)
            throws DocumentException {

        addSectionTitle(document, "4. COMPARACIÓN HISTÓRICA POR PERIODOS");

        List<ModalityTypeComparisonReportDTO.PeriodComparisonDTO> periods = report.getHistoricalComparison();

        // Tabla de comparación
        int numPeriods = periods.size();
        PdfPTable comparisonTable = new PdfPTable(numPeriods + 1);
        comparisonTable.setWidthPercentage(100);
        comparisonTable.setSpacingAfter(20);

        // Encabezados
        addTableHeader(comparisonTable, "Tipo de Modalidad");
        for (ModalityTypeComparisonReportDTO.PeriodComparisonDTO period : periods) {
            addTableHeader(comparisonTable, period.getPeriodLabel());
        }

        // Obtener todos los tipos de modalidad
        Map<String, Integer> allTypes = report.getStudentDistributionByType();

        for (String typeName : allTypes.keySet()) {
            // Nombre del tipo
            PdfPCell typeCell = new PdfPCell(new Phrase(typeName, SMALL_FONT));
            typeCell.setBackgroundColor(LIGHT_GOLD);
            typeCell.setPadding(5);
            comparisonTable.addCell(typeCell);

            // Datos de cada periodo
            for (ModalityTypeComparisonReportDTO.PeriodComparisonDTO period : periods) {
                Integer count = period.getModalitiesByType().getOrDefault(typeName, 0);
                Integer students = period.getStudentsByType().getOrDefault(typeName, 0);

                Phrase phrase = new Phrase();
                phrase.add(new Chunk(count + " modalidades\n",
                        FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9)));
                phrase.add(new Chunk(students + " estudiantes",
                        FontFactory.getFont(FontFactory.HELVETICA, 8, BaseColor.GRAY)));

                PdfPCell dataCell = new PdfPCell(phrase);
                dataCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                dataCell.setPadding(5);
                comparisonTable.addCell(dataCell);
            }
        }

        // Totales
        PdfPCell totalLabelCell = new PdfPCell(new Phrase("TOTALES", BOLD_FONT));
        totalLabelCell.setBackgroundColor(INSTITUTIONAL_RED);
        totalLabelCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        totalLabelCell.setPadding(5);
        comparisonTable.addCell(totalLabelCell);

        for (ModalityTypeComparisonReportDTO.PeriodComparisonDTO period : periods) {
            Phrase totalPhrase = new Phrase();
            totalPhrase.add(new Chunk(period.getTotalModalitiesInPeriod() + " modalidades\n",
                    FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, BaseColor.WHITE)));
            totalPhrase.add(new Chunk(period.getTotalStudentsInPeriod() + " estudiantes",
                    FontFactory.getFont(FontFactory.HELVETICA, 8, BaseColor.WHITE)));

            PdfPCell totalCell = new PdfPCell(totalPhrase);
            totalCell.setBackgroundColor(INSTITUTIONAL_RED);
            totalCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            totalCell.setPadding(5);
            comparisonTable.addCell(totalCell);
        }

        document.add(comparisonTable);
    }

    /**
     * Análisis de tendencias
     */
    private void addTrendsAnalysis(Document document, ModalityTypeComparisonReportDTO report)
            throws DocumentException {

        addSectionTitle(document, "5. ANÁLISIS DE TENDENCIAS");

        ModalityTypeComparisonReportDTO.TrendsAnalysisDTO trends = report.getTrendsAnalysis();

        // Tendencia general
        PdfPTable overallTable = new PdfPTable(1);
        overallTable.setWidthPercentage(100);
        overallTable.setSpacingAfter(15);

        BaseColor trendColor;
        String trendIcon;
        switch (trends.getOverallTrend()) {
            case "GROWING":
                trendColor = INSTITUTIONAL_GOLD;
                trendIcon = "↗";
                break;
            case "DECLINING":
                trendColor = INSTITUTIONAL_RED;
                trendIcon = "↘";
                break;
            default:
                trendColor = INSTITUTIONAL_RED;
                trendIcon = "→";
        }

        PdfPCell trendCell = new PdfPCell();
        trendCell.setBackgroundColor(trendColor);
        trendCell.setPadding(10);
        trendCell.setBorderWidth(0);

        Paragraph trendPara = new Paragraph();
        trendPara.add(new Chunk(trendIcon + " TENDENCIA GENERAL: ",
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, BaseColor.WHITE)));
        trendPara.add(new Chunk(getTrendLabel(trends.getOverallTrend()),
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, BaseColor.WHITE)));
        trendCell.addElement(trendPara);
        overallTable.addCell(trendCell);

        document.add(overallTable);

        // Tipos en crecimiento
        if (trends.getGrowingTypes() != null && !trends.getGrowingTypes().isEmpty()) {
            addTrendSection(document, "✓ TIPOS EN CRECIMIENTO", trends.getGrowingTypes(),
                    trends.getGrowthRateByType(), INSTITUTIONAL_GOLD);
        }

        // Tipos en declive
        if (trends.getDecliningTypes() != null && !trends.getDecliningTypes().isEmpty()) {
            addTrendSection(document, "✗ TIPOS EN DECLIVE", trends.getDecliningTypes(),
                    trends.getGrowthRateByType(), INSTITUTIONAL_RED);
        }

        // Tipos estables
        if (trends.getStableTypes() != null && !trends.getStableTypes().isEmpty()) {
            addTrendSection(document, "= TIPOS ESTABLES", trends.getStableTypes(),
                    trends.getGrowthRateByType(), INSTITUTIONAL_RED);
        }

        // Destacados
        if (trends.getMostImprovedType() != null) {
            PdfPTable improvedTable = new PdfPTable(1);
            improvedTable.setWidthPercentage(100);
            improvedTable.setSpacingBefore(15);

            PdfPCell improvedCell = new PdfPCell();
            improvedCell.setBackgroundColor(LIGHT_GOLD);
            improvedCell.setPadding(8);

            Double rate = trends.getGrowthRateByType().get(trends.getMostImprovedType());
            Paragraph improvedPara = new Paragraph(
                    "🏆 Mayor Mejora: " + trends.getMostImprovedType() +
                    " (+" + String.format("%.2f", rate) + "%)",
                    BOLD_FONT);
            improvedCell.addElement(improvedPara);
            improvedTable.addCell(improvedCell);

            document.add(improvedTable);
        }

        if (trends.getMostDeclinedType() != null) {
            PdfPTable declinedTable = new PdfPTable(1);
            declinedTable.setWidthPercentage(100);
            declinedTable.setSpacingBefore(5);

            PdfPCell declinedCell = new PdfPCell();
            declinedCell.setBackgroundColor(new BaseColor(255, 230, 230));
            declinedCell.setPadding(8);

            Double rate = trends.getGrowthRateByType().get(trends.getMostDeclinedType());
            Paragraph declinedPara = new Paragraph(
                    "⚠ Mayor Declive: " + trends.getMostDeclinedType() +
                    " (" + String.format("%.2f", rate) + "%)",
                    BOLD_FONT);
            declinedCell.addElement(declinedPara);
            declinedTable.addCell(declinedCell);

            document.add(declinedTable);
        }
    }

    /**
     * Conclusiones
     */
    private void addConclusions(Document document, ModalityTypeComparisonReportDTO report)
            throws DocumentException {

        addSectionTitle(document, "6. CONCLUSIONES Y RECOMENDACIONES");

        List<String> conclusions = generateConclusions(report);

        for (int i = 0; i < conclusions.size(); i++) {
            Paragraph conclusion = new Paragraph((i + 1) + ". " + conclusions.get(i), NORMAL_FONT);
            conclusion.setSpacingAfter(10);
            conclusion.setIndentationLeft(20);
            document.add(conclusion);
        }

        // Footer informativo
        PdfPTable footerTable = new PdfPTable(1);
        footerTable.setWidthPercentage(100);
        footerTable.setSpacingBefore(30);

        PdfPCell footerCell = new PdfPCell();
        footerCell.setBackgroundColor(LIGHT_GOLD);
        footerCell.setPadding(10);
        footerCell.setBorder(Rectangle.BOX);

        Paragraph footerText = new Paragraph();
        footerText.add(new Chunk("ℹ NOTA: ",
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, BaseColor.DARK_GRAY)));
        footerText.add(new Chunk(
                "Este reporte fue generado automáticamente por el Sistema SIGMA. " +
                "Los datos presentados corresponden al programa académico " +
                report.getAcademicProgramName() + " y están filtrados según los criterios especificados. " +
                "Para consultas o análisis adicionales, contacte con la coordinación del programa.",
                FontFactory.getFont(FontFactory.HELVETICA, 8, BaseColor.GRAY)));
        footerCell.addElement(footerText);
        footerTable.addCell(footerCell);

        document.add(footerTable);
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

    private void addInfoRow(PdfPTable table, String label, String value) {
        PdfPCell labelCell = new PdfPCell(new Phrase(label, BOLD_FONT));
        labelCell.setBorder(Rectangle.NO_BORDER);
        labelCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        labelCell.setPadding(5);
        table.addCell(labelCell);

        PdfPCell valueCell = new PdfPCell(new Phrase(value, NORMAL_FONT));
        valueCell.setBorder(Rectangle.NO_BORDER);
        valueCell.setHorizontalAlignment(Element.ALIGN_LEFT);
        valueCell.setPadding(5);
        table.addCell(valueCell);
    }

    private void addSummaryRow(PdfPTable table, String label, String value, Font valueFont) {
        PdfPCell labelCell = new PdfPCell(new Phrase(label, NORMAL_FONT));
        labelCell.setBackgroundColor(LIGHT_GOLD);
        labelCell.setPadding(8);
        table.addCell(labelCell);

        PdfPCell valueCell = new PdfPCell(new Phrase(value, valueFont));
        valueCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        valueCell.setPadding(8);
        table.addCell(valueCell);
    }

    private void addStatCell(PdfPTable table, String label, String value) {
        PdfPCell labelCell = new PdfPCell(new Phrase(label, SMALL_FONT));
        labelCell.setBackgroundColor(LIGHT_GOLD);
        labelCell.setPadding(5);
        table.addCell(labelCell);

        PdfPCell valueCell = new PdfPCell(new Phrase(value, BOLD_FONT));
        valueCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        valueCell.setPadding(5);
        table.addCell(valueCell);
    }

    private void addTableHeader(PdfPTable table, String text) {
        PdfPCell header = new PdfPCell(new Phrase(text,
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, BaseColor.WHITE)));
        header.setBackgroundColor(INSTITUTIONAL_RED);
        header.setHorizontalAlignment(Element.ALIGN_CENTER);
        header.setPadding(8);
        table.addCell(header);
    }

    private void addTrendSection(Document document, String title, List<String> types,
            Map<String, Double> rates, BaseColor color) throws DocumentException {

        Paragraph sectionTitle = new Paragraph(title, SUBHEADER_FONT);
        sectionTitle.setSpacingBefore(10);
        sectionTitle.setSpacingAfter(5);
        document.add(sectionTitle);

        PdfPTable trendTable = new PdfPTable(2);
        trendTable.setWidthPercentage(90);
        trendTable.setWidths(new int[]{70, 30});
        trendTable.setSpacingAfter(10);

        for (String type : types) {
            PdfPCell typeCell = new PdfPCell(new Phrase(type, NORMAL_FONT));
            typeCell.setPadding(5);
            trendTable.addCell(typeCell);

            Double rate = rates.get(type);
            String rateStr = rate != null ? String.format("%+.2f%%", rate) : "N/A";
            PdfPCell rateCell = new PdfPCell(new Phrase(rateStr, BOLD_FONT));
            rateCell.setBackgroundColor(color);
            rateCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            rateCell.setPadding(5);
            trendTable.addCell(rateCell);
        }

        document.add(trendTable);
    }

    private String getTrendLabel(String trend) {
        switch (trend) {
            case "GROWING":
                return "EN CRECIMIENTO";
            case "DECLINING":
                return "EN DECLIVE";
            default:
                return "ESTABLE";
        }
    }

    private List<String> generateConclusions(ModalityTypeComparisonReportDTO report) {
        List<String> conclusions = new java.util.ArrayList<>();

        ModalityTypeComparisonReportDTO.ComparisonSummaryDTO summary = report.getSummary();

        // Conclusión sobre diversidad
        conclusions.add("El programa ofrece " + summary.getTotalModalityTypes() +
                " tipos diferentes de modalidades de grado, con un total de " +
                summary.getTotalModalities() + " modalidades activas.");

        // Conclusión sobre tipo más popular
        if (summary.getMostPopularType() != null) {
            conclusions.add("El tipo de modalidad más popular es \"" + summary.getMostPopularType() +
                    "\", con " + summary.getMostPopularTypeCount() + " modalidades, lo que representa " +
                    "una preferencia significativa de los estudiantes.");
        }

        // Conclusión sobre distribución
        conclusions.add("En promedio, cada tipo de modalidad tiene " +
                summary.getAverageModalitiesPerType() + " modalidades y " +
                summary.getAverageStudentsPerType() + " estudiantes.");

        // Conclusiones de tendencias
        if (report.getTrendsAnalysis() != null) {
            ModalityTypeComparisonReportDTO.TrendsAnalysisDTO trends = report.getTrendsAnalysis();

            switch (trends.getOverallTrend()) {
                case "GROWING":
                    conclusions.add("La tendencia general del programa es de crecimiento, " +
                            "con " + trends.getGrowingTypes().size() + " tipos de modalidad en expansión.");
                    break;
                case "DECLINING":
                    conclusions.add("Se observa una tendencia general de declive, " +
                            "sugiriendo la necesidad de revisar la oferta académica.");
                    break;
                default:
                    conclusions.add("El programa muestra una tendencia estable en la " +
                            "distribución de tipos de modalidad.");
            }
        }

        // Recomendaciones
        conclusions.add("Se recomienda continuar monitoreando las preferencias estudiantiles " +
                "y ajustar la oferta de modalidades según la demanda observada.");

        return conclusions;
    }

    /**
     * Clase para manejar encabezados y pies de página
     */
    private static class PageEventHelper extends PdfPageEventHelper {
        private final ModalityTypeComparisonReportDTO report;

        public PageEventHelper(ModalityTypeComparisonReportDTO report) {
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
                    TINY_FONT);

            ColumnText.showTextAligned(cb, Element.ALIGN_CENTER, footer,
                    (document.right() - document.left()) / 2 + document.leftMargin(),
                    document.bottom() - 10, 0);
        }
    }
}


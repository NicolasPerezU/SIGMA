package com.SIGMA.USCO.report.service;

import com.SIGMA.USCO.report.dto.ModalityHistoricalReportDTO;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.itextpdf.text.pdf.draw.LineSeparator;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;

/**
 * Servicio para generar PDF del reporte histórico de modalidad
 * Análisis temporal completo con diseño profesional e institucional
 */
@Service
public class ModalityHistoricalPdfGenerator {

    // Fuentes profesionales
    private static final Font TITLE_FONT = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 22, BaseColor.BLACK);
    private static final Font SUBTITLE_FONT = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, BaseColor.DARK_GRAY);
    private static final Font HEADER_FONT = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 15, new BaseColor(44, 62, 80));
    private static final Font SUBHEADER_FONT = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, new BaseColor(52, 73, 94));
    private static final Font BOLD_FONT = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, BaseColor.BLACK);
    private static final Font NORMAL_FONT = FontFactory.getFont(FontFactory.HELVETICA, 10, BaseColor.BLACK);
    private static final Font SMALL_FONT = FontFactory.getFont(FontFactory.HELVETICA, 9, new BaseColor(52, 73, 94));
    private static final Font TINY_FONT = FontFactory.getFont(FontFactory.HELVETICA, 8, BaseColor.GRAY);

    // Colores institucionales profesionales
    private static final BaseColor PRIMARY_COLOR = new BaseColor(0, 51, 102);
    private static final BaseColor SECONDARY_COLOR = new BaseColor(0, 102, 153);
    private static final BaseColor ACCENT_COLOR = new BaseColor(204, 153, 0);
    private static final BaseColor LIGHT_BLUE = new BaseColor(230, 240, 250);
    private static final BaseColor LIGHT_GRAY = new BaseColor(248, 249, 250);
    private static final BaseColor MEDIUM_GRAY = new BaseColor(233, 236, 239);
    private static final BaseColor SUCCESS_COLOR = new BaseColor(40, 167, 69);
    private static final BaseColor WARNING_COLOR = new BaseColor(255, 193, 7);
    private static final BaseColor DANGER_COLOR = new BaseColor(220, 53, 69);
    private static final BaseColor INFO_COLOR = new BaseColor(23, 162, 184);
    private static final BaseColor TEXT_DARK = new BaseColor(33, 37, 41);

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final DateTimeFormatter SIMPLE_DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public ByteArrayOutputStream generatePDF(ModalityHistoricalReportDTO report)
            throws DocumentException, IOException {

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4, 50, 50, 50, 50);
        PdfWriter writer = PdfWriter.getInstance(document, outputStream);

        // Agregar eventos de página (encabezado y pie de página)
        HistoricalPageEventHelper pageEvent = new HistoricalPageEventHelper(report);
        writer.setPageEvent(pageEvent);

        document.open();

        // 1. Portada
        addCoverPage(document, report);

        // 2. Información General de la Modalidad
        document.newPage();
        addModalityGeneralInfo(document, report);

        // 3. Estado Actual
        document.newPage();
        addCurrentState(document, report);

        // 4. Análisis Histórico por Periodos
        document.newPage();
        addHistoricalAnalysis(document, report);

        // 5. Análisis de Tendencias y Evolución
        document.newPage();
        addTrendsAnalysis(document, report);

        // 6. Análisis Comparativo
        document.newPage();
        addComparativeAnalysis(document, report);

        // 7. Estadísticas de Directores
        if (report.getDirectorStatistics() != null &&
            report.getDirectorStatistics().getTotalUniqueDirectors() > 0) {
            document.newPage();
            addDirectorStatistics(document, report);
        }

        // 8. Estadísticas de Estudiantes
        document.newPage();
        addStudentStatistics(document, report);

        // 9. Análisis de Desempeño
        document.newPage();
        addPerformanceAnalysis(document, report);

        // 10. Proyecciones y Recomendaciones
        document.newPage();
        addProjectionsAndRecommendations(document, report);

        document.close();
        return outputStream;
    }

    /**
     * Portada del reporte con diseño profesional
     */
    private void addCoverPage(Document document, ModalityHistoricalReportDTO report)
            throws DocumentException {

        // Banda superior institucional
        PdfPTable headerBand = new PdfPTable(1);
        headerBand.setWidthPercentage(100);
        headerBand.setSpacingAfter(40);

        PdfPCell bandCell = new PdfPCell();
        bandCell.setBackgroundColor(PRIMARY_COLOR);
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
        Paragraph title = new Paragraph("ANÁLISIS HISTÓRICO DE MODALIDAD", TITLE_FONT);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(20);
        document.add(title);

        // Nombre de la modalidad analizada
        if (report.getModalityInfo() != null) {
            Paragraph modalityName = new Paragraph(
                report.getModalityInfo().getModalityName().toUpperCase(),
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, ACCENT_COLOR)
            );
            modalityName.setAlignment(Element.ALIGN_CENTER);
            modalityName.setSpacingAfter(40);
            document.add(modalityName);
        }

        // Cuadro de información del reporte
        PdfPTable infoTable = new PdfPTable(2);
        infoTable.setWidthPercentage(80);
        infoTable.setWidths(new float[]{1f, 1.5f});
        infoTable.setSpacingBefore(30);
        infoTable.setHorizontalAlignment(Element.ALIGN_CENTER);

        addInfoRow(infoTable, "Tipo de Reporte:", "Análisis Histórico Temporal");
        addInfoRow(infoTable, "Fecha de Generación:",
            report.getGeneratedAt().format(DATE_FORMATTER));
        addInfoRow(infoTable, "Generado Por:", report.getGeneratedBy());

        if (report.getModalityInfo() != null) {
            addInfoRow(infoTable, "Periodos Analizados:",
                String.valueOf(report.getHistoricalAnalysis() != null ?
                    report.getHistoricalAnalysis().size() : 0));
            addInfoRow(infoTable, "Años Activos:",
                report.getModalityInfo().getYearsActive() + " años");
            addInfoRow(infoTable, "Instancias Totales:",
                String.valueOf(report.getModalityInfo().getTotalHistoricalInstances()));
        }

        document.add(infoTable);

        // Línea separadora decorativa
        document.add(new Paragraph("\n\n\n"));
        LineSeparator line = new LineSeparator();
        line.setLineColor(ACCENT_COLOR);
        line.setLineWidth(2);
        document.add(new Chunk(line));

        // Advertencia/Nota
        document.add(new Paragraph("\n\n"));
        Paragraph disclaimer = new Paragraph(
            "Este reporte presenta un análisis detallado de la evolución histórica de la modalidad, " +
            "incluyendo tendencias, estadísticas comparativas, desempeño y proyecciones futuras. " +
            "La información es generada automáticamente por el sistema SIGMA.",
            SMALL_FONT
        );
        disclaimer.setAlignment(Element.ALIGN_JUSTIFIED);
        disclaimer.setIndentationLeft(50);
        disclaimer.setIndentationRight(50);
        document.add(disclaimer);
    }

    /**
     * Información general de la modalidad
     */
    private void addModalityGeneralInfo(Document document, ModalityHistoricalReportDTO report)
            throws DocumentException {

        addSectionTitle(document, "1. INFORMACIÓN GENERAL DE LA MODALIDAD");

        if (report.getModalityInfo() == null) {
            document.add(new Paragraph("No hay información disponible.", NORMAL_FONT));
            return;
        }

        ModalityHistoricalReportDTO.ModalityInfoDTO info = report.getModalityInfo();

        // Tabla de información básica
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{1.2f, 2f});
        table.setSpacingBefore(10);
        table.setSpacingAfter(20);

        addDetailRow(table, "Nombre de la Modalidad:", info.getModalityName());
        addDetailRow(table, "Descripción:", info.getDescription() != null ?
            info.getDescription() : "No disponible");
        addDetailRow(table, "Requiere Director:",
            info.getRequiresDirector() ? "Sí" : "No");
        addDetailRow(table, "Tipo de Modalidad:",
            info.getModalityType() != null ? info.getModalityType() : "MIXTA");
        addDetailRow(table, "Estado Actual:",
            info.getIsActive() ? "ACTIVA" : "INACTIVA");
        addDetailRow(table, "Fecha de Creación:",
            info.getCreatedAt() != null ? info.getCreatedAt().format(SIMPLE_DATE_FORMATTER) : "N/D");
        addDetailRow(table, "Años en Operación:", info.getYearsActive() + " años");
        addDetailRow(table, "Total de Instancias Históricas:",
            String.valueOf(info.getTotalHistoricalInstances()));

        document.add(table);

        // Cuadro resumen con estadísticas clave
        addStatisticsHighlight(document, report);
    }

    /**
     * Estado actual de la modalidad
     */
    private void addCurrentState(Document document, ModalityHistoricalReportDTO report)
            throws DocumentException {

        addSectionTitle(document, "2. ESTADO ACTUAL");

        if (report.getCurrentState() == null) {
            document.add(new Paragraph("No hay información del estado actual.", NORMAL_FONT));
            return;
        }

        ModalityHistoricalReportDTO.CurrentStateDTO current = report.getCurrentState();

        // Información del periodo actual
        Paragraph periodInfo = new Paragraph(
            "Periodo Actual: " + current.getCurrentPeriodYear() + "-" +
            current.getCurrentPeriodSemester(),
            SUBHEADER_FONT
        );
        periodInfo.setSpacingAfter(15);
        document.add(periodInfo);

        // Tabla de métricas actuales
        PdfPTable metricsTable = createMetricsTable();

        addMetricCell(metricsTable, "Instancias Activas",
            String.valueOf(current.getActiveInstances()), INFO_COLOR);
        addMetricCell(metricsTable, "Estudiantes Inscritos",
            String.valueOf(current.getTotalStudentsEnrolled()), SUCCESS_COLOR);
        addMetricCell(metricsTable, "Directores Asignados",
            String.valueOf(current.getAssignedDirectors()), SECONDARY_COLOR);
        addMetricCell(metricsTable, "Popularidad Actual",
            translatePopularity(current.getCurrentPopularity()),
            getPopularityColor(current.getCurrentPopularity()));

        document.add(metricsTable);

        // Distribución por estado
        document.add(new Paragraph("\n"));
        addSubsectionTitle(document, "Distribución por Estado");

        PdfPTable statusTable = new PdfPTable(2);
        statusTable.setWidthPercentage(80);
        statusTable.setWidths(new float[]{2f, 1f});
        statusTable.setSpacingBefore(10);
        statusTable.setSpacingAfter(15);
        statusTable.setHorizontalAlignment(Element.ALIGN_CENTER);

        addStatusRow(statusTable, "Completadas:", current.getCompletedInstances(), SUCCESS_COLOR);
        addStatusRow(statusTable, "En Progreso:", current.getInProgressInstances(), INFO_COLOR);
        addStatusRow(statusTable, "En Revisión:", current.getInReviewInstances(), WARNING_COLOR);

        document.add(statusTable);

        // Estadísticas adicionales
        if (current.getAverageCompletionDays() != null && current.getAverageCompletionDays() > 0) {
            addHighlightBox(document,
                "Tiempo Promedio de Completitud",
                Math.round(current.getAverageCompletionDays()) + " días",
                LIGHT_BLUE);
        }

        if (current.getPositionInRanking() != null) {
            addHighlightBox(document,
                "Posición en Ranking del Programa",
                "#" + current.getPositionInRanking(),
                LIGHT_BLUE);
        }
    }

    /**
     * Análisis histórico por periodos
     */
    private void addHistoricalAnalysis(Document document, ModalityHistoricalReportDTO report)
            throws DocumentException {

        addSectionTitle(document, "3. ANÁLISIS HISTÓRICO POR PERIODOS ACADÉMICOS");

        if (report.getHistoricalAnalysis() == null || report.getHistoricalAnalysis().isEmpty()) {
            document.add(new Paragraph("No hay datos históricos disponibles.", NORMAL_FONT));
            return;
        }

        List<ModalityHistoricalReportDTO.AcademicPeriodAnalysisDTO> periods =
            report.getHistoricalAnalysis();

        // Gráfico visual de evolución
        addEvolutionChart(document, periods);

        // Tabla detallada por periodo
        document.add(new Paragraph("\n"));
        addSubsectionTitle(document, "Detalle por Periodo");

        for (ModalityHistoricalReportDTO.AcademicPeriodAnalysisDTO period : periods) {
            addPeriodDetail(document, period);
            document.add(new Paragraph("\n"));
        }
    }

    /**
     * Detalle de un periodo académico
     */
    private void addPeriodDetail(Document document,
                                  ModalityHistoricalReportDTO.AcademicPeriodAnalysisDTO period)
            throws DocumentException {

        // Encabezado del periodo
        PdfPTable periodHeader = new PdfPTable(1);
        periodHeader.setWidthPercentage(100);
        periodHeader.setSpacingBefore(5);

        PdfPCell headerCell = new PdfPCell();
        headerCell.setBackgroundColor(MEDIUM_GRAY);
        headerCell.setPadding(8);
        headerCell.setBorder(Rectangle.NO_BORDER);

        Paragraph headerText = new Paragraph(
            "Periodo " + period.getPeriodLabel() +
            " (" + period.getTotalInstances() + " instancias)",
            BOLD_FONT
        );
        headerCell.addElement(headerText);
        periodHeader.addCell(headerCell);
        document.add(periodHeader);

        // Tabla de datos del periodo
        PdfPTable dataTable = new PdfPTable(4);
        dataTable.setWidthPercentage(100);
        dataTable.setWidths(new float[]{1.5f, 1f, 1.5f, 1f});
        dataTable.setSpacingAfter(5);

        // Fila 1
        addDataCell(dataTable, "Estudiantes:", String.valueOf(period.getStudentsEnrolled()));
        addDataCell(dataTable, "Individuales:", String.valueOf(period.getIndividualInstances()));

        // Fila 2
        addDataCell(dataTable, "Completadas:", String.valueOf(period.getCompletedSuccessfully()));
        addDataCell(dataTable, "Abandonadas:", String.valueOf(period.getAbandoned()));

        // Fila 3
        addDataCell(dataTable, "Tasa Completitud:",
            String.format("%.1f%%", period.getCompletionRate()));
        addDataCell(dataTable, "Días Promedio:",
            period.getAverageCompletionDays() != null ?
                String.format("%.0f", period.getAverageCompletionDays()) : "N/D");

        // Fila 4
        addDataCell(dataTable, "Directores:", String.valueOf(period.getDirectorsInvolved()));
        addDataCell(dataTable, "Grupales:", String.valueOf(period.getGroupInstances()));

        document.add(dataTable);

        // Observaciones
        if (period.getObservations() != null && !period.getObservations().isEmpty()) {
            Paragraph obs = new Paragraph(
                "Observaciones: " + period.getObservations(),
                SMALL_FONT
            );
            obs.setIndentationLeft(10);
            obs.setSpacingAfter(5);
            document.add(obs);
        }
    }

    /**
     * Gráfico visual de evolución
     */
    private void addEvolutionChart(Document document,
                                   List<ModalityHistoricalReportDTO.AcademicPeriodAnalysisDTO> periods)
            throws DocumentException {

        addSubsectionTitle(document, "Evolución Temporal");

        // Crear tabla para gráfico visual
        PdfPTable chartTable = new PdfPTable(1);
        chartTable.setWidthPercentage(100);
        chartTable.setSpacingBefore(10);
        chartTable.setSpacingAfter(15);

        // Encontrar valor máximo para escalar
        int maxValue = periods.stream()
            .mapToInt(ModalityHistoricalReportDTO.AcademicPeriodAnalysisDTO::getTotalInstances)
            .max()
            .orElse(1);

        // Crear barra para cada periodo
        for (ModalityHistoricalReportDTO.AcademicPeriodAnalysisDTO period : periods) {
            PdfPCell cell = new PdfPCell();
            cell.setPadding(5);
            cell.setBorder(Rectangle.NO_BORDER);

            // Tabla interna para la barra
            PdfPTable barTable = new PdfPTable(2);
            barTable.setWidthPercentage(100);

            try {
                barTable.setWidths(new float[]{0.8f, 4f});
            } catch (DocumentException e) {
                // Continuar sin ajustar widths
            }

            // Etiqueta del periodo
            PdfPCell labelCell = new PdfPCell(new Phrase(period.getPeriodLabel(), SMALL_FONT));
            labelCell.setBorder(Rectangle.NO_BORDER);
            labelCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            labelCell.setPadding(3);
            barTable.addCell(labelCell);

            // Barra visual
            float percentage = maxValue > 0 ? (float) period.getTotalInstances() / maxValue : 0;
            PdfPCell barCell = createBarCell(period.getTotalInstances(), percentage);
            barTable.addCell(barCell);

            cell.addElement(barTable);
            chartTable.addCell(cell);
        }

        document.add(chartTable);
    }

    /**
     * Crear celda de barra visual
     */
    private PdfPCell createBarCell(int value, float percentage) {
        PdfPTable barContainer = new PdfPTable(2);
        try {
            barContainer.setWidths(new float[]{percentage * 100, (1 - percentage) * 100 + 0.01f});
        } catch (DocumentException e) {
            // Continuar sin ajustar widths
        }
        barContainer.setWidthPercentage(100);

        // Parte coloreada
        PdfPCell filledCell = new PdfPCell(new Phrase(String.valueOf(value),
            FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8, BaseColor.WHITE)));
        filledCell.setBackgroundColor(INFO_COLOR);
        filledCell.setBorder(Rectangle.NO_BORDER);
        filledCell.setPadding(3);
        filledCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        barContainer.addCell(filledCell);

        // Parte vacía
        PdfPCell emptyCell = new PdfPCell();
        emptyCell.setBackgroundColor(LIGHT_GRAY);
        emptyCell.setBorder(Rectangle.NO_BORDER);
        barContainer.addCell(emptyCell);

        PdfPCell containerCell = new PdfPCell();
        containerCell.addElement(barContainer);
        containerCell.setBorder(Rectangle.NO_BORDER);
        containerCell.setPadding(0);

        return containerCell;
    }

    /**
     * Análisis de tendencias
     */
    private void addTrendsAnalysis(Document document, ModalityHistoricalReportDTO report)
            throws DocumentException {

        addSectionTitle(document, "4. ANÁLISIS DE TENDENCIAS Y EVOLUCIÓN");

        if (report.getTrendsEvolution() == null) {
            document.add(new Paragraph("No hay datos de tendencias disponibles.", NORMAL_FONT));
            return;
        }

        ModalityHistoricalReportDTO.TrendsEvolutionDTO trends = report.getTrendsEvolution();

        // Tendencia general
        PdfPTable trendTable = new PdfPTable(1);
        trendTable.setWidthPercentage(100);
        trendTable.setSpacingBefore(10);
        trendTable.setSpacingAfter(20);

        PdfPCell trendCell = new PdfPCell();
        trendCell.setBackgroundColor(getTrendColor(trends.getOverallTrend()));
        trendCell.setPadding(15);
        trendCell.setBorder(Rectangle.NO_BORDER);

        Paragraph trendText = new Paragraph();
        trendText.add(new Chunk("TENDENCIA GENERAL: ",
            FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, BaseColor.WHITE)));
        trendText.add(new Chunk(translateTrend(trends.getOverallTrend()),
            FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, BaseColor.WHITE)));

        trendText.setAlignment(Element.ALIGN_CENTER);
        trendCell.addElement(trendText);
        trendTable.addCell(trendCell);
        document.add(trendTable);

        // Estadísticas de tendencia
        if (trends.getGrowthRate() != null) {
            addHighlightBox(document,
                "Tasa de Crecimiento",
                String.format("%.1f%%", trends.getGrowthRate()),
                trends.getGrowthRate() >= 0 ? LIGHT_BLUE : new BaseColor(255, 230, 230));
        }

        // Picos y valles
        document.add(new Paragraph("\n"));
        addSubsectionTitle(document, "Picos y Valles Históricos");

        PdfPTable peaksTable = new PdfPTable(2);
        peaksTable.setWidthPercentage(90);
        peaksTable.setWidths(new float[]{1f, 1f});
        peaksTable.setSpacingBefore(10);
        peaksTable.setSpacingAfter(15);
        peaksTable.setHorizontalAlignment(Element.ALIGN_CENTER);

        // Pico
        if (trends.getPeakYear() != null && trends.getPeakSemester() != null) {
            PdfPCell peakCell = new PdfPCell();
            peakCell.setBackgroundColor(SUCCESS_COLOR);
            peakCell.setPadding(10);
            peakCell.setBorder(Rectangle.NO_BORDER);

            Paragraph peakContent = new Paragraph();
            peakContent.add(new Chunk("PICO MÁXIMO\n",
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, BaseColor.WHITE)));
            peakContent.add(new Chunk(trends.getPeakYear() + "-" + trends.getPeakSemester() + "\n",
                FontFactory.getFont(FontFactory.HELVETICA, 10, BaseColor.WHITE)));
            peakContent.add(new Chunk(trends.getPeakInstances() + " instancias",
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.WHITE)));
            peakContent.setAlignment(Element.ALIGN_CENTER);

            peakCell.addElement(peakContent);
            peaksTable.addCell(peakCell);
        }

        // Valle
        if (trends.getLowestYear() != null && trends.getLowestSemester() != null) {
            PdfPCell valleyCell = new PdfPCell();
            valleyCell.setBackgroundColor(WARNING_COLOR);
            valleyCell.setPadding(10);
            valleyCell.setBorder(Rectangle.NO_BORDER);

            Paragraph valleyContent = new Paragraph();
            valleyContent.add(new Chunk("VALLE MÍNIMO\n",
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, BaseColor.WHITE)));
            valleyContent.add(new Chunk(trends.getLowestYear() + "-" + trends.getLowestSemester() + "\n",
                FontFactory.getFont(FontFactory.HELVETICA, 10, BaseColor.WHITE)));
            valleyContent.add(new Chunk(trends.getLowestInstances() + " instancias",
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.WHITE)));
            valleyContent.setAlignment(Element.ALIGN_CENTER);

            valleyCell.addElement(valleyContent);
            peaksTable.addCell(valleyCell);
        }

        document.add(peaksTable);

        // Patrones identificados
        if (trends.getIdentifiedPatterns() != null && !trends.getIdentifiedPatterns().isEmpty()) {
            document.add(new Paragraph("\n"));
            addSubsectionTitle(document, "Patrones Identificados");

            com.itextpdf.text.List patternList = new com.itextpdf.text.List(com.itextpdf.text.List.UNORDERED);
            patternList.setIndentationLeft(20);

            for (String pattern : trends.getIdentifiedPatterns()) {
                ListItem item = new ListItem(pattern, NORMAL_FONT);
                patternList.add(item);
            }

            document.add(patternList);
        }
    }

    /**
     * Análisis comparativo
     */
    private void addComparativeAnalysis(Document document, ModalityHistoricalReportDTO report)
            throws DocumentException {

        addSectionTitle(document, "5. ANÁLISIS COMPARATIVO ENTRE PERIODOS");

        if (report.getComparativeAnalysis() == null) {
            document.add(new Paragraph("No hay datos comparativos disponibles.", NORMAL_FONT));
            return;
        }

        ModalityHistoricalReportDTO.ComparativeAnalysisDTO comparative = report.getComparativeAnalysis();

        // Comparación Actual vs Anterior
        if (comparative.getCurrentVsPrevious() != null) {
            addComparisonBox(document, "Actual vs Periodo Anterior",
                comparative.getCurrentVsPrevious());
        }

        // Comparación Actual vs Mismo periodo año anterior
        if (comparative.getCurrentVsLastYear() != null) {
            addComparisonBox(document, "Actual vs Mismo Periodo Año Anterior",
                comparative.getCurrentVsLastYear());
        }

        // Comparación Mejor vs Peor
        if (comparative.getBestVsWorst() != null) {
            addComparisonBox(document, "Mejor Periodo vs Peor Periodo",
                comparative.getBestVsWorst());
        }

        // Hallazgos clave
        if (comparative.getKeyFindings() != null && !comparative.getKeyFindings().isEmpty()) {
            document.add(new Paragraph("\n"));
            addSubsectionTitle(document, "Hallazgos Clave");

            com.itextpdf.text.List findingsList = new com.itextpdf.text.List(com.itextpdf.text.List.ORDERED);
            findingsList.setIndentationLeft(20);

            for (String finding : comparative.getKeyFindings()) {
                ListItem item = new ListItem(finding, NORMAL_FONT);
                item.setSpacingAfter(5);
                findingsList.add(item);
            }

            document.add(findingsList);
        }
    }

    /**
     * Cuadro de comparación
     */
    private void addComparisonBox(Document document, String title,
                                  ModalityHistoricalReportDTO.PeriodComparisonDTO comparison)
            throws DocumentException {

        addSubsectionTitle(document, title);

        PdfPTable compTable = new PdfPTable(3);
        compTable.setWidthPercentage(100);
        compTable.setWidths(new float[]{1.5f, 1.2f, 1.2f});
        compTable.setSpacingBefore(10);
        compTable.setSpacingAfter(20);

        // Encabezado
        addCompHeaderCell(compTable, "Métrica");
        addCompHeaderCell(compTable, comparison.getPeriod1Label());
        addCompHeaderCell(compTable, comparison.getPeriod2Label());

        // Instancias
        addCompDataCell(compTable, "Instancias", false);
        addCompDataCell(compTable, String.valueOf(comparison.getPeriod1Instances()), false);
        addCompDataCell(compTable, String.valueOf(comparison.getPeriod2Instances()), false);

        // Estudiantes
        addCompDataCell(compTable, "Estudiantes", true);
        addCompDataCell(compTable, String.valueOf(comparison.getPeriod1Students()), true);
        addCompDataCell(compTable, String.valueOf(comparison.getPeriod2Students()), true);

        // Cambio en instancias
        addCompDataCell(compTable, "Cambio Instancias", false);
        PdfPCell changeCell = new PdfPCell(new Phrase(
            String.format("%+.1f%%", comparison.getInstancesChange()),
            BOLD_FONT
        ));
        changeCell.setColspan(2);
        changeCell.setPadding(8);
        changeCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        changeCell.setBackgroundColor(comparison.getInstancesChange() >= 0 ?
            new BaseColor(220, 255, 220) : new BaseColor(255, 230, 230));
        compTable.addCell(changeCell);

        // Cambio en estudiantes
        addCompDataCell(compTable, "Cambio Estudiantes", true);
        PdfPCell changeStudCell = new PdfPCell(new Phrase(
            String.format("%+.1f%%", comparison.getStudentsChange()),
            BOLD_FONT
        ));
        changeStudCell.setColspan(2);
        changeStudCell.setPadding(8);
        changeStudCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        changeStudCell.setBackgroundColor(comparison.getStudentsChange() >= 0 ?
            new BaseColor(220, 255, 220) : new BaseColor(255, 230, 230));
        compTable.addCell(changeStudCell);

        document.add(compTable);

        // Veredicto
        if (comparison.getVerdict() != null) {
            Paragraph verdict = new Paragraph(
                "Veredicto: " + translateVerdict(comparison.getVerdict()),
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11,
                    getVerdictColor(comparison.getVerdict()))
            );
            verdict.setAlignment(Element.ALIGN_CENTER);
            verdict.setSpacingAfter(10);
            document.add(verdict);
        }
    }

    /**
     * Estadísticas de directores
     */
    private void addDirectorStatistics(Document document, ModalityHistoricalReportDTO report)
            throws DocumentException {

        addSectionTitle(document, "6. ESTADÍSTICAS DE DIRECTORES");

        if (report.getDirectorStatistics() == null) {
            document.add(new Paragraph("Esta modalidad no requiere directores.", NORMAL_FONT));
            return;
        }

        ModalityHistoricalReportDTO.DirectorStatisticsDTO dirStats = report.getDirectorStatistics();

        // Resumen general
        PdfPTable summaryTable = createMetricsTable();

        addMetricCell(summaryTable, "Total Directores Únicos",
            String.valueOf(dirStats.getTotalUniqueDirectors()), PRIMARY_COLOR);
        addMetricCell(summaryTable, "Directores Actuales",
            String.valueOf(dirStats.getCurrentActiveDirectors()), SECONDARY_COLOR);
        addMetricCell(summaryTable, "Promedio Instancias/Director",
            String.format("%.1f", dirStats.getAverageInstancesPerDirector()), INFO_COLOR);
        addMetricCell(summaryTable, "Director Más Experimentado",
            dirStats.getMostExperiencedDirector() != null ?
                dirStats.getMostExperiencedDirector() : "N/D",
            ACCENT_COLOR);

        document.add(summaryTable);

        // Top directores históricos
        if (dirStats.getTopDirectorsAllTime() != null && !dirStats.getTopDirectorsAllTime().isEmpty()) {
            document.add(new Paragraph("\n"));
            addSubsectionTitle(document, "Top Directores de Todos los Tiempos");

            addTopDirectorsTable(document, dirStats.getTopDirectorsAllTime());
        }

        // Top directores periodo actual
        if (dirStats.getTopDirectorsCurrentPeriod() != null &&
            !dirStats.getTopDirectorsCurrentPeriod().isEmpty()) {
            document.add(new Paragraph("\n"));
            addSubsectionTitle(document, "Top Directores del Periodo Actual");

            addTopDirectorsTable(document, dirStats.getTopDirectorsCurrentPeriod());
        }
    }

    /**
     * Tabla de top directores
     */
    private void addTopDirectorsTable(Document document,
                                     List<ModalityHistoricalReportDTO.TopDirectorDTO> directors)
            throws DocumentException {

        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{3f, 1.5f, 1.5f, 1.5f});
        table.setSpacingBefore(10);
        table.setSpacingAfter(15);

        // Encabezados
        addTableHeaderCell(table, "Director");
        addTableHeaderCell(table, "Instancias");
        addTableHeaderCell(table, "Estudiantes");
        addTableHeaderCell(table, "Tasa Éxito");

        // Datos
        for (ModalityHistoricalReportDTO.TopDirectorDTO director : directors) {
            addTableDataCell(table, director.getDirectorName(), false);
            addTableDataCell(table, String.valueOf(director.getInstancesSupervised()), false);
            addTableDataCell(table, String.valueOf(director.getStudentsSupervised()), false);
            addTableDataCell(table,
                director.getSuccessRate() != null ?
                    String.format("%.1f%%", director.getSuccessRate()) : "N/D",
                false);
        }

        document.add(table);
    }

    /**
     * Estadísticas de estudiantes
     */
    private void addStudentStatistics(Document document, ModalityHistoricalReportDTO report)
            throws DocumentException {

        addSectionTitle(document, "7. ESTADÍSTICAS DE ESTUDIANTES");

        if (report.getStudentStatistics() == null) {
            document.add(new Paragraph("No hay datos de estudiantes disponibles.", NORMAL_FONT));
            return;
        }

        ModalityHistoricalReportDTO.StudentStatisticsDTO studStats = report.getStudentStatistics();

        // Métricas principales
        PdfPTable metricsTable = createMetricsTable();

        addMetricCell(metricsTable, "Total Estudiantes Históricos",
            String.valueOf(studStats.getTotalHistoricalStudents()), PRIMARY_COLOR);
        addMetricCell(metricsTable, "Estudiantes Actuales",
            String.valueOf(studStats.getCurrentStudents()), SECONDARY_COLOR);
        addMetricCell(metricsTable, "Promedio Est./Instancia",
            String.format("%.1f", studStats.getAverageStudentsPerInstance()), INFO_COLOR);
        addMetricCell(metricsTable, "Tipo Preferido",
            studStats.getPreferredType() != null ? studStats.getPreferredType() : "MIXTO",
            ACCENT_COLOR);

        document.add(metricsTable);

        // Estadísticas de grupos
        if (studStats.getMaxStudentsInGroup() != null && studStats.getMaxStudentsInGroup() > 1) {
            document.add(new Paragraph("\n"));
            addSubsectionTitle(document, "Estadísticas de Grupos");

            PdfPTable groupTable = new PdfPTable(2);
            groupTable.setWidthPercentage(70);
            groupTable.setHorizontalAlignment(Element.ALIGN_CENTER);
            groupTable.setSpacingBefore(10);
            groupTable.setSpacingAfter(15);

            addDetailRow(groupTable, "Máximo Estudiantes por Grupo:",
                String.valueOf(studStats.getMaxStudentsInGroup()));
            addDetailRow(groupTable, "Mínimo Estudiantes por Grupo:",
                String.valueOf(studStats.getMinStudentsInGroup()));
            addDetailRow(groupTable, "Relación Individual/Grupal:",
                studStats.getIndividualVsGroupRatio() != null ?
                    String.format("%.2f", studStats.getIndividualVsGroupRatio()) : "N/D");

            document.add(groupTable);
        }

        // Distribución por semestre (si está disponible)
        if (studStats.getStudentsBySemester() != null && !studStats.getStudentsBySemester().isEmpty()) {
            document.add(new Paragraph("\n"));
            addSubsectionTitle(document, "Distribución de Estudiantes por Semestre");

            // Mostrar top 5 semestres con más estudiantes
            studStats.getStudentsBySemester().entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(5)
                .forEach(entry -> {
                    try {
                        Paragraph p = new Paragraph(
                            entry.getKey() + ": " + entry.getValue() + " estudiantes",
                            NORMAL_FONT
                        );
                        p.setIndentationLeft(20);
                        p.setSpacingAfter(3);
                        document.add(p);
                    } catch (DocumentException e) {
                        // Ignorar errores individuales
                    }
                });
        }
    }

    /**
     * Análisis de desempeño
     */
    private void addPerformanceAnalysis(Document document, ModalityHistoricalReportDTO report)
            throws DocumentException {

        addSectionTitle(document, "8. ANÁLISIS DE DESEMPEÑO");

        if (report.getPerformanceAnalysis() == null) {
            document.add(new Paragraph("No hay datos de desempeño disponibles.", NORMAL_FONT));
            return;
        }

        ModalityHistoricalReportDTO.PerformanceAnalysisDTO perf = report.getPerformanceAnalysis();

        // Veredicto general de desempeño
        PdfPTable verdictTable = new PdfPTable(1);
        verdictTable.setWidthPercentage(100);
        verdictTable.setSpacingBefore(10);
        verdictTable.setSpacingAfter(20);

        PdfPCell verdictCell = new PdfPCell();
        verdictCell.setBackgroundColor(getPerformanceColor(perf.getPerformanceVerdict()));
        verdictCell.setPadding(15);
        verdictCell.setBorder(Rectangle.NO_BORDER);

        Paragraph verdictText = new Paragraph();
        verdictText.add(new Chunk("CALIFICACIÓN DE DESEMPEÑO: ",
            FontFactory.getFont(FontFactory.HELVETICA_BOLD, 13, BaseColor.WHITE)));
        verdictText.add(new Chunk(translatePerformanceVerdict(perf.getPerformanceVerdict()),
            FontFactory.getFont(FontFactory.HELVETICA_BOLD, 15, BaseColor.WHITE)));
        verdictText.setAlignment(Element.ALIGN_CENTER);

        verdictCell.addElement(verdictText);
        verdictTable.addCell(verdictCell);
        document.add(verdictTable);

        // Métricas clave
        PdfPTable metricsTable = new PdfPTable(2);
        metricsTable.setWidthPercentage(90);
        metricsTable.setWidths(new float[]{2f, 1f});
        metricsTable.setSpacingBefore(10);
        metricsTable.setSpacingAfter(20);
        metricsTable.setHorizontalAlignment(Element.ALIGN_CENTER);

        addPerformanceMetricRow(metricsTable, "Tasa de Completitud General:",
            String.format("%.1f%%", perf.getOverallCompletionRate()));
        addPerformanceMetricRow(metricsTable, "Tasa de Éxito:",
            String.format("%.1f%%", perf.getSuccessRate()));
        addPerformanceMetricRow(metricsTable, "Tasa de Abandono:",
            String.format("%.1f%%", perf.getAbandonmentRate()));
        addPerformanceMetricRow(metricsTable, "Tiempo Promedio Completitud:",
            String.format("%.0f días", perf.getAverageCompletionTimeDays()));

        if (perf.getFastestCompletionDays() != null && perf.getFastestCompletionDays() > 0) {
            addPerformanceMetricRow(metricsTable, "Completitud Más Rápida:",
                perf.getFastestCompletionDays() + " días");
        }

        if (perf.getSlowestCompletionDays() != null && perf.getSlowestCompletionDays() > 0) {
            addPerformanceMetricRow(metricsTable, "Completitud Más Lenta:",
                perf.getSlowestCompletionDays() + " días");
        }

        document.add(metricsTable);

        // Fortalezas
        if (perf.getStrengthPoints() != null && !perf.getStrengthPoints().isEmpty()) {
            document.add(new Paragraph("\n"));
            addSubsectionTitle(document, "Fortalezas Identificadas");

            com.itextpdf.text.List strengthList = new com.itextpdf.text.List(com.itextpdf.text.List.UNORDERED);
            strengthList.setIndentationLeft(20);
            strengthList.setListSymbol(new Chunk("✓ ",
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, SUCCESS_COLOR)));

            for (String strength : perf.getStrengthPoints()) {
                ListItem item = new ListItem(strength, NORMAL_FONT);
                item.setSpacingAfter(5);
                strengthList.add(item);
            }

            document.add(strengthList);
        }

        // Áreas de mejora
        if (perf.getImprovementAreas() != null && !perf.getImprovementAreas().isEmpty()) {
            document.add(new Paragraph("\n"));
            addSubsectionTitle(document, "Áreas de Mejora");

            com.itextpdf.text.List improvementList = new com.itextpdf.text.List(com.itextpdf.text.List.UNORDERED);
            improvementList.setIndentationLeft(20);
            improvementList.setListSymbol(new Chunk("! ",
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, WARNING_COLOR)));

            for (String improvement : perf.getImprovementAreas()) {
                ListItem item = new ListItem(improvement, NORMAL_FONT);
                item.setSpacingAfter(5);
                improvementList.add(item);
            }

            document.add(improvementList);
        }
    }

    /**
     * Proyecciones y recomendaciones
     */
    private void addProjectionsAndRecommendations(Document document, ModalityHistoricalReportDTO report)
            throws DocumentException {

        addSectionTitle(document, "9. PROYECCIONES Y RECOMENDACIONES");

        if (report.getProjections() == null) {
            document.add(new Paragraph("No hay proyecciones disponibles.", NORMAL_FONT));
            return;
        }

        ModalityHistoricalReportDTO.ProjectionsDTO proj = report.getProjections();

        // Proyecciones numéricas
        addSubsectionTitle(document, "Proyecciones de Demanda");

        PdfPTable projTable = new PdfPTable(2);
        projTable.setWidthPercentage(80);
        projTable.setWidths(new float[]{2f, 1f});
        projTable.setSpacingBefore(10);
        projTable.setSpacingAfter(15);
        projTable.setHorizontalAlignment(Element.ALIGN_CENTER);

        addProjectionRow(projTable, "Próximo Semestre:",
            String.valueOf(proj.getProjectedNextSemester()) + " instancias");
        addProjectionRow(projTable, "Próximo Año:",
            String.valueOf(proj.getProjectedNextYear()) + " instancias");
        addProjectionRow(projTable, "Nivel de Demanda Proyectado:",
            translateDemandProjection(proj.getDemandProjection()));

        if (proj.getConfidenceLevel() != null) {
            addProjectionRow(projTable, "Nivel de Confianza:",
                String.format("%.0f%%", proj.getConfidenceLevel()));
        }

        document.add(projTable);

        // Acciones recomendadas
        if (proj.getRecommendedActions() != null && !proj.getRecommendedActions().isEmpty()) {
            document.add(new Paragraph("\n"));
            addSubsectionTitle(document, "Acciones Recomendadas");

            Paragraph actions = new Paragraph(proj.getRecommendedActions(), NORMAL_FONT);
            actions.setAlignment(Element.ALIGN_JUSTIFIED);
            actions.setIndentationLeft(20);
            actions.setIndentationRight(20);
            actions.setSpacingAfter(15);
            document.add(actions);
        }

        // Oportunidades
        if (proj.getOpportunities() != null && !proj.getOpportunities().isEmpty()) {
            document.add(new Paragraph("\n"));
            addSubsectionTitle(document, "Oportunidades Identificadas");

            com.itextpdf.text.List oppList = new com.itextpdf.text.List(com.itextpdf.text.List.UNORDERED);
            oppList.setIndentationLeft(20);
            oppList.setListSymbol(new Chunk("⚈ ",
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, SUCCESS_COLOR)));

            for (String opportunity : proj.getOpportunities()) {
                ListItem item = new ListItem(opportunity, NORMAL_FONT);
                item.setSpacingAfter(5);
                oppList.add(item);
            }

            document.add(oppList);
        }

        // Riesgos
        if (proj.getRisks() != null && !proj.getRisks().isEmpty()) {
            document.add(new Paragraph("\n"));
            addSubsectionTitle(document, "Riesgos a Considerar");

            com.itextpdf.text.List riskList = new com.itextpdf.text.List(com.itextpdf.text.List.UNORDERED);
            riskList.setIndentationLeft(20);
            riskList.setListSymbol(new Chunk("⚠ ",
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, DANGER_COLOR)));

            for (String risk : proj.getRisks()) {
                ListItem item = new ListItem(risk, NORMAL_FONT);
                item.setSpacingAfter(5);
                riskList.add(item);
            }

            document.add(riskList);
        }

        // Nota final
        document.add(new Paragraph("\n\n"));
        PdfPTable noteTable = new PdfPTable(1);
        noteTable.setWidthPercentage(100);

        PdfPCell noteCell = new PdfPCell();
        noteCell.setBackgroundColor(LIGHT_BLUE);
        noteCell.setPadding(15);
        noteCell.setBorder(Rectangle.NO_BORDER);

        Paragraph note = new Paragraph(
            "Este análisis histórico ha sido generado automáticamente por el sistema SIGMA " +
            "basándose en datos históricos recopilados. Las proyecciones son estimaciones " +
            "basadas en tendencias pasadas y deben ser interpretadas como orientativas. " +
            "Se recomienda complementar este análisis con juicio profesional y consideraciones " +
            "del contexto actual del programa académico.",
            SMALL_FONT
        );
        note.setAlignment(Element.ALIGN_JUSTIFIED);
        noteCell.addElement(note);
        noteTable.addCell(noteCell);

        document.add(noteTable);
    }

    // ==================== MÉTODOS AUXILIARES ====================

    /**
     * Cuadro de estadísticas destacadas
     */
    private void addStatisticsHighlight(Document document, ModalityHistoricalReportDTO report)
            throws DocumentException {

        addSubsectionTitle(document, "Estadísticas Clave");

        PdfPTable statsTable = new PdfPTable(3);
        statsTable.setWidthPercentage(100);
        statsTable.setSpacingBefore(10);
        statsTable.setSpacingAfter(15);

        if (report.getModalityInfo() != null) {
            addStatCell(statsTable, "Total Histórico",
                String.valueOf(report.getModalityInfo().getTotalHistoricalInstances()),
                "instancias", PRIMARY_COLOR);
        }

        if (report.getStudentStatistics() != null) {
            addStatCell(statsTable, "Total Estudiantes",
                String.valueOf(report.getStudentStatistics().getTotalHistoricalStudents()),
                "estudiantes", SECONDARY_COLOR);
        }

        if (report.getPerformanceAnalysis() != null) {
            addStatCell(statsTable, "Tasa de Éxito",
                String.format("%.1f%%", report.getPerformanceAnalysis().getSuccessRate()),
                "", SUCCESS_COLOR);
        }

        document.add(statsTable);
    }

    /**
     * Celda de estadística
     */
    private void addStatCell(PdfPTable table, String label, String value, String unit, BaseColor color) {
        PdfPCell cell = new PdfPCell();
        cell.setBackgroundColor(color);
        cell.setPadding(12);
        cell.setBorder(Rectangle.NO_BORDER);

        Paragraph content = new Paragraph();
        content.add(new Chunk(value + "\n",
            FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20, BaseColor.WHITE)));
        content.add(new Chunk(unit + "\n",
            FontFactory.getFont(FontFactory.HELVETICA, 9, new BaseColor(230, 230, 230))));
        content.add(new Chunk(label,
            FontFactory.getFont(FontFactory.HELVETICA, 10, BaseColor.WHITE)));
        content.setAlignment(Element.ALIGN_CENTER);

        cell.addElement(content);
        table.addCell(cell);
    }

    /**
     * Crear tabla de métricas (4 columnas)
     */
    private PdfPTable createMetricsTable() throws DocumentException {
        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10);
        table.setSpacingAfter(15);
        return table;
    }

    /**
     * Agregar celda de métrica
     */
    private void addMetricCell(PdfPTable table, String label, String value, BaseColor color) {
        PdfPCell cell = new PdfPCell();
        cell.setBackgroundColor(color);
        cell.setPadding(10);
        cell.setBorder(Rectangle.NO_BORDER);

        Paragraph content = new Paragraph();
        content.add(new Chunk(value + "\n",
            FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, BaseColor.WHITE)));
        content.add(new Chunk(label,
            FontFactory.getFont(FontFactory.HELVETICA, 9, new BaseColor(240, 240, 240))));
        content.setAlignment(Element.ALIGN_CENTER);

        cell.addElement(content);
        table.addCell(cell);
    }

    /**
     * Agregar fila de información
     */
    private void addInfoRow(PdfPTable table, String label, String value) {
        PdfPCell labelCell = new PdfPCell(new Phrase(label, BOLD_FONT));
        labelCell.setPadding(8);
        labelCell.setBackgroundColor(LIGHT_GRAY);
        labelCell.setBorder(Rectangle.NO_BORDER);
        table.addCell(labelCell);

        PdfPCell valueCell = new PdfPCell(new Phrase(value, NORMAL_FONT));
        valueCell.setPadding(8);
        valueCell.setBackgroundColor(BaseColor.WHITE);
        valueCell.setBorder(Rectangle.NO_BORDER);
        table.addCell(valueCell);
    }

    /**
     * Agregar fila de detalle
     */
    private void addDetailRow(PdfPTable table, String label, String value) {
        PdfPCell labelCell = new PdfPCell(new Phrase(label, BOLD_FONT));
        labelCell.setPadding(8);
        labelCell.setBorder(Rectangle.BOTTOM);
        labelCell.setBorderColor(MEDIUM_GRAY);
        labelCell.setBorderWidth(0.5f);
        table.addCell(labelCell);

        PdfPCell valueCell = new PdfPCell(new Phrase(value, NORMAL_FONT));
        valueCell.setPadding(8);
        valueCell.setBorder(Rectangle.BOTTOM);
        valueCell.setBorderColor(MEDIUM_GRAY);
        valueCell.setBorderWidth(0.5f);
        table.addCell(valueCell);
    }

    /**
     * Agregar fila de estado
     */
    private void addStatusRow(PdfPTable table, String label, Integer value, BaseColor color) {
        PdfPCell labelCell = new PdfPCell(new Phrase(label, NORMAL_FONT));
        labelCell.setPadding(8);
        labelCell.setBorder(Rectangle.NO_BORDER);
        table.addCell(labelCell);

        PdfPCell valueCell = new PdfPCell(new Phrase(String.valueOf(value), BOLD_FONT));
        valueCell.setPadding(8);
        valueCell.setBackgroundColor(color);
        valueCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        valueCell.setBorder(Rectangle.NO_BORDER);
        table.addCell(valueCell);
    }

    /**
     * Cuadro de resaltado
     */
    private void addHighlightBox(Document document, String title, String value, BaseColor bgColor)
            throws DocumentException {

        PdfPTable table = new PdfPTable(1);
        table.setWidthPercentage(70);
        table.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.setSpacingBefore(10);
        table.setSpacingAfter(10);

        PdfPCell cell = new PdfPCell();
        cell.setBackgroundColor(bgColor);
        cell.setPadding(12);
        cell.setBorder(Rectangle.NO_BORDER);

        Paragraph content = new Paragraph();
        content.add(new Chunk(title + "\n", SUBHEADER_FONT));
        content.add(new Chunk(value,
            FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, PRIMARY_COLOR)));
        content.setAlignment(Element.ALIGN_CENTER);

        cell.addElement(content);
        table.addCell(cell);
        document.add(table);
    }

    /**
     * Agregar título de sección
     */
    private void addSectionTitle(Document document, String title) throws DocumentException {
        Paragraph section = new Paragraph(title, HEADER_FONT);
        section.setSpacingBefore(15);
        section.setSpacingAfter(10);

        LineSeparator line = new LineSeparator();
        line.setLineColor(PRIMARY_COLOR);
        line.setLineWidth(2);

        document.add(section);
        document.add(new Chunk(line));
        document.add(new Paragraph("\n"));
    }

    /**
     * Agregar título de subsección
     */
    private void addSubsectionTitle(Document document, String title) throws DocumentException {
        Paragraph subsection = new Paragraph(title, SUBHEADER_FONT);
        subsection.setSpacingBefore(10);
        subsection.setSpacingAfter(8);
        document.add(subsection);
    }

    /**
     * Agregar celda de datos
     */
    private void addDataCell(PdfPTable table, String label, String value) {
        // Label
        PdfPCell labelCell = new PdfPCell(new Phrase(label, SMALL_FONT));
        labelCell.setPadding(5);
        labelCell.setBorder(Rectangle.NO_BORDER);
        labelCell.setBackgroundColor(LIGHT_GRAY);
        table.addCell(labelCell);

        // Value
        PdfPCell valueCell = new PdfPCell(new Phrase(value, BOLD_FONT));
        valueCell.setPadding(5);
        valueCell.setBorder(Rectangle.NO_BORDER);
        valueCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(valueCell);
    }

    /**
     * Agregar encabezado de comparación
     */
    private void addCompHeaderCell(PdfPTable table, String text) {
        PdfPCell cell = new PdfPCell(new Phrase(text, BOLD_FONT));
        cell.setPadding(10);
        cell.setBackgroundColor(PRIMARY_COLOR);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setBorder(Rectangle.NO_BORDER);

        Paragraph p = new Paragraph(text,
            FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, BaseColor.WHITE));
        p.setAlignment(Element.ALIGN_CENTER);
        cell.setPhrase(new Phrase(p));

        table.addCell(cell);
    }

    /**
     * Agregar celda de datos de comparación
     */
    private void addCompDataCell(PdfPTable table, String text, boolean alternate) {
        PdfPCell cell = new PdfPCell(new Phrase(text, NORMAL_FONT));
        cell.setPadding(8);
        cell.setBackgroundColor(alternate ? LIGHT_GRAY : BaseColor.WHITE);
        cell.setBorder(Rectangle.NO_BORDER);
        table.addCell(cell);
    }

    /**
     * Agregar encabezado de tabla
     */
    private void addTableHeaderCell(PdfPTable table, String text) {
        PdfPCell cell = new PdfPCell(new Phrase(text,
            FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, BaseColor.WHITE)));
        cell.setPadding(8);
        cell.setBackgroundColor(SECONDARY_COLOR);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setBorder(Rectangle.NO_BORDER);
        table.addCell(cell);
    }

    /**
     * Agregar celda de datos de tabla
     */
    private void addTableDataCell(PdfPTable table, String text, boolean alternate) {
        PdfPCell cell = new PdfPCell(new Phrase(text, SMALL_FONT));
        cell.setPadding(6);
        cell.setBackgroundColor(alternate ? LIGHT_GRAY : BaseColor.WHITE);
        cell.setBorder(Rectangle.BOTTOM);
        cell.setBorderColor(MEDIUM_GRAY);
        cell.setBorderWidth(0.5f);
        table.addCell(cell);
    }

    /**
     * Agregar fila de métrica de desempeño
     */
    private void addPerformanceMetricRow(PdfPTable table, String label, String value) {
        PdfPCell labelCell = new PdfPCell(new Phrase(label, NORMAL_FONT));
        labelCell.setPadding(8);
        labelCell.setBorder(Rectangle.BOTTOM);
        labelCell.setBorderColor(MEDIUM_GRAY);
        labelCell.setBorderWidth(0.5f);
        table.addCell(labelCell);

        PdfPCell valueCell = new PdfPCell(new Phrase(value, BOLD_FONT));
        valueCell.setPadding(8);
        valueCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        valueCell.setBorder(Rectangle.BOTTOM);
        valueCell.setBorderColor(MEDIUM_GRAY);
        valueCell.setBorderWidth(0.5f);
        table.addCell(valueCell);
    }

    /**
     * Agregar fila de proyección
     */
    private void addProjectionRow(PdfPTable table, String label, String value) {
        PdfPCell labelCell = new PdfPCell(new Phrase(label, BOLD_FONT));
        labelCell.setPadding(10);
        labelCell.setBackgroundColor(LIGHT_GRAY);
        labelCell.setBorder(Rectangle.NO_BORDER);
        table.addCell(labelCell);

        PdfPCell valueCell = new PdfPCell(new Phrase(value, NORMAL_FONT));
        valueCell.setPadding(10);
        valueCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        valueCell.setBackgroundColor(BaseColor.WHITE);
        valueCell.setBorder(Rectangle.NO_BORDER);
        table.addCell(valueCell);
    }

    // ==================== MÉTODOS DE TRADUCCIÓN ====================

    private String translatePopularity(String popularity) {
        if (popularity == null) return "No Disponible";
        switch (popularity) {
            case "HIGH": return "Alta";
            case "MEDIUM": return "Media";
            case "LOW": return "Baja";
            default: return popularity;
        }
    }

    private BaseColor getPopularityColor(String popularity) {
        if (popularity == null) return MEDIUM_GRAY;
        switch (popularity) {
            case "HIGH": return SUCCESS_COLOR;
            case "MEDIUM": return WARNING_COLOR;
            case "LOW": return DANGER_COLOR;
            default: return MEDIUM_GRAY;
        }
    }

    private String translateTrend(String trend) {
        if (trend == null) return "Sin Datos";
        switch (trend) {
            case "GROWING": return "EN CRECIMIENTO";
            case "STABLE": return "ESTABLE";
            case "DECLINING": return "EN DECLIVE";
            case "INSUFFICIENT_DATA": return "DATOS INSUFICIENTES";
            default: return trend;
        }
    }

    private BaseColor getTrendColor(String trend) {
        if (trend == null) return MEDIUM_GRAY;
        switch (trend) {
            case "GROWING": return SUCCESS_COLOR;
            case "STABLE": return INFO_COLOR;
            case "DECLINING": return DANGER_COLOR;
            default: return MEDIUM_GRAY;
        }
    }

    private String translateVerdict(String verdict) {
        if (verdict == null) return "Sin Evaluar";
        switch (verdict) {
            case "IMPROVED": return "Mejorado";
            case "DECLINED": return "Disminuido";
            case "STABLE": return "Estable";
            default: return verdict;
        }
    }

    private BaseColor getVerdictColor(String verdict) {
        if (verdict == null) return TEXT_DARK;
        switch (verdict) {
            case "IMPROVED": return SUCCESS_COLOR;
            case "DECLINED": return DANGER_COLOR;
            case "STABLE": return INFO_COLOR;
            default: return TEXT_DARK;
        }
    }

    private String translatePerformanceVerdict(String verdict) {
        if (verdict == null) return "Sin Evaluar";
        switch (verdict) {
            case "EXCELLENT": return "EXCELENTE";
            case "GOOD": return "BUENO";
            case "REGULAR": return "REGULAR";
            case "NEEDS_IMPROVEMENT": return "NECESITA MEJORA";
            default: return verdict;
        }
    }

    private BaseColor getPerformanceColor(String verdict) {
        if (verdict == null) return MEDIUM_GRAY;
        switch (verdict) {
            case "EXCELLENT": return SUCCESS_COLOR;
            case "GOOD": return INFO_COLOR;
            case "REGULAR": return WARNING_COLOR;
            case "NEEDS_IMPROVEMENT": return DANGER_COLOR;
            default: return MEDIUM_GRAY;
        }
    }

    private String translateDemandProjection(String demand) {
        if (demand == null) return "No Disponible";
        switch (demand) {
            case "HIGH": return "Alta Demanda";
            case "MEDIUM": return "Demanda Media";
            case "LOW": return "Baja Demanda";
            default: return demand;
        }
    }

    // ==================== CLASE INTERNA: PAGE EVENT HELPER ====================

    /**
     * Helper para eventos de página (encabezado y pie de página)
     */
    private static class HistoricalPageEventHelper extends PdfPageEventHelper {

        private final ModalityHistoricalReportDTO report;
        private final Font headerFont = FontFactory.getFont(FontFactory.HELVETICA, 8, BaseColor.GRAY);
        private final Font footerFont = FontFactory.getFont(FontFactory.HELVETICA, 8, BaseColor.GRAY);

        public HistoricalPageEventHelper(ModalityHistoricalReportDTO report) {
            this.report = report;
        }

        @Override
        public void onEndPage(PdfWriter writer, Document document) {
            PdfContentByte cb = writer.getDirectContent();

            // Encabezado
            if (writer.getPageNumber() > 1) {
                Phrase header = new Phrase("Análisis Histórico - " +
                    (report.getModalityInfo() != null ? report.getModalityInfo().getModalityName() : ""),
                    headerFont);
                ColumnText.showTextAligned(cb, Element.ALIGN_LEFT, header,
                        document.left(), document.top() + 20, 0);
            }

            // Pie de página
            Phrase footer = new Phrase(
                "Página " + writer.getPageNumber() + " | Generado: " +
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
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


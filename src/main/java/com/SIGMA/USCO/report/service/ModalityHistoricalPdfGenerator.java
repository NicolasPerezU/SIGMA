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

    // COLORES INSTITUCIONALES - USO EXCLUSIVO
    private static final BaseColor INSTITUTIONAL_RED = new BaseColor(143, 30, 30); // #8F1E1E - Color primario
    private static final BaseColor INSTITUTIONAL_GOLD = new BaseColor(213, 203, 160); // #D5CBA0 - Color secundario
    private static final BaseColor WHITE = BaseColor.WHITE; // Color primario
    private static final BaseColor LIGHT_GOLD = new BaseColor(245, 242, 235); // Tono muy claro de dorado para fondos sutiles
    private static final BaseColor TEXT_BLACK = BaseColor.BLACK; // Texto principal
    private static final BaseColor TEXT_GRAY = new BaseColor(80, 80, 80); // Texto secundario

    // Fuentes con colores institucionales
    private static final Font TITLE_FONT = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 22, INSTITUTIONAL_RED);
    private static final Font SUBTITLE_FONT = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, INSTITUTIONAL_RED);
    private static final Font HEADER_FONT = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 15, INSTITUTIONAL_RED);
    private static final Font SUBHEADER_FONT = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, INSTITUTIONAL_RED);
    private static final Font BOLD_FONT = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, TEXT_BLACK);
    private static final Font NORMAL_FONT = FontFactory.getFont(FontFactory.HELVETICA, 10, TEXT_BLACK);
    private static final Font SMALL_FONT = FontFactory.getFont(FontFactory.HELVETICA, 9, TEXT_GRAY);
    private static final Font TINY_FONT = FontFactory.getFont(FontFactory.HELVETICA, 8, TEXT_GRAY);
    private static final Font HEADER_TABLE_FONT = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, WHITE);

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
        Paragraph title = new Paragraph("ANÁLISIS HISTÓRICO DE MODALIDAD", TITLE_FONT);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(20);
        document.add(title);

        // Nombre de la modalidad analizada
        if (report.getModalityInfo() != null) {
            Paragraph modalityName = new Paragraph(
                report.getModalityInfo().getModalityName().toUpperCase(),
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, INSTITUTIONAL_GOLD)
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
        line.setLineColor(INSTITUTIONAL_GOLD);
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
            String.valueOf(current.getActiveInstances()), INSTITUTIONAL_GOLD);
        addMetricCell(metricsTable, "Estudiantes Inscritos",
            String.valueOf(current.getTotalStudentsEnrolled()), INSTITUTIONAL_GOLD);
        addMetricCell(metricsTable, "Directores Asignados",
            String.valueOf(current.getAssignedDirectors()), INSTITUTIONAL_GOLD);
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

        addStatusRow(statusTable, "Completadas:", current.getCompletedInstances(), INSTITUTIONAL_GOLD);
        addStatusRow(statusTable, "En Progreso:", current.getInProgressInstances(), INSTITUTIONAL_GOLD);
        addStatusRow(statusTable, "En Revisión:", current.getInReviewInstances(), INSTITUTIONAL_RED);

        document.add(statusTable);

        // Estadísticas adicionales
        if (current.getAverageCompletionDays() != null && current.getAverageCompletionDays() > 0) {
            addHighlightBox(document,
                "Tiempo Promedio de Completitud",
                Math.round(current.getAverageCompletionDays()) + " días",
                LIGHT_GOLD);
        }

        if (current.getPositionInRanking() != null) {
            addHighlightBox(document,
                "Posición en Ranking del Programa",
                "#" + current.getPositionInRanking(),
                LIGHT_GOLD);
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

        // NUEVO: Resumen estadístico visual antes del gráfico
        addHistoricalSummaryCards(document, periods);

        // NUEVO: Gráfico visual de evolución mejorado
        addEnhancedEvolutionChart(document, periods);

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
        headerCell.setBackgroundColor(LIGHT_GOLD);
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

        // Asegurar que los widths sean válidos (entre 0.1 y 99.9)
        float filledWidth = Math.max(Math.min(percentage * 100, 99.9f), 0.1f);
        float emptyWidth = Math.max(100 - filledWidth, 0.1f);

        try {
            barContainer.setWidths(new float[]{filledWidth, emptyWidth});
        } catch (DocumentException e) {
            // Si hay error, usar valores por defecto
            try {
                barContainer.setWidths(new float[]{50, 50});
            } catch (DocumentException ex) {
                // Continuar sin ajustar widths
            }
        }
        barContainer.setWidthPercentage(100);

        // Parte coloreada
        PdfPCell filledCell = new PdfPCell(new Phrase(String.valueOf(value),
            FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8, WHITE)));
        filledCell.setBackgroundColor(INSTITUTIONAL_RED);  // Barra con color institucional rojo
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

        // NUEVO: Tarjetas de resumen de tendencias
        addTrendsSummaryCards(document, trends);

        // Tendencia general mejorada
        PdfPTable trendTable = new PdfPTable(1);
        trendTable.setWidthPercentage(100);
        trendTable.setSpacingBefore(10);
        trendTable.setSpacingAfter(20);

        PdfPCell trendCell = new PdfPCell();
        BaseColor trendBgColor = getTrendColor(trends.getOverallTrend());
        trendCell.setBackgroundColor(trendBgColor);
        trendCell.setPadding(15);
        trendCell.setBorder(Rectangle.NO_BORDER);

        String trendIcon = getTrendIcon(trends.getOverallTrend());
        Paragraph trendText = new Paragraph();
        trendText.add(new Chunk(trendIcon + " TENDENCIA GENERAL: ",
            FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, WHITE)));
        trendText.add(new Chunk(translateTrend(trends.getOverallTrend()),
            FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, WHITE)));

        trendText.setAlignment(Element.ALIGN_CENTER);
        trendCell.addElement(trendText);
        trendTable.addCell(trendCell);
        document.add(trendTable);

        // Picos y valles mejorados
        document.add(new Paragraph("\n"));
        addSubsectionTitle(document, "📊 Picos y Valles Históricos");

        addEnhancedPeaksAndValleys(document, trends);
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
            String.valueOf(dirStats.getTotalUniqueDirectors()), INSTITUTIONAL_RED);
        addMetricCell(summaryTable, "Directores Actuales",
            String.valueOf(dirStats.getCurrentActiveDirectors()), INSTITUTIONAL_GOLD);
        addMetricCell(summaryTable, "Promedio Instancias/Director",
            String.format("%.1f", dirStats.getAverageInstancesPerDirector()), INSTITUTIONAL_GOLD);
        addMetricCell(summaryTable, "Director Más Experimentado",
            dirStats.getMostExperiencedDirector() != null ?
                dirStats.getMostExperiencedDirector() : "N/D",
            INSTITUTIONAL_GOLD);

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
            String.valueOf(studStats.getTotalHistoricalStudents()), INSTITUTIONAL_RED);
        addMetricCell(metricsTable, "Estudiantes Actuales",
            String.valueOf(studStats.getCurrentStudents()), INSTITUTIONAL_GOLD);
        addMetricCell(metricsTable, "Promedio Est./Instancia",
            String.format("%.1f", studStats.getAverageStudentsPerInstance()), INSTITUTIONAL_GOLD);
        addMetricCell(metricsTable, "Tipo Preferido",
            studStats.getPreferredType() != null ? studStats.getPreferredType() : "MIXTO",
            INSTITUTIONAL_GOLD);

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
            FontFactory.getFont(FontFactory.HELVETICA_BOLD, 13, WHITE)));
        verdictText.add(new Chunk(translatePerformanceVerdict(perf.getPerformanceVerdict()),
            FontFactory.getFont(FontFactory.HELVETICA_BOLD, 15, WHITE)));
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
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, INSTITUTIONAL_GOLD)));

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
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, INSTITUTIONAL_RED)));

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
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, INSTITUTIONAL_GOLD)));

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
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, INSTITUTIONAL_RED)));

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
        noteCell.setBackgroundColor(LIGHT_GOLD);
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
                "instancias", INSTITUTIONAL_RED);
        }

        if (report.getStudentStatistics() != null) {
            addStatCell(statsTable, "Total Estudiantes",
                String.valueOf(report.getStudentStatistics().getTotalHistoricalStudents()),
                "estudiantes", INSTITUTIONAL_GOLD);
        }

        if (report.getPerformanceAnalysis() != null) {
            addStatCell(statsTable, "Tasa de Éxito",
                String.format("%.1f%%", report.getPerformanceAnalysis().getSuccessRate()),
                "", INSTITUTIONAL_GOLD);  // Tasa de éxito: dorado (positivo)
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
            FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20, WHITE)));
        content.add(new Chunk(unit + "\n",
            FontFactory.getFont(FontFactory.HELVETICA, 9, new BaseColor(230, 230, 230))));
        content.add(new Chunk(label,
            FontFactory.getFont(FontFactory.HELVETICA, 10, WHITE)));
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
            FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, WHITE)));
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
     * Agregar fila de detalle
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
            FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, INSTITUTIONAL_RED)));
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
        line.setLineColor(INSTITUTIONAL_RED);
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
        labelCell.setBackgroundColor(LIGHT_GOLD);
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
        cell.setBackgroundColor(INSTITUTIONAL_RED);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setBorder(Rectangle.NO_BORDER);

        Paragraph p = new Paragraph(text,
            FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, WHITE));
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
        cell.setBackgroundColor(alternate ? LIGHT_GOLD : WHITE);
        cell.setBorder(Rectangle.NO_BORDER);
        table.addCell(cell);
    }

    /**
     * Agregar encabezado de tabla
     */
    private void addTableHeaderCell(PdfPTable table, String text) {
        PdfPCell cell = new PdfPCell(new Phrase(text,
            FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, WHITE)));
        cell.setPadding(8);
        cell.setBackgroundColor(INSTITUTIONAL_GOLD);
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
        cell.setBackgroundColor(alternate ? LIGHT_GOLD : WHITE);
        cell.setBorder(Rectangle.BOTTOM);
        cell.setBorderColor(LIGHT_GOLD);
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
        labelCell.setBorderColor(LIGHT_GOLD);
        labelCell.setBorderWidth(0.5f);
        table.addCell(labelCell);

        PdfPCell valueCell = new PdfPCell(new Phrase(value, BOLD_FONT));
        valueCell.setPadding(8);
        valueCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        valueCell.setBorder(Rectangle.BOTTOM);
        valueCell.setBorderColor(LIGHT_GOLD);
        valueCell.setBorderWidth(0.5f);
        table.addCell(valueCell);
    }

    /**
     * Agregar fila de proyección
     */
    private void addProjectionRow(PdfPTable table, String label, String value) {
        PdfPCell labelCell = new PdfPCell(new Phrase(label, BOLD_FONT));
        labelCell.setPadding(10);
        labelCell.setBackgroundColor(LIGHT_GOLD);
        labelCell.setBorder(Rectangle.NO_BORDER);
        table.addCell(labelCell);

        PdfPCell valueCell = new PdfPCell(new Phrase(value, NORMAL_FONT));
        valueCell.setPadding(10);
        valueCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        valueCell.setBackgroundColor(WHITE);
        valueCell.setBorder(Rectangle.NO_BORDER);
        table.addCell(valueCell);
    }

    // ==================== NUEVOS MÉTODOS PARA VISUALIZACIONES MEJORADAS ====================

    /**
     * Agregar tarjetas de resumen estadístico histórico
     */
    private void addHistoricalSummaryCards(Document document,
                                           List<ModalityHistoricalReportDTO.AcademicPeriodAnalysisDTO> periods)
            throws DocumentException {

        // Calcular estadísticas agregadas
        int totalPeriods = periods.size();
        int totalInstances = periods.stream().mapToInt(ModalityHistoricalReportDTO.AcademicPeriodAnalysisDTO::getTotalInstances).sum();
        int totalStudents = periods.stream().mapToInt(ModalityHistoricalReportDTO.AcademicPeriodAnalysisDTO::getStudentsEnrolled).sum();
        double avgInstancesPerPeriod = totalPeriods > 0 ? (double) totalInstances / totalPeriods : 0;

        // Tabla de 4 tarjetas
        PdfPTable cardsTable = new PdfPTable(4);
        cardsTable.setWidthPercentage(100);
        cardsTable.setSpacingBefore(10);
        cardsTable.setSpacingAfter(20);

        addSummaryCard(cardsTable, "Periodos Analizados", String.valueOf(totalPeriods), "📅", INSTITUTIONAL_GOLD);
        addSummaryCard(cardsTable, "Total Instancias", String.valueOf(totalInstances), "📊", INSTITUTIONAL_RED);
        addSummaryCard(cardsTable, "Total Estudiantes", String.valueOf(totalStudents), "👥", INSTITUTIONAL_GOLD);
        addSummaryCard(cardsTable, "Promedio/Periodo", String.format("%.1f", avgInstancesPerPeriod), "📈", INSTITUTIONAL_RED);

        document.add(cardsTable);
    }

    /**
     * Agregar tarjeta individual de resumen
     */
    private void addSummaryCard(PdfPTable table, String label, String value, String icon, BaseColor color) {
        PdfPCell card = new PdfPCell();
        card.setPadding(12);
        card.setBorderColor(color);
        card.setBorderWidth(2f);
        card.setBackgroundColor(WHITE);

        // Icono
        Paragraph iconPara = new Paragraph(icon,
                FontFactory.getFont(FontFactory.HELVETICA, 20, color));
        iconPara.setAlignment(Element.ALIGN_CENTER);
        card.addElement(iconPara);

        // Valor grande
        Paragraph valuePara = new Paragraph(value,
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 22, color));
        valuePara.setAlignment(Element.ALIGN_CENTER);
        valuePara.setSpacingBefore(3);
        card.addElement(valuePara);

        // Etiqueta
        Paragraph labelPara = new Paragraph(label,
                FontFactory.getFont(FontFactory.HELVETICA, 8, TEXT_GRAY));
        labelPara.setAlignment(Element.ALIGN_CENTER);
        labelPara.setSpacingBefore(3);
        card.addElement(labelPara);

        table.addCell(card);
    }

    /**
     * Gráfico de evolución mejorado con línea de tendencia visual
     */
    private void addEnhancedEvolutionChart(Document document,
                                           List<ModalityHistoricalReportDTO.AcademicPeriodAnalysisDTO> periods)
            throws DocumentException {

        addSubsectionTitle(document, "📈 Evolución Temporal de Instancias");

        // Encontrar valores máximo y mínimo para escalar
        int maxValue = periods.stream()
                .mapToInt(ModalityHistoricalReportDTO.AcademicPeriodAnalysisDTO::getTotalInstances)
                .max()
                .orElse(1);

        int minValue = periods.stream()
                .mapToInt(ModalityHistoricalReportDTO.AcademicPeriodAnalysisDTO::getTotalInstances)
                .min()
                .orElse(0);

        // Calcular promedio para línea de referencia
        double avgValue = periods.stream()
                .mapToInt(ModalityHistoricalReportDTO.AcademicPeriodAnalysisDTO::getTotalInstances)
                .average()
                .orElse(0);

        // Información de referencia
        PdfPTable legendTable = new PdfPTable(3);
        legendTable.setWidthPercentage(90);
        legendTable.setSpacingBefore(10);
        legendTable.setSpacingAfter(5);
        legendTable.setHorizontalAlignment(Element.ALIGN_CENTER);

        addLegendItem(legendTable, "📊 Máximo:", String.valueOf(maxValue), INSTITUTIONAL_GOLD);
        addLegendItem(legendTable, "📉 Mínimo:", String.valueOf(minValue), INSTITUTIONAL_RED);
        addLegendItem(legendTable, "📈 Promedio:", String.format("%.1f", avgValue), INSTITUTIONAL_GOLD);

        document.add(legendTable);

        // Crear gráfico mejorado con barras y etiquetas
        PdfPTable chartTable = new PdfPTable(1);
        chartTable.setWidthPercentage(100);
        chartTable.setSpacingBefore(10);
        chartTable.setSpacingAfter(15);

        for (ModalityHistoricalReportDTO.AcademicPeriodAnalysisDTO period : periods) {
            addEnhancedPeriodBar(chartTable, period, maxValue, avgValue);
        }

        document.add(chartTable);

        // Agregar línea de tendencia textual
        addTrendIndicator(document, periods);
    }

    /**
     * Agregar ítem de leyenda
     */
    private void addLegendItem(PdfPTable table, String label, String value, BaseColor color) {
        PdfPCell cell = new PdfPCell();
        cell.setPadding(5);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setBackgroundColor(LIGHT_GOLD);

        Paragraph content = new Paragraph();
        content.add(new Chunk(label + " ",
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, TEXT_BLACK)));
        content.add(new Chunk(value,
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, color)));
        content.setAlignment(Element.ALIGN_CENTER);

        cell.addElement(content);
        table.addCell(cell);
    }

    /**
     * Agregar barra de periodo mejorada con comparación al promedio
     */
    private void addEnhancedPeriodBar(PdfPTable table,
                                     ModalityHistoricalReportDTO.AcademicPeriodAnalysisDTO period,
                                     int maxValue,
                                     double avgValue) {
        PdfPCell containerCell = new PdfPCell();
        containerCell.setPadding(4);
        containerCell.setBorder(Rectangle.NO_BORDER);

        // Tabla interna: label + barra + valor
        PdfPTable innerTable = new PdfPTable(3);
        try {
            innerTable.setWidths(new float[]{1.2f, 4f, 1f});
        } catch (DocumentException e) {
            // Ignorar
        }

        // Etiqueta del periodo
        PdfPCell labelCell = new PdfPCell(new Phrase(period.getPeriodLabel(),
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, TEXT_BLACK)));
        labelCell.setBorder(Rectangle.NO_BORDER);
        labelCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        labelCell.setPadding(3);
        innerTable.addCell(labelCell);

        // Barra de progreso con color según relación con promedio
        float percentage = maxValue > 0 ? (float) period.getTotalInstances() / maxValue : 0;
        BaseColor barColor = period.getTotalInstances() >= avgValue ? INSTITUTIONAL_GOLD : INSTITUTIONAL_RED;

        PdfPCell barCell = createEnhancedBarCell(period.getTotalInstances(), percentage, barColor);
        innerTable.addCell(barCell);

        // Valor y comparación con promedio
        PdfPCell valueCell = new PdfPCell();
        valueCell.setBorder(Rectangle.NO_BORDER);
        valueCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        valueCell.setPadding(3);

        Paragraph valueContent = new Paragraph();
        valueContent.add(new Chunk(String.valueOf(period.getTotalInstances()),
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, barColor)));

        // Indicador visual de comparación con promedio
        String indicator = period.getTotalInstances() > avgValue ? " ▲" :
                          period.getTotalInstances() < avgValue ? " ▼" : " ●";
        valueContent.add(new Chunk(indicator,
                FontFactory.getFont(FontFactory.HELVETICA, 8, barColor)));

        valueCell.addElement(valueContent);
        innerTable.addCell(valueCell);

        containerCell.addElement(innerTable);
        table.addCell(containerCell);
    }

    /**
     * Crear celda de barra mejorada con gradiente visual
     */
    private PdfPCell createEnhancedBarCell(int value, float percentage, BaseColor color) {
        PdfPTable barContainer = new PdfPTable(2);

        // Asegurar que el ancho de la barra sea al menos 5% para visibilidad
        // pero nunca mayor a 100%
        float barWidth = Math.max(Math.min(percentage * 100, 100), 5);
        float emptyWidth = Math.max(100 - barWidth, 0.1f); // Evitar valores negativos o cero

        try {
            barContainer.setWidths(new float[]{barWidth, emptyWidth});
        } catch (DocumentException e) {
            // Si hay error en los widths, usar valores por defecto
            try {
                barContainer.setWidths(new float[]{50, 50});
            } catch (DocumentException ex) {
                // Ignorar si falla el fallback
            }
        }
        barContainer.setWidthPercentage(100);

        // Parte coloreada con valor
        PdfPCell filledCell = new PdfPCell(new Phrase(String.valueOf(value),
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, WHITE)));
        filledCell.setBackgroundColor(color);
        filledCell.setBorder(Rectangle.NO_BORDER);
        filledCell.setPadding(4);
        filledCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        filledCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        barContainer.addCell(filledCell);

        // Parte vacía
        PdfPCell emptyCell = new PdfPCell();
        emptyCell.setBackgroundColor(LIGHT_GOLD);
        emptyCell.setBorder(Rectangle.NO_BORDER);
        barContainer.addCell(emptyCell);

        PdfPCell containerCell = new PdfPCell();
        containerCell.addElement(barContainer);
        containerCell.setBorder(Rectangle.BOX);
        containerCell.setBorderColor(color);
        containerCell.setBorderWidth(0.5f);
        containerCell.setPadding(0);

        return containerCell;
    }

    /**
     * Agregar indicador de tendencia general
     */
    private void addTrendIndicator(Document document,
                                   List<ModalityHistoricalReportDTO.AcademicPeriodAnalysisDTO> periods)
            throws DocumentException {

        if (periods.size() < 2) return;

        // Comparar primer y último periodo (nota: lista está ordenada del más reciente al más antiguo)
        int firstValue = periods.get(0).getTotalInstances();
        int lastValue = periods.get(periods.size() - 1).getTotalInstances();

        // Calcular cambio absoluto y porcentual
        int absoluteChange = lastValue - firstValue;
        double change;
        String changeText;

        if (firstValue > 0) {
            // Calcular cambio porcentual normal
            change = ((double) absoluteChange / firstValue) * 100;
            changeText = String.format("%+.1f%%", change);
        } else if (lastValue > 0) {
            // Si firstValue es 0 pero lastValue no, es un crecimiento desde cero
            change = 100.0; // Consideramos como 100% de crecimiento desde cero
            changeText = "+100% (desde 0)";
        } else {
            // Ambos son 0, no hay cambio
            change = 0;
            changeText = "0%";
        }

        PdfPTable trendTable = new PdfPTable(1);
        trendTable.setWidthPercentage(90);
        trendTable.setSpacingBefore(10);
        trendTable.setHorizontalAlignment(Element.ALIGN_CENTER);

        PdfPCell trendCell = new PdfPCell();
        BaseColor trendColor = change > 0 ? INSTITUTIONAL_GOLD : change < 0 ? INSTITUTIONAL_RED : INSTITUTIONAL_GOLD;
        trendCell.setBackgroundColor(trendColor);
        trendCell.setPadding(8);
        trendCell.setBorder(Rectangle.NO_BORDER);

        String trendIcon = change > 5 ? "📈" : change < -5 ? "📉" : "➡";
        String trendText = change > 0 ? "TENDENCIA CRECIENTE" : change < 0 ? "TENDENCIA DECRECIENTE" : "TENDENCIA ESTABLE";

        Paragraph trendPara = new Paragraph();
        trendPara.add(new Chunk(trendIcon + " " + trendText + ": ",
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, WHITE)));
        trendPara.add(new Chunk(changeText,
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 13, WHITE)));
        trendPara.add(new Chunk(" (de " + firstValue + " a " + lastValue + " instancias)",
                FontFactory.getFont(FontFactory.HELVETICA, 9, WHITE)));
        trendPara.setAlignment(Element.ALIGN_CENTER);

        trendCell.addElement(trendPara);
        trendTable.addCell(trendCell);

        document.add(trendTable);
    }

    /**
     * Agregar tarjetas de resumen de tendencias
     */
    private void addTrendsSummaryCards(Document document, ModalityHistoricalReportDTO.TrendsEvolutionDTO trends)
            throws DocumentException {

        PdfPTable cardsTable = new PdfPTable(3);
        cardsTable.setWidthPercentage(100);
        cardsTable.setSpacingBefore(10);
        cardsTable.setSpacingAfter(15);

        // Tasa de crecimiento
        if (trends.getGrowthRate() != null) {
            BaseColor growthColor = trends.getGrowthRate() >= 0 ? INSTITUTIONAL_GOLD : INSTITUTIONAL_RED;
            addSummaryCard(cardsTable, "Tasa de Crecimiento",
                    String.format("%+.1f%%", trends.getGrowthRate()), "📈", growthColor);
        } else {
            addSummaryCard(cardsTable, "Tasa de Crecimiento", "N/D", "📈", INSTITUTIONAL_GOLD);
        }

        // Pico máximo
        if (trends.getPeakInstances() != null) {
            addSummaryCard(cardsTable, "Pico Máximo",
                    String.valueOf(trends.getPeakInstances()), "🔝", INSTITUTIONAL_GOLD);
        } else {
            addSummaryCard(cardsTable, "Pico Máximo", "N/D", "🔝", INSTITUTIONAL_GOLD);
        }

        // Valle mínimo
        if (trends.getLowestInstances() != null) {
            addSummaryCard(cardsTable, "Valle Mínimo",
                    String.valueOf(trends.getLowestInstances()), "📉", INSTITUTIONAL_RED);
        } else {
            addSummaryCard(cardsTable, "Valle Mínimo", "N/D", "📉", INSTITUTIONAL_RED);
        }

        document.add(cardsTable);
    }

    /**
     * Agregar visualización mejorada de picos y valles
     */
    private void addEnhancedPeaksAndValleys(Document document, ModalityHistoricalReportDTO.TrendsEvolutionDTO trends)
            throws DocumentException {

        PdfPTable peaksTable = new PdfPTable(2);
        peaksTable.setWidthPercentage(100);
        peaksTable.setWidths(new float[]{1f, 1f});
        peaksTable.setSpacingBefore(10);
        peaksTable.setSpacingAfter(15);

        // Pico mejorado
        if (trends.getPeakYear() != null && trends.getPeakSemester() != null) {
            PdfPCell peakCell = new PdfPCell();
            peakCell.setBackgroundColor(INSTITUTIONAL_GOLD);
            peakCell.setPadding(15);
            peakCell.setBorder(Rectangle.NO_BORDER);

            Paragraph peakContent = new Paragraph();
            peakContent.add(new Chunk("🏆 PICO MÁXIMO\n",
                    FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, WHITE)));
            peakContent.add(new Chunk("\n", FontFactory.getFont(FontFactory.HELVETICA, 8, WHITE)));
            peakContent.add(new Chunk(trends.getPeakYear() + "-" + trends.getPeakSemester() + "\n",
                    FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, WHITE)));
            peakContent.add(new Chunk(trends.getPeakInstances() + " instancias",
                    FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, WHITE)));
            peakContent.setAlignment(Element.ALIGN_CENTER);

            peakCell.addElement(peakContent);
            peaksTable.addCell(peakCell);
        }

        // Valle mejorado
        if (trends.getLowestYear() != null && trends.getLowestSemester() != null) {
            PdfPCell valleyCell = new PdfPCell();
            valleyCell.setBackgroundColor(INSTITUTIONAL_RED);
            valleyCell.setPadding(15);
            valleyCell.setBorder(Rectangle.NO_BORDER);

            Paragraph valleyContent = new Paragraph();
            valleyContent.add(new Chunk("⚠ VALLE MÍNIMO\n",
                    FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, WHITE)));
            valleyContent.add(new Chunk("\n", FontFactory.getFont(FontFactory.HELVETICA, 8, WHITE)));
            valleyContent.add(new Chunk(trends.getLowestYear() + "-" + trends.getLowestSemester() + "\n",
                    FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, WHITE)));
            valleyContent.add(new Chunk(trends.getLowestInstances() + " instancias",
                    FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, WHITE)));
            valleyContent.setAlignment(Element.ALIGN_CENTER);

            valleyCell.addElement(valleyContent);
            peaksTable.addCell(valleyCell);
        }

        document.add(peaksTable);

        // Diferencia entre pico y valle
        if (trends.getPeakInstances() != null && trends.getLowestInstances() != null) {
            int difference = trends.getPeakInstances() - trends.getLowestInstances();

            // Calcular porcentaje de variabilidad de forma más significativa
            // Usamos el promedio entre pico y valle como base de referencia
            double average = (trends.getPeakInstances() + trends.getLowestInstances()) / 2.0;
            double percentageDiff = average > 0 ? (difference / average * 100) : 0;

            // Alternativamente, podemos calcular el incremento desde el valle al pico
            double increaseFromLowest = trends.getLowestInstances() > 0 ?
                    ((double) difference / trends.getLowestInstances() * 100) : 0;

            PdfPTable diffTable = new PdfPTable(1);
            diffTable.setWidthPercentage(90);
            diffTable.setSpacingBefore(10);
            diffTable.setSpacingAfter(15);
            diffTable.setHorizontalAlignment(Element.ALIGN_CENTER);

            PdfPCell diffCell = new PdfPCell();
            diffCell.setBackgroundColor(LIGHT_GOLD);
            diffCell.setPadding(10);
            diffCell.setBorder(Rectangle.BOX);
            diffCell.setBorderColor(INSTITUTIONAL_GOLD);
            diffCell.setBorderWidth(2f);

            Paragraph diffText = new Paragraph();
            diffText.add(new Chunk("📊 Variabilidad Histórica: ",
                    FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, TEXT_BLACK)));
            diffText.add(new Chunk(difference + " instancias de diferencia ",
                    FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, INSTITUTIONAL_RED)));

            // Mostrar incremento desde el valle si es significativo
            if (trends.getLowestInstances() > 0 && increaseFromLowest > 0) {
                diffText.add(new Chunk("(" + String.format("+%.1f%%", increaseFromLowest) + " desde el valle)",
                        FontFactory.getFont(FontFactory.HELVETICA, 10, TEXT_GRAY)));
            } else if (trends.getLowestInstances() == 0) {
                diffText.add(new Chunk("(crecimiento desde 0)",
                        FontFactory.getFont(FontFactory.HELVETICA, 10, TEXT_GRAY)));
            }

            diffText.setAlignment(Element.ALIGN_CENTER);

            diffCell.addElement(diffText);
            diffTable.addCell(diffCell);

            document.add(diffTable);
        }

        // Patrones identificados
        if (trends.getIdentifiedPatterns() != null && !trends.getIdentifiedPatterns().isEmpty()) {
            document.add(new Paragraph("\n"));
            addSubsectionTitle(document, "🔍 Patrones Identificados");

            com.itextpdf.text.List patternList = new com.itextpdf.text.List(com.itextpdf.text.List.UNORDERED);
            patternList.setIndentationLeft(20);

            for (String pattern : trends.getIdentifiedPatterns()) {
                ListItem item = new ListItem(pattern,
                        FontFactory.getFont(FontFactory.HELVETICA, 10, TEXT_BLACK));
                item.setSpacingAfter(5);
                patternList.add(item);
            }

            document.add(patternList);
        }
    }

    /**
     * Obtener icono según la tendencia
     */
    private String getTrendIcon(String trend) {
        if (trend == null) return "📊";
        switch (trend) {
            case "GROWING": return "📈";
            case "STABLE": return "➡";
            case "DECLINING": return "📉";
            default: return "📊";
        }
    }

    // ==================== FIN DE NUEVOS MÉTODOS ====================

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
        if (popularity == null) return LIGHT_GOLD;
        switch (popularity) {
            case "HIGH": return INSTITUTIONAL_GOLD;      // Alta popularidad: dorado
            case "MEDIUM": return INSTITUTIONAL_GOLD;    // Media popularidad: dorado
            case "LOW": return INSTITUTIONAL_RED;        // Baja popularidad: rojo
            default: return LIGHT_GOLD;
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
        if (trend == null) return LIGHT_GOLD;
        switch (trend) {
            case "GROWING": return INSTITUTIONAL_GOLD;    // Crecimiento: dorado (positivo)
            case "STABLE": return INSTITUTIONAL_GOLD;     // Estable: dorado (neutro positivo)
            case "DECLINING": return INSTITUTIONAL_RED;   // Declive: rojo (alerta)
            default: return LIGHT_GOLD;
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
        if (verdict == null) return TEXT_GRAY;
        switch (verdict) {
            case "IMPROVED": return INSTITUTIONAL_GOLD;   // Mejorado: dorado (positivo)
            case "DECLINED": return INSTITUTIONAL_RED;    // Disminuido: rojo (alerta)
            case "STABLE": return INSTITUTIONAL_GOLD;     // Estable: dorado (neutro)
            default: return TEXT_GRAY;
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
        if (verdict == null) return LIGHT_GOLD;
        switch (verdict) {
            case "EXCELLENT": return INSTITUTIONAL_GOLD;         // Excelente: dorado
            case "GOOD": return INSTITUTIONAL_GOLD;              // Bueno: dorado
            case "REGULAR": return INSTITUTIONAL_RED;            // Regular: rojo (atención)
            case "NEEDS_IMPROVEMENT": return INSTITUTIONAL_RED;  // Necesita mejora: rojo (alerta)
            default: return LIGHT_GOLD;
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
        private final Font headerFont = FontFactory.getFont(FontFactory.HELVETICA, 8, TEXT_GRAY);
        private final Font footerFont = FontFactory.getFont(FontFactory.HELVETICA, 8, TEXT_GRAY);

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


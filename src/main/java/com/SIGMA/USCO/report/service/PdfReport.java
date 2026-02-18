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

    // Colores institucionales
    private static final BaseColor INSTITUTIONAL_RED = new BaseColor(143, 30, 30); // #8F1E1E
    private static final BaseColor INSTITUTIONAL_GOLD = new BaseColor(213, 203, 160); // #D5CBA0
    private static final BaseColor LIGHT_GOLD = new BaseColor(245, 242, 235); // Tono claro de dorado para fondos

    // Fuentes con colores institucionales
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
        addModalityDetails(document, report.getModalities());
        addFooter(document, report);

        document.close();
        return outputStream;
    }


    private void addHeader(Document document) throws DocumentException {
        // Fondo dorado claro
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

        // Línea separadora en color rojo institucional
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

        // Agregar información del programa académico
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

        // Información general del reporte
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

        // Tabla de métricas con colores institucionales
        PdfPTable metricsTable = new PdfPTable(2);
        metricsTable.setWidthPercentage(100);
        metricsTable.setSpacingAfter(15);

        // Todas las métricas con colores institucionales
        addMetricRow(metricsTable, "Total de Modalidades Activas",
                summary.getTotalActiveModalities().toString(), INSTITUTIONAL_RED);
        addMetricRow(metricsTable, "Total de Estudiantes Activos",
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

        // Distribución por tipo
        addSubsectionTitle(document, "1.1 Distribución por Tipo de Modalidad");
        PdfPTable typeTable = createDistributionTable(summary.getModalitiesByType());
        document.add(typeTable);

        // Distribución por estado
        addSubsectionTitle(document, "1.2 Distribución por Estado");
        PdfPTable statusTable = createDistributionTable(summary.getModalitiesByStatus());
        document.add(statusTable);
    }





    private void addModalityDetails(Document document, java.util.List<ModalityDetailReportDTO> modalities)
            throws DocumentException {


        document.newPage();

        addSectionTitle(document, "3. DETALLE DE MODALIDADES ACTIVAS");

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


        // Encabezados de la tabla
        addTableHeader(detailTable, "ID");
        addTableHeader(detailTable, "Modalidad");
        addTableHeader(detailTable, "Estudiante(s)");
        addTableHeader(detailTable, "Programa");
        addTableHeader(detailTable, "Estado");
        addTableHeader(detailTable, "Director");
        addTableHeader(detailTable, "Días desde Inicio");

        // Agregar filas de datos
        for (ModalityDetailReportDTO modality : modalities) {
            addDetailRow(detailTable, modality);
        }

        document.add(detailTable);
    }


    private void addFooter(Document document, GlobalModalityReportDTO report) throws DocumentException {
        document.newPage();

        addSectionTitle(document, "4. OBSERVACIONES Y NOTAS");

        Paragraph notes = new Paragraph();
        notes.setFont(NORMAL_FONT);
        notes.setAlignment(Element.ALIGN_JUSTIFIED);

        // Nota sobre el filtrado por programa
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

    private PdfPTable createDistributionTable(Map<String, Long> distribution) {
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(90);
        table.setSpacingAfter(15);

        // Encabezados con colores institucionales
        PdfPCell headerCell1 = new PdfPCell(new Phrase("Categoría", HEADER_TABLE_FONT));
        headerCell1.setHorizontalAlignment(Element.ALIGN_CENTER);
        headerCell1.setBackgroundColor(INSTITUTIONAL_RED);
        headerCell1.setPadding(8);
        headerCell1.setBorder(Rectangle.BOX);
        headerCell1.setBorderColor(INSTITUTIONAL_GOLD);
        table.addCell(headerCell1);

        PdfPCell headerCell2 = new PdfPCell(new Phrase("Cantidad", HEADER_TABLE_FONT));
        headerCell2.setHorizontalAlignment(Element.ALIGN_CENTER);
        headerCell2.setBackgroundColor(INSTITUTIONAL_RED);
        headerCell2.setPadding(8);
        headerCell2.setBorder(Rectangle.BOX);
        headerCell2.setBorderColor(INSTITUTIONAL_GOLD);
        table.addCell(headerCell2);

        // Datos con colores alternos
        boolean alternate = false;
        for (Map.Entry<String, Long> entry : distribution.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .toList()) {

            PdfPCell cell1 = new PdfPCell(new Phrase(entry.getKey(), NORMAL_FONT));
            cell1.setPadding(6);
            cell1.setBackgroundColor(alternate ? LIGHT_GOLD : BaseColor.WHITE);
            cell1.setBorder(Rectangle.BOX);
            cell1.setBorderColor(INSTITUTIONAL_GOLD);
            table.addCell(cell1);

            PdfPCell cell2 = new PdfPCell(new Phrase(entry.getValue().toString(), NORMAL_FONT));
            cell2.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell2.setPadding(6);
            cell2.setBackgroundColor(alternate ? LIGHT_GOLD : BaseColor.WHITE);
            cell2.setBorder(Rectangle.BOX);
            cell2.setBorderColor(INSTITUTIONAL_GOLD);
            table.addCell(cell2);

            alternate = !alternate;
        }

        return table;
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

    /**
     * Verifica si una modalidad no requiere director
     * @param modalityName Nombre de la modalidad
     * @return true si no requiere director, false en caso contrario
     */
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
}


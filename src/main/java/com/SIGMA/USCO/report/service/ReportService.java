package com.SIGMA.USCO.report.service;

import com.SIGMA.USCO.Modalities.Entity.DegreeModality;
import com.SIGMA.USCO.Modalities.Entity.StudentModality;
import com.SIGMA.USCO.Modalities.Entity.enums.ModalityStatus;
import com.SIGMA.USCO.Modalities.Entity.enums.ModalityType;
import com.SIGMA.USCO.Modalities.Repository.StudentModalityRepository;
import com.SIGMA.USCO.report.dto.DirectorModalitiesDTO;
import com.SIGMA.USCO.report.dto.ModalityReportDTO;
import com.SIGMA.USCO.report.dto.SemesterModalitiesFilterDTO;
import com.SIGMA.USCO.report.dto.SemesterTypeComparisonDTO;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RequiredArgsConstructor
@Service
public class ReportService {

    private final StudentModalityRepository studentModalityRepository;


    public byte[] exportActiveModalitiesPdf() throws Exception {

        List<ModalityReportDTO> data = buildActiveModalitiesReport();

        Document document = new Document(PageSize.A4, 50, 50, 60, 50);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, out);
        document.open();

        Font titleFont = new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD);
        Font subtitleFont = new Font(Font.FontFamily.HELVETICA, 11);
        Font metaFont = new Font(Font.FontFamily.HELVETICA, 9, Font.ITALIC);
        Font headerFont = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD);
        Font cellFont = new Font(Font.FontFamily.HELVETICA, 10);

        Paragraph title = new Paragraph(
                "REPORTE GLOBAL DE MODALIDADES DE GRADO ACTIVAS",
                titleFont
        );
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);

        Paragraph subtitle = new Paragraph(
                "Sistema Integrado de Gestión de Modalidades Académicas – SIGMA\n\n",
                subtitleFont
        );
        subtitle.setAlignment(Element.ALIGN_CENTER);
        document.add(subtitle);

        Paragraph date = new Paragraph(
                "Fecha de generación: " + LocalDate.now() + "\n\n",
                metaFont
        );
        date.setAlignment(Element.ALIGN_RIGHT);
        document.add(date);

        PdfPTable table = new PdfPTable(5);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{3f, 2f, 2f, 2f, 4f});

        Stream.of(
                "Modalidad",
                "Tipo",
                "Estado",
                "Estudiantes",
                "Director(es)"
        ).forEach(h -> {
            PdfPCell cell = new PdfPCell(new Phrase(h, headerFont));
            cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setPadding(6);
            table.addCell(cell);
        });

        for (ModalityReportDTO row : data) {
            table.addCell(new Phrase(row.getModalityName(), cellFont));
            table.addCell(new Phrase(row.getModalityType(), cellFont));
            table.addCell(new Phrase(row.getModalityStatus(), cellFont));
            table.addCell(new Phrase(row.getTotalStudents().toString(), cellFont));
            table.addCell(new Phrase(row.getDirectors(), cellFont));
        }

        document.add(table);

        Paragraph footer = new Paragraph(
                "\nReporte generado automáticamente por el sistema SIGMA.",
                metaFont
        );
        footer.setAlignment(Element.ALIGN_CENTER);
        document.add(footer);

        document.close();
        return out.toByteArray();
    }

    public byte[] exportActiveModalitiesExcel() throws Exception {

        List<ModalityReportDTO> data = buildActiveModalitiesReport();

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Modalidades Activas");

        Font headerFont = (Font) workbook.createFont();
        ((org.apache.poi.ss.usermodel.Font) headerFont).setBold(true);

        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setFont((org.apache.poi.ss.usermodel.Font) headerFont);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);
        headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        Row header = sheet.createRow(0);
        String[] columns = {
                "Modalidad",
                "Tipo",
                "Estado",
                "Total Estudiantes",
                "Director(es)"
        };

        for (int i = 0; i < columns.length; i++) {
            Cell cell = header.createCell(i);
            cell.setCellValue(columns[i]);
            cell.setCellStyle(headerStyle);
        }

        int rowIdx = 1;
        for (ModalityReportDTO row : data) {
            Row r = sheet.createRow(rowIdx++);
            r.createCell(0).setCellValue(row.getModalityName());
            r.createCell(1).setCellValue(row.getModalityType());
            r.createCell(2).setCellValue(row.getModalityStatus());
            r.createCell(3).setCellValue(row.getTotalStudents());
            r.createCell(4).setCellValue(row.getDirectors());
        }

        for (int i = 0; i < columns.length; i++) {
            sheet.autoSizeColumn(i);
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        workbook.close();

        return out.toByteArray();
    }

    public List<ModalityReportDTO> buildActiveModalitiesReport() {

        List<StudentModality> studentModalities =
                studentModalityRepository.findByModality_Status(ModalityStatus.ACTIVE);

        Map<Long, List<StudentModality>> grouped =
                studentModalities.stream()
                        .collect(Collectors.groupingBy(
                                sm -> sm.getModality().getId()
                        ));

        return grouped.values().stream()
                .map(list -> {

                    DegreeModality modality = list.get(0).getModality();

                    String directors = list.stream()
                            .map(StudentModality::getProjectDirector)
                            .filter(Objects::nonNull)
                            .map(d -> d.getName() + " " + d.getLastName())
                            .distinct()
                            .collect(Collectors.joining(", "));

                    return ModalityReportDTO.builder()
                            .modalityName(modality.getName())
                            .modalityType(modality.getType().name())
                            .modalityStatus(modality.getStatus().name())
                            .totalStudents((long) list.size())
                            .directors(
                                    directors.isBlank()
                                            ? "Sin director asignado"
                                            : directors
                            )
                            .build();
                })
                .toList();
    }

    public ResponseEntity<?> getReportByModalityType(ModalityReportDTO filter) {

        List<StudentModality> modalities;

        if (filter != null && filter.getYear() != null) {

            LocalDateTime start;
            LocalDateTime end;

            if (filter.getSemester() != null) {

                if (filter.getSemester() == 1) {
                    start = LocalDate.of(filter.getYear(), 1, 1).atStartOfDay();
                    end = LocalDate.of(filter.getYear(), 6, 30).atTime(23, 59);
                } else {
                    start = LocalDate.of(filter.getYear(), 7, 1).atStartOfDay();
                    end = LocalDate.of(filter.getYear(), 12, 31).atTime(23, 59);
                }

            } else {
                start = LocalDate.of(filter.getYear(), 1, 1).atStartOfDay();
                end = LocalDate.of(filter.getYear(), 12, 31).atTime(23, 59);
            }

            modalities =
                    studentModalityRepository.findBySelectionDateBetween(start, end);

        } else {
            modalities = studentModalityRepository.findAll();
        }

        Map<String, Long> grouped =
                modalities.stream()
                        .collect(Collectors.groupingBy(
                                sm -> sm.getModality().getType().name(),
                                Collectors.mapping(
                                        sm -> sm.getStudent().getId(),
                                        Collectors.collectingAndThen(
                                                Collectors.toSet(),
                                                set -> (long) set.size()
                                        )
                                )
                        ));

        List<ModalityReportDTO> response =
                grouped.entrySet().stream()
                        .map(e -> ModalityReportDTO.builder()
                                .modalityType(e.getKey())
                                .totalStudents(e.getValue())
                                .build()
                        )
                        .toList();

        return ResponseEntity.ok(response);
    }

    public ResponseEntity<byte[]> exportModalitiesByTypePdf(ModalityReportDTO filter) throws Exception {

        List<ModalityReportDTO> data =
                (List<ModalityReportDTO>)
                        getReportByModalityType(filter).getBody();

        Document document = new Document(PageSize.A4, 50, 50, 60, 50);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, out);
        document.open();

        Font titleFont = new Font(Font.FontFamily.HELVETICA, 15, Font.BOLD);
        Font metaFont = new Font(Font.FontFamily.HELVETICA, 9, Font.ITALIC);
        Font headerFont = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD);
        Font cellFont = new Font(Font.FontFamily.HELVETICA, 10);

        Paragraph title = new Paragraph(
                "REPORTE DE MODALIDADES POR TIPO DE GRADO",
                titleFont
        );
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(15);
        document.add(title);

        Paragraph meta = new Paragraph(
                "Sistema SIGMA • Fecha: " + LocalDate.now(),
                metaFont
        );
        meta.setAlignment(Element.ALIGN_RIGHT);
        meta.setSpacingAfter(20);
        document.add(meta);

        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(80);
        table.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.setWidths(new float[]{4f, 2f});

        Stream.of("Tipo de Modalidad", "Total Estudiantes")
                .forEach(h -> {
                    PdfPCell cell = new PdfPCell(new Phrase(h, headerFont));
                    cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cell.setPadding(6);
                    table.addCell(cell);
                });

        for (ModalityReportDTO row : data) {
            table.addCell(new Phrase(row.getModalityType(), cellFont));
            table.addCell(new Phrase(
                    row.getTotalStudents().toString(),
                    cellFont
            ));
        }

        document.add(table);

        Paragraph footer = new Paragraph(
                "\nReporte generado automáticamente por SIGMA.",
                metaFont
        );
        footer.setAlignment(Element.ALIGN_CENTER);
        footer.setSpacingBefore(20);
        document.add(footer);

        document.close();

        return ResponseEntity.ok()
                .header("Content-Disposition",
                        "attachment; filename=modalidades_por_tipo.pdf")
                .contentType(
                        org.springframework.http.MediaType.APPLICATION_PDF
                )
                .body(out.toByteArray());
    }

    public ResponseEntity<byte[]> exportModalitiesByTypeExcel(
            ModalityReportDTO filter
    ) throws Exception {

        List<ModalityReportDTO> data =
                (List<ModalityReportDTO>)
                        getReportByModalityType(filter).getBody();

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Modalidades por Tipo");

        Font headerFont = (Font) workbook.createFont();
        headerFont.isBold();

        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setFont((org.apache.poi.ss.usermodel.Font) headerFont);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);
        headerStyle.setFillForegroundColor(
                IndexedColors.GREY_25_PERCENT.getIndex()
        );
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerStyle.setBorderBottom(BorderStyle.THIN);

        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("Tipo de Modalidad");
        headerRow.createCell(1).setCellValue("Total Estudiantes");

        headerRow.getCell(0).setCellStyle(headerStyle);
        headerRow.getCell(1).setCellStyle(headerStyle);

        int rowIdx = 1;
        for (ModalityReportDTO row : data) {
            Row excelRow = sheet.createRow(rowIdx++);
            excelRow.createCell(0).setCellValue(row.getModalityType());
            excelRow.createCell(1).setCellValue(row.getTotalStudents());
        }

        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);
        sheet.createFreezePane(0, 1);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        workbook.close();

        return ResponseEntity.ok()
                .header("Content-Disposition",
                        "attachment; filename=modalidades_por_tipo.xlsx")
                .contentType(
                        MediaType
                                .parseMediaType(
                                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
                                )
                )
                .body(out.toByteArray());
    }

    public ResponseEntity<List<ModalityReportDTO>> getModalitiesComparisonByType(ModalityReportDTO filter) {

        List<StudentModality> modalities = studentModalityRepository.findAll();


        if (filter != null && filter.getYear() != null) {
            modalities = modalities.stream()
                    .filter(sm ->
                            sm.getSelectionDate() != null &&
                                    sm.getSelectionDate().getYear() == filter.getYear()
                    )
                    .toList();
        }

        if (filter != null && filter.getSemester() != null) {
            modalities = modalities.stream()
                    .filter(sm -> {
                        int month = sm.getSelectionDate().getMonthValue();
                        return filter.getSemester() == 1
                                ? month <= 6
                                : month > 6;
                    })
                    .toList();
        }


        Map<String, Long> grouped = modalities.stream()
                .collect(Collectors.groupingBy(
                        sm -> sm.getModality().getType().name(),
                        Collectors.counting()
                ));


        List<ModalityReportDTO> response = grouped.entrySet().stream()
                .map(e -> ModalityReportDTO.builder()
                        .modalityType(e.getKey())
                        .year(filter != null ? filter.getYear() : null)
                        .semester(filter != null ? filter.getSemester() : null)
                        .totalStudents(e.getValue())
                        .build()
                )
                .toList();

        return ResponseEntity.ok(response);
    }

    public ResponseEntity<byte[]> exportModalitiesComparisonPdf(
            ModalityReportDTO filter
    ) throws Exception {

        List<ModalityReportDTO> data =
                (List<ModalityReportDTO>)
                        getModalitiesComparisonByType(filter).getBody();

        Document document = new Document(PageSize.A4, 50, 50, 60, 50);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, out);
        document.open();

        Font titleFont = new Font(Font.FontFamily.HELVETICA, 15, Font.BOLD);
        Font subtitleFont = new Font(Font.FontFamily.HELVETICA, 11);
        Font metaFont = new Font(Font.FontFamily.HELVETICA, 9, Font.ITALIC);
        Font headerFont = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD);
        Font cellFont = new Font(Font.FontFamily.HELVETICA, 10);

        Paragraph title = new Paragraph(
                "COMPARATIVA DE MODALIDADES POR TIPO DE GRADO",
                titleFont
        );
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(10);
        document.add(title);

        Paragraph subtitle = new Paragraph(
                "Sistema SIGMA – Consejo y Secretaría del Programa",
                subtitleFont
        );
        subtitle.setAlignment(Element.ALIGN_CENTER);
        subtitle.setSpacingAfter(15);
        document.add(subtitle);

        Paragraph meta = new Paragraph(
                "Periodo: " +
                        (filter != null && filter.getYear() != null
                                ? filter.getYear()
                                : "Todos") +
                        " | Semestre: " +
                        (filter != null && filter.getSemester() != null
                                ? filter.getSemester()
                                : "Todos") +
                        " | Fecha: " + LocalDate.now(),
                metaFont
        );
        meta.setAlignment(Element.ALIGN_RIGHT);
        meta.setSpacingAfter(20);
        document.add(meta);

        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(90);
        table.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.setWidths(new float[]{4f, 2f, 2f, 2f});

        Stream.of(
                "Tipo de Modalidad",
                "Año",
                "Semestre",
                "Total Estudiantes"
        ).forEach(h -> {
            PdfPCell cell = new PdfPCell(new Phrase(h, headerFont));
            cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setPadding(6);
            table.addCell(cell);
        });

        for (ModalityReportDTO row : data) {
            table.addCell(new Phrase(row.getModalityType(), cellFont));
            table.addCell(new Phrase(
                    row.getYear() != null ? row.getYear().toString() : "—",
                    cellFont
            ));
            table.addCell(new Phrase(
                    row.getSemester() != null ? row.getSemester().toString() : "—",
                    cellFont
            ));
            table.addCell(new Phrase(
                    row.getTotalStudents().toString(),
                    cellFont
            ));
        }

        document.add(table);

        Paragraph footer = new Paragraph(
                "\nReporte generado automáticamente por SIGMA.",
                metaFont
        );
        footer.setAlignment(Element.ALIGN_CENTER);
        footer.setSpacingBefore(20);
        document.add(footer);

        document.close();

        return ResponseEntity.ok()
                .header(
                        "Content-Disposition",
                        "attachment; filename=comparativa_modalidades_tipo.pdf"
                )
                .contentType(
                        org.springframework.http.MediaType.APPLICATION_PDF
                )
                .body(out.toByteArray());
    }

    public ResponseEntity<byte[]> exportModalitiesComparisonExcel(
            ModalityReportDTO filter
    ) throws Exception {

        List<ModalityReportDTO> data =
                (List<ModalityReportDTO>)
                        getModalitiesComparisonByType(filter).getBody();

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Comparativa por Tipo");

        Font headerFont = (Font) workbook.createFont();
        headerFont.isBold();

        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setFont((org.apache.poi.ss.usermodel.Font) headerFont);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);
        headerStyle.setFillForegroundColor(
                IndexedColors.GREY_25_PERCENT.getIndex()
        );
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerStyle.setBorderBottom(BorderStyle.THIN);

        Row headerRow = sheet.createRow(0);
        String[] headers = {
                "Tipo de Modalidad",
                "Año",
                "Semestre",
                "Total Estudiantes"
        };

        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        int rowIdx = 1;
        for (ModalityReportDTO row : data) {
            Row excelRow = sheet.createRow(rowIdx++);
            excelRow.createCell(0).setCellValue(row.getModalityType());
            excelRow.createCell(1).setCellValue(
                    row.getYear() != null ? row.getYear() : 0
            );
            excelRow.createCell(2).setCellValue(
                    row.getSemester() != null ? row.getSemester() : 0
            );
            excelRow.createCell(3).setCellValue(row.getTotalStudents());
        }

        sheet.setAutoFilter(new CellRangeAddress(0, rowIdx - 1, 0, 3));
        sheet.createFreezePane(0, 1);

        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        workbook.close();

        return ResponseEntity.ok()
                .header(
                        "Content-Disposition",
                        "attachment; filename=comparativa_modalidades_tipo.xlsx"
                )
                .contentType(
                        MediaType.parseMediaType(
                                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
                        )
                )
                .body(out.toByteArray());
    }

    public ResponseEntity<?> getModalitiesByDirector(Long directorId, String statusFilter) {

        List<StudentModality> modalities =
                studentModalityRepository.findByProjectDirector_Id(directorId);

        if (statusFilter != null) {
            modalities = modalities.stream()
                    .filter(sm -> sm.getStatus().name().equals(statusFilter))
                    .toList();
        }

        List<DirectorModalitiesDTO> report = modalities.stream()
                .map(sm -> DirectorModalitiesDTO.builder()
                        .directorName(
                                sm.getProjectDirector().getName() + " " +
                                        sm.getProjectDirector().getLastName()
                        )
                        .studentName(
                                sm.getStudent().getName() + " " +
                                        sm.getStudent().getLastName()
                        )
                        .studentEmail(sm.getStudent().getEmail())
                        .modalityName(sm.getModality().getName())
                        .modalityStatus(sm.getStatus().name())
                        .startDate(sm.getSelectionDate())
                        .build()
                )
                .toList();

        return ResponseEntity.ok(report);
    }

    public ResponseEntity<byte[]> exportModalitiesByDirectorPdf(Long directorId, String statusFilter) throws Exception {
        List<DirectorModalitiesDTO> data =
                (List<DirectorModalitiesDTO>)
                        getModalitiesByDirector(directorId, statusFilter).getBody();

        Document document = new Document(PageSize.A4, 50, 50, 60, 50);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, out);
        document.open();

        Font titleFont = new Font(Font.FontFamily.HELVETICA, 15, Font.BOLD);
        Font headerFont = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD);
        Font cellFont = new Font(Font.FontFamily.HELVETICA, 10);
        Font metaFont = new Font(Font.FontFamily.HELVETICA, 9, Font.ITALIC);

        document.add(new Paragraph(
                "REPORTE DE MODALIDADES POR DIRECTOR DE PROYECTO",
                titleFont
        ));

        document.add(new Paragraph(
                "Fecha de generación: " + LocalDate.now() + "\n\n",
                metaFont
        ));

        PdfPTable table = new PdfPTable(5);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{3f, 3f, 3f, 2f, 2f});

        Stream.of(
                "Director",
                "Estudiante",
                "Correo",
                "Modalidad",
                "Estado"
        ).forEach(h -> {
            PdfPCell cell = new PdfPCell(new Phrase(h, headerFont));
            cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
        });

        for (DirectorModalitiesDTO row : data) {
            table.addCell(new Phrase(row.getDirectorName(), cellFont));
            table.addCell(new Phrase(row.getStudentName(), cellFont));
            table.addCell(new Phrase(row.getStudentEmail(), cellFont));
            table.addCell(new Phrase(row.getModalityName(), cellFont));
            table.addCell(new Phrase(row.getModalityStatus(), cellFont));
        }

        document.add(table);
        document.close();

        return ResponseEntity.ok()
                .header(
                        "Content-Disposition",
                        "attachment; filename=modalidades_por_director.pdf"
                )
                .contentType(
                        org.springframework.http.MediaType.APPLICATION_PDF
                )
                .body(out.toByteArray());
    }

    public ResponseEntity<byte[]> exportModalitiesByDirectorExcel(Long directorId, String statusFilter) throws Exception {

        List<DirectorModalitiesDTO> data =
                (List<DirectorModalitiesDTO>)
                        getModalitiesByDirector(directorId, statusFilter).getBody();

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Modalidades por Director");

        Font headerFont = (Font) workbook.createFont();
        headerFont.isBold();

        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setFont((org.apache.poi.ss.usermodel.Font) headerFont);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);

        Row header = sheet.createRow(0);
        String[] columns = {
                "Director",
                "Estudiante",
                "Correo",
                "Modalidad",
                "Estado",
                "Fecha Inicio"
        };

        for (int i = 0; i < columns.length; i++) {
            Cell cell = header.createCell(i);
            cell.setCellValue(columns[i]);
            cell.setCellStyle(headerStyle);
        }

        int rowIdx = 1;
        for (DirectorModalitiesDTO row : data) {
            Row r = sheet.createRow(rowIdx++);
            r.createCell(0).setCellValue(row.getDirectorName());
            r.createCell(1).setCellValue(row.getStudentName());
            r.createCell(2).setCellValue(row.getStudentEmail());
            r.createCell(3).setCellValue(row.getModalityName());
            r.createCell(4).setCellValue(row.getModalityStatus());
            r.createCell(5).setCellValue(
                    row.getStartDate() != null
                            ? row.getStartDate().toString()
                            : ""
            );
        }

        for (int i = 0; i < columns.length; i++) {
            sheet.autoSizeColumn(i);
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        workbook.close();

        return ResponseEntity.ok()
                .header(
                        "Content-Disposition",
                        "attachment; filename=modalidades_por_director.xlsx"
                )
                .contentType(
                        MediaType.parseMediaType(
                                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
                        )
                )
                .body(out.toByteArray());
    }

    public ResponseEntity<List<DirectorModalitiesDTO>> getModalitiesBySemester(ModalityReportDTO filter) {

        if (filter == null || filter.getYear() == null) {
            return ResponseEntity.badRequest()
                    .body(List.of());
        }

        LocalDateTime start;
        LocalDateTime end;

        if (filter.getSemester() != null) {

            if (filter.getSemester() == 1) {
                start = LocalDate.of(filter.getYear(), 1, 1).atStartOfDay();
                end = LocalDate.of(filter.getYear(), 6, 30).atTime(23, 59);
            } else {
                start = LocalDate.of(filter.getYear(), 7, 1).atStartOfDay();
                end = LocalDate.of(filter.getYear(), 12, 31).atTime(23, 59);
            }

        } else {
            start = LocalDate.of(filter.getYear(), 1, 1).atStartOfDay();
            end = LocalDate.of(filter.getYear(), 12, 31).atTime(23, 59);
        }

        List<StudentModality> modalities =
                studentModalityRepository.findBySelectionDateBetween(start, end);


        modalities = modalities.stream()
                .filter(sm -> sm.getModality().getStatus() == ModalityStatus.ACTIVE)
                .toList();


        if (filter.getModalityType() != null) {
            modalities = modalities.stream()
                    .filter(sm ->
                            sm.getModality().getType().name().equals(filter.getModalityType())
                    )
                    .toList();
        }


        if (filter.getModalityStatus() != null) {
            modalities = modalities.stream()
                    .filter(sm ->
                            sm.getStatus().name().equals(filter.getModalityStatus())
                    )
                    .toList();
        }

        List<DirectorModalitiesDTO> response =
                modalities.stream()
                        .map(sm -> DirectorModalitiesDTO.builder()
                                .studentName(
                                        sm.getStudent().getName() + " " +
                                                sm.getStudent().getLastName()
                                )
                                .studentEmail(sm.getStudent().getEmail())
                                .directorName(
                                        sm.getProjectDirector() != null
                                                ? sm.getProjectDirector().getName() + " " +
                                                sm.getProjectDirector().getLastName()
                                                : "Sin director asignado"
                                )
                                .modalityName(sm.getModality().getName())
                                .modalityStatus(sm.getStatus().name())
                                .academicDistinction(sm.getAcademicDistinction())
                                .startDate(sm.getSelectionDate())
                                .build()
                        )
                        .toList();

        return ResponseEntity.ok(response);
    }

    public ResponseEntity<byte[]> exportModalitiesBySemesterPdf(ModalityReportDTO filter) throws Exception {

        List<DirectorModalitiesDTO> data =
                getModalitiesBySemester(filter).getBody();

        Document document = new Document(PageSize.A4, 50, 50, 60, 50);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, out);
        document.open();

        Font titleFont = new Font(Font.FontFamily.HELVETICA, 15, Font.BOLD);
        Font metaFont = new Font(Font.FontFamily.HELVETICA, 9, Font.ITALIC);
        Font headerFont = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD);
        Font cellFont = new Font(Font.FontFamily.HELVETICA, 10);

        Paragraph title = new Paragraph(
                "REPORTE DE MODALIDADES ACTIVAS POR SEMESTRE",
                titleFont
        );
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(10);
        document.add(title);

        Paragraph meta = new Paragraph(
                "Año: " + filter.getYear() +
                        (filter.getSemester() != null
                                ? " | Semestre: " + filter.getSemester()
                                : " | Semestre: Todos") +
                        " | Fecha: " + LocalDate.now(),
                metaFont
        );
        meta.setAlignment(Element.ALIGN_RIGHT);
        meta.setSpacingAfter(15);
        document.add(meta);

        PdfPTable table = new PdfPTable(6);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{3f, 3f, 3f, 3f, 2f, 2f});

        Stream.of(
                "Estudiante",
                "Correo",
                "Director",
                "Modalidad",
                "Estado",
                "Mención"
        ).forEach(h -> {
            PdfPCell cell = new PdfPCell(new Phrase(h, headerFont));
            cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setPadding(6);
            table.addCell(cell);
        });

        for (DirectorModalitiesDTO row : data) {
            table.addCell(new Phrase(row.getStudentName(), cellFont));
            table.addCell(new Phrase(row.getStudentEmail(), cellFont));
            table.addCell(new Phrase(row.getDirectorName(), cellFont));
            table.addCell(new Phrase(row.getModalityName(), cellFont));
            table.addCell(new Phrase(row.getModalityStatus(), cellFont));
            table.addCell(new Phrase(
                    row.getAcademicDistinction() != null
                            ? row.getAcademicDistinction().name()
                            : "—",
                    cellFont
            ));
        }

        document.add(table);

        Paragraph footer = new Paragraph(
                "\nReporte generado automáticamente por SIGMA.",
                metaFont
        );
        footer.setAlignment(Element.ALIGN_CENTER);
        footer.setSpacingBefore(15);
        document.add(footer);

        document.close();

        return ResponseEntity.ok()
                .header(
                        "Content-Disposition",
                        "attachment; filename=modalidades_por_semestre.pdf"
                )
                .contentType(MediaType.APPLICATION_PDF)
                .body(out.toByteArray());
    }

    public ResponseEntity<byte[]> exportModalitiesBySemesterExcel(ModalityReportDTO filter) throws Exception {

        List<DirectorModalitiesDTO> data =
                getModalitiesBySemester(filter).getBody();

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Modalidades por Semestre");

        Font headerFont = (Font) workbook.createFont();
        ((org.apache.poi.ss.usermodel.Font) headerFont).setBold(true);

        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setFont((org.apache.poi.ss.usermodel.Font) headerFont);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);
        headerStyle.setFillForegroundColor(
                IndexedColors.GREY_25_PERCENT.getIndex()
        );
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerStyle.setBorderBottom(BorderStyle.THIN);

        String[] headers = {
                "Estudiante",
                "Correo",
                "Director",
                "Modalidad",
                "Estado",
                "Mención",
                "Fecha Inicio"
        };

        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        int rowIdx = 1;
        for (DirectorModalitiesDTO row : data) {
            Row r = sheet.createRow(rowIdx++);
            r.createCell(0).setCellValue(row.getStudentName());
            r.createCell(1).setCellValue(row.getStudentEmail());
            r.createCell(2).setCellValue(row.getDirectorName());
            r.createCell(3).setCellValue(row.getModalityName());
            r.createCell(4).setCellValue(row.getModalityStatus());
            r.createCell(5).setCellValue(
                    row.getAcademicDistinction() != null
                            ? row.getAcademicDistinction().name()
                            : ""
            );
            r.createCell(6).setCellValue(
                    row.getStartDate() != null
                            ? row.getStartDate().toString()
                            : ""
            );
        }

        sheet.setAutoFilter(
                new CellRangeAddress(0, rowIdx - 1, 0, headers.length - 1)
        );
        sheet.createFreezePane(0, 1);

        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        workbook.close();

        return ResponseEntity.ok()
                .header(
                        "Content-Disposition",
                        "attachment; filename=modalidades_por_semestre.xlsx"
                )
                .contentType(
                        MediaType.parseMediaType(
                                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
                        )
                )
                .body(out.toByteArray());
    }

    public ResponseEntity<List<DirectorModalitiesDTO>> getStudentsByModalitySemester(ModalityReportDTO filter) {

        if (filter == null || filter.getYear() == null) {
            return ResponseEntity.badRequest().body(List.of());
        }

        LocalDateTime start;
        LocalDateTime end;

        if (filter.getSemester() != null && filter.getSemester() == 1) {
            start = LocalDate.of(filter.getYear(), 1, 1).atStartOfDay();
            end = LocalDate.of(filter.getYear(), 6, 30).atTime(23, 59);
        } else if (filter.getSemester() != null) {
            start = LocalDate.of(filter.getYear(), 7, 1).atStartOfDay();
            end = LocalDate.of(filter.getYear(), 12, 31).atTime(23, 59);
        } else {
            start = LocalDate.of(filter.getYear(), 1, 1).atStartOfDay();
            end = LocalDate.of(filter.getYear(), 12, 31).atTime(23, 59);
        }

        List<StudentModality> modalities =
                studentModalityRepository.findBySelectionDateBetween(start, end);

        // Filtro por estado del proceso
        if (filter.getModalityStatus() != null) {
            modalities = modalities.stream()
                    .filter(sm ->
                            sm.getStatus().name().equals(filter.getModalityStatus())
                    )
                    .toList();
        }

        // Filtro por tipo de modalidad
        if (filter.getModalityType() != null) {
            modalities = modalities.stream()
                    .filter(sm ->
                            sm.getModality().getType().name().equals(filter.getModalityType())
                    )
                    .toList();
        }

        List<DirectorModalitiesDTO> response = modalities.stream()
                .map(sm -> DirectorModalitiesDTO.builder()
                        .studentName(
                                sm.getStudent().getName() + " " +
                                        sm.getStudent().getLastName()
                        )
                        .studentEmail(sm.getStudent().getEmail())
                        .modalityName(sm.getModality().getName())
                        .modalityStatus(sm.getStatus().name())
                        .directorName(
                                sm.getProjectDirector() != null
                                        ? sm.getProjectDirector().getName() + " " +
                                        sm.getProjectDirector().getLastName()
                                        : "Sin director asignado"
                        )
                        .academicDistinction(sm.getAcademicDistinction())
                        .startDate(sm.getSelectionDate())
                        .build()
                )
                .toList();

        return ResponseEntity.ok(response);
    }

    public ResponseEntity<byte[]> exportStudentsByModalitySemesterPdf(ModalityReportDTO filter) throws Exception {

        List<DirectorModalitiesDTO> data =
                getStudentsByModalitySemester(filter).getBody();

        Document document = new Document(PageSize.A4, 50, 50, 60, 50);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, out);
        document.open();

        Font titleFont = new Font(Font.FontFamily.HELVETICA, 15, Font.BOLD);
        Font headerFont = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD);
        Font cellFont = new Font(Font.FontFamily.HELVETICA, 10);
        Font metaFont = new Font(Font.FontFamily.HELVETICA, 9, Font.ITALIC);

        document.add(new Paragraph(
                "REPORTE DE ESTUDIANTES POR MODALIDAD Y SEMESTRE",
                titleFont
        ));

        document.add(new Paragraph(
                "Sistema SIGMA • Fecha: " + LocalDate.now() + "\n\n",
                metaFont
        ));

        PdfPTable table = new PdfPTable(5);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{3f, 3f, 3f, 2f, 2f});

        Stream.of(
                "Estudiante",
                "Correo",
                "Modalidad",
                "Estado",
                "Director"
        ).forEach(h -> {
            PdfPCell cell = new PdfPCell(new Phrase(h, headerFont));
            cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
        });

        for (DirectorModalitiesDTO row : data) {
            table.addCell(new Phrase(row.getStudentName(), cellFont));
            table.addCell(new Phrase(row.getStudentEmail(), cellFont));
            table.addCell(new Phrase(row.getModalityName(), cellFont));
            table.addCell(new Phrase(row.getModalityStatus(), cellFont));
            table.addCell(new Phrase(row.getDirectorName(), cellFont));
        }

        document.add(table);
        document.close();

        return ResponseEntity.ok()
                .header(
                        "Content-Disposition",
                        "attachment; filename=estudiantes_por_modalidad_semestre.pdf"
                )
                .contentType(MediaType.APPLICATION_PDF)
                .body(out.toByteArray());
    }

    public ResponseEntity<byte[]> exportStudentsByModalitySemesterExcel(ModalityReportDTO filter) throws Exception {

        List<DirectorModalitiesDTO> data =
                getStudentsByModalitySemester(filter).getBody();

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Estudiantes por Modalidad");

        Font headerFont = (Font) workbook.createFont();
        headerFont.isBold();

        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setFont((org.apache.poi.ss.usermodel.Font) headerFont);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);

        Row header = sheet.createRow(0);
        String[] columns = {
                "Estudiante",
                "Correo",
                "Modalidad",
                "Estado",
                "Director"
        };

        for (int i = 0; i < columns.length; i++) {
            Cell cell = header.createCell(i);
            cell.setCellValue(columns[i]);
            cell.setCellStyle(headerStyle);
        }

        int rowIdx = 1;
        for (DirectorModalitiesDTO row : data) {
            Row r = sheet.createRow(rowIdx++);
            r.createCell(0).setCellValue(row.getStudentName());
            r.createCell(1).setCellValue(row.getStudentEmail());
            r.createCell(2).setCellValue(row.getModalityName());
            r.createCell(3).setCellValue(row.getModalityStatus());
            r.createCell(4).setCellValue(row.getDirectorName());
        }

        for (int i = 0; i < columns.length; i++) {
            sheet.autoSizeColumn(i);
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        workbook.close();

        return ResponseEntity.ok()
                .header(
                        "Content-Disposition",
                        "attachment; filename=estudiantes_por_modalidad_semestre.xlsx"
                )
                .contentType(
                        MediaType.parseMediaType(
                                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
                        )
                )
                .body(out.toByteArray());
    }

    public ResponseEntity<List<SemesterTypeComparisonDTO>> getModalitiesComparisonBySemesterAndType(ModalityReportDTO filter) {

        if (filter == null || filter.getYear() == null) {
            return ResponseEntity.badRequest().body(List.of());
        }

        LocalDateTime start;
        LocalDateTime end;

        if (filter.getSemester() != null && filter.getSemester() == 1) {
            start = LocalDate.of(filter.getYear(), 1, 1).atStartOfDay();
            end = LocalDate.of(filter.getYear(), 6, 30).atTime(23, 59);
        } else if (filter.getSemester() != null) {
            start = LocalDate.of(filter.getYear(), 7, 1).atStartOfDay();
            end = LocalDate.of(filter.getYear(), 12, 31).atTime(23, 59);
        } else {
            start = LocalDate.of(filter.getYear(), 1, 1).atStartOfDay();
            end = LocalDate.of(filter.getYear(), 12, 31).atTime(23, 59);
        }

        List<StudentModality> modalities =
                studentModalityRepository.findBySelectionDateBetween(start, end);

        Map<String, Set<Long>> grouped =
                modalities.stream()
                        .collect(Collectors.groupingBy(
                                sm -> sm.getModality().getType().name(),
                                Collectors.mapping(
                                        sm -> sm.getStudent().getId(),
                                        Collectors.toSet()
                                )
                        ));

        List<SemesterTypeComparisonDTO> response =
                grouped.entrySet().stream()
                        .map(e -> SemesterTypeComparisonDTO.builder()
                                .year(filter.getYear())
                                .semester(filter.getSemester())
                                .modalityType(ModalityType.valueOf(e.getKey()))
                                .totalStudents((long) e.getValue().size())
                                .build()
                        )
                        .toList();

        return ResponseEntity.ok(response);
    }

    public ResponseEntity<byte[]> exportModalitiesComparisonSemesterTypePdf(ModalityReportDTO filter) throws Exception {

        List<SemesterTypeComparisonDTO> data =
                getModalitiesComparisonBySemesterAndType(filter).getBody();

        Document document = new Document(PageSize.A4, 50, 50, 60, 50);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, out);
        document.open();

        Font titleFont = new Font(Font.FontFamily.HELVETICA, 15, Font.BOLD);
        Font headerFont = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD);
        Font cellFont = new Font(Font.FontFamily.HELVETICA, 10);
        Font metaFont = new Font(Font.FontFamily.HELVETICA, 9, Font.ITALIC);

        document.add(new Paragraph(
                "COMPARATIVA DE MODALIDADES POR SEMESTRE Y TIPO",
                titleFont
        ));

        document.add(new Paragraph(
                "Año: " + filter.getYear() +
                        " | Semestre: " +
                        (filter.getSemester() != null ? filter.getSemester() : "Todos") +
                        "\n\n",
                metaFont
        ));

        PdfPTable table = new PdfPTable(3);
        table.setWidthPercentage(80);
        table.setWidths(new float[]{4f, 2f, 2f});

        Stream.of(
                "Tipo de Modalidad",
                "Semestre",
                "Total Estudiantes"
        ).forEach(h -> {
            PdfPCell cell = new PdfPCell(new Phrase(h, headerFont));
            cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
        });

        for (SemesterTypeComparisonDTO row : data) {
            table.addCell(new Phrase(String.valueOf(row.getModalityType()), cellFont));
            table.addCell(new Phrase(
                    row.getSemester() != null ? row.getSemester().toString() : "—",
                    cellFont
            ));
            table.addCell(new Phrase(
                    row.getTotalStudents().toString(),
                    cellFont
            ));
        }

        document.add(table);
        document.close();

        return ResponseEntity.ok()
                .header(
                        "Content-Disposition",
                        "attachment; filename=comparativa_semestre_tipo.pdf"
                )
                .contentType(MediaType.APPLICATION_PDF)
                .body(out.toByteArray());
    }

    public ResponseEntity<byte[]> exportModalitiesComparisonSemesterTypeExcel(ModalityReportDTO filter) throws Exception {

        List<SemesterTypeComparisonDTO> data =
                getModalitiesComparisonBySemesterAndType(filter).getBody();

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Comparativa Semestre-Tipo");

        Font headerFont = (Font) workbook.createFont();
        headerFont.isBold();

        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setFont((org.apache.poi.ss.usermodel.Font) headerFont);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);

        Row header = sheet.createRow(0);
        String[] columns = {
                "Tipo de Modalidad",
                "Semestre",
                "Total Estudiantes"
        };

        for (int i = 0; i < columns.length; i++) {
            Cell cell = header.createCell(i);
            cell.setCellValue(columns[i]);
            cell.setCellStyle(headerStyle);
        }

        int rowIdx = 1;
        for (SemesterTypeComparisonDTO row : data) {
            Row r = sheet.createRow(rowIdx++);
            r.createCell(0).setCellValue(row.getModalityType().ordinal());
            r.createCell(1).setCellValue(
                    row.getSemester() != null ? row.getSemester() : 0
            );
            r.createCell(2).setCellValue(row.getTotalStudents());
        }

        for (int i = 0; i < columns.length; i++) {
            sheet.autoSizeColumn(i);
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        workbook.close();

        return ResponseEntity.ok()
                .header(
                        "Content-Disposition",
                        "attachment; filename=comparativa_semestre_tipo.xlsx"
                )
                .contentType(
                        MediaType.parseMediaType(
                                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
                        )
                )
                .body(out.toByteArray());
    }

}

package com.SIGMA.USCO.report.controller;

import com.SIGMA.USCO.report.dto.DirectorModalitiesDTO;
import com.SIGMA.USCO.report.dto.ModalityReportDTO;
import com.SIGMA.USCO.report.dto.SemesterModalitiesFilterDTO;
import com.SIGMA.USCO.report.dto.SemesterTypeComparisonDTO;
import com.SIGMA.USCO.report.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/reports")
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/modalities-active")
    @PreAuthorize("hasAuthority('PERM_VIEW_REPORTS')")
    public ResponseEntity<?> viewActiveModalitiesReport() {
        return ResponseEntity.ok(reportService.buildActiveModalitiesReport());
    }

    @GetMapping("/modalities-active/pdf")
    @PreAuthorize("hasAuthority('PERM_VIEW_REPORTS')")
    public ResponseEntity<byte[]> exportPdf() throws Exception {

        byte[] pdf = reportService.exportActiveModalitiesPdf();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=reporte_modalidades_activas.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    @GetMapping("/modalities-active/excel")
    @PreAuthorize("hasAuthority('PERM_VIEW_REPORTS')")
    public ResponseEntity<byte[]> exportExcel() throws Exception {

        byte[] excel = reportService.exportActiveModalitiesExcel();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=reporte_modalidades_activas.xlsx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(excel);
    }

    @PostMapping("/modality-type")
    @PreAuthorize("hasAuthority('PERM_VIEW_REPORTS')")
    public ResponseEntity<?> getReport(@RequestBody(required = false) ModalityReportDTO filter) {
        return reportService.getReportByModalityType(filter);
    }

    @PostMapping("/modality-type/pdf")
    @PreAuthorize("hasAuthority('PERM_VIEW_REPORTS')")
    public ResponseEntity<byte[]> exportPdf(@RequestBody(required = false) ModalityReportDTO filter) throws Exception {
        return reportService.exportModalitiesByTypePdf(filter);
    }

    @PostMapping("/modality-type/excel")
    @PreAuthorize("hasAuthority('PERM_VIEW_REPORTS')")
    public ResponseEntity<byte[]> exportExcel(@RequestBody(required = false) ModalityReportDTO filter) throws Exception {
        return reportService.exportModalitiesByTypeExcel(filter);
    }

    @PostMapping ("/modality-comparison")
    @PreAuthorize("hasAuthority('PERM_VIEW_REPORTS')")
    public ResponseEntity<List<ModalityReportDTO>> compare(@RequestBody(required = false) ModalityReportDTO filter
    ) {
        return reportService.getModalitiesComparisonByType(filter);
    }

    @PostMapping("/modality-comparison/pdf")
    @PreAuthorize("hasAuthority('PERM_VIEW_REPORTS')")
    public ResponseEntity<byte[]> pdfCompare(@RequestBody(required = false) ModalityReportDTO filter) throws Exception {
        return reportService.exportModalitiesComparisonPdf(filter);
    }

    @PostMapping("/modality-comparison/excel")
    @PreAuthorize("hasAuthority('PERM_VIEW_REPORTS')")
    public ResponseEntity<byte[]> excelCompare(@RequestBody(required = false) ModalityReportDTO filter) throws Exception {
        return reportService.exportModalitiesComparisonExcel(filter);
    }

    @GetMapping("/director/{directorId}")
    @PreAuthorize("hasAuthority('PERM_VIEW_REPORTS')")
    public ResponseEntity<?> getDirector(@PathVariable Long directorId, @RequestParam(required = false) String status
    ) {
        return reportService.getModalitiesByDirector(directorId, status);
    }

    @GetMapping("/director/{directorId}/pdf")
    @PreAuthorize("hasAuthority('PERM_VIEW_REPORTS')")
    public ResponseEntity<byte[]> exportPdfDirector(@PathVariable Long directorId, @RequestParam(required = false) String status
    ) throws Exception {
        return reportService.exportModalitiesByDirectorPdf(directorId, status);
    }

    @GetMapping("/director/{directorId}/excel")
    @PreAuthorize("hasAuthority('PERM_VIEW_REPORTS')")
    public ResponseEntity<byte[]> exportExcelDirector(@PathVariable Long directorId, @RequestParam(required = false) String status) throws Exception {
        return reportService.exportModalitiesByDirectorExcel(directorId, status);
    }

    @PostMapping("/semester")
    @PreAuthorize("hasAuthority('PERM_VIEW_REPORTS')")
    public ResponseEntity<List<DirectorModalitiesDTO>> getModalitiesBySemester(@RequestBody ModalityReportDTO filter) {
        return reportService.getModalitiesBySemester(filter);
    }
    @PostMapping("/semester/pdf")
    @PreAuthorize("hasAuthority('PERM_VIEW_REPORTS')")
    public ResponseEntity<byte[]> exportSemesterPdf(@RequestBody ModalityReportDTO filter) throws Exception {
        return reportService.exportModalitiesBySemesterPdf(filter);
    }
    @PostMapping("/semester/excel")
    @PreAuthorize("hasAuthority('PERM_VIEW_REPORTS')")
    public ResponseEntity<byte[]> exportSemesterExcel(@RequestBody ModalityReportDTO filter
    ) throws Exception {
        return reportService.exportModalitiesBySemesterExcel(filter);
    }

    @PostMapping("/semester/students")
    @PreAuthorize("hasAuthority('PERM_VIEW_REPORTS')")
    public ResponseEntity<List<DirectorModalitiesDTO>> studentsBySemester(@RequestBody ModalityReportDTO filter) {
        return reportService.getStudentsByModalitySemester(filter);
    }

    @PostMapping("/semester/students/pdf")
    @PreAuthorize("hasAuthority('PERM_VIEW_REPORTS')")
    public ResponseEntity<byte[]> studentsBySemesterPdf(@RequestBody ModalityReportDTO filter) throws Exception {
        return reportService.exportStudentsByModalitySemesterPdf(filter);
    }

    @PostMapping("/semester/students/excel")
    @PreAuthorize("hasAuthority('PERM_VIEW_REPORTS')")
    public ResponseEntity<byte[]> studentsBySemesterExcel(@RequestBody ModalityReportDTO filter
    ) throws Exception {
        return reportService.exportStudentsByModalitySemesterExcel(filter);
    }

    @PostMapping("/semester-type-comparison")
    @PreAuthorize("hasAuthority('PERM_VIEW_REPORTS')")
    public ResponseEntity<List<SemesterTypeComparisonDTO>> compareSemesterType(@RequestBody ModalityReportDTO filter) {
        return reportService.getModalitiesComparisonBySemesterAndType(filter);
    }

    @PostMapping("/semester-type-comparison/pdf")
    @PreAuthorize("hasAuthority('PERM_VIEW_REPORTS')")
    public ResponseEntity<byte[]> compareSemesterTypePdf(@RequestBody ModalityReportDTO filter) throws Exception {
        return reportService.exportModalitiesComparisonSemesterTypePdf(filter);
    }

    @PostMapping("/semester-type-comparison/excel")
    @PreAuthorize("hasAuthority('PERM_VIEW_REPORTS')")
    public ResponseEntity<byte[]> compareSemesterTypeExcel(@RequestBody ModalityReportDTO filter) throws Exception {
        return reportService.exportModalitiesComparisonSemesterTypeExcel(filter);
    }




}

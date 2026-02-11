package com.SIGMA.USCO.notifications.service;

import com.SIGMA.USCO.Modalities.Entity.AcademicCertificate;
import com.SIGMA.USCO.Modalities.Entity.StudentModality;
import com.SIGMA.USCO.Modalities.Entity.enums.AcademicDistinction;
import com.SIGMA.USCO.Modalities.Entity.enums.CertificateStatus;
import com.SIGMA.USCO.Modalities.Repository.AcademicCertificateRepository;
import com.SIGMA.USCO.Users.Entity.User;
import com.SIGMA.USCO.academic.entity.StudentProfile;
import com.SIGMA.USCO.academic.repository.StudentProfileRepository;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
@Slf4j
public class AcademicCertificatePdfService {

    private final AcademicCertificateRepository certificateRepository;
    private final StudentProfileRepository studentProfileRepository;

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Transactional
    public AcademicCertificate generateCertificate(StudentModality studentModality) throws IOException {


        if (certificateRepository.existsByStudentModalityId(studentModality.getId())) {
            log.info("Ya existe un certificado para la modalidad {}. Regenerando con diseño actualizado...",
                    studentModality.getId());


            AcademicCertificate existingCertificate = certificateRepository
                    .findByStudentModalityId(studentModality.getId())
                    .orElseThrow();


            try {
                Path oldFilePath = Paths.get(existingCertificate.getFilePath());
                if (Files.exists(oldFilePath)) {
                    Files.delete(oldFilePath);
                    log.info("Archivo PDF antiguo eliminado: {}", oldFilePath);
                }
            } catch (IOException e) {
                log.warn("No se pudo eliminar el archivo PDF antiguo: {}", e.getMessage());
            }


            certificateRepository.delete(existingCertificate);
            log.info("Registro de certificado antiguo eliminado de BD");
        }

        User student = studentModality.getStudent();
        String certificateNumber = generateCertificateNumber(studentModality);

        Path certificatesPath = Paths.get(uploadDir, "certificates",
                String.valueOf(studentModality.getProgramDegreeModality().getAcademicProgram().getId()));
        Files.createDirectories(certificatesPath);

        String fileName = "ACTA_" + certificateNumber + "_" + student.getId() + ".pdf";
        Path filePath = certificatesPath.resolve(fileName);


        generatePdfDocument(filePath, studentModality, certificateNumber);
        log.info("Nuevo certificado PDF generado: {}", filePath);

        String fileHash = calculateFileHash(filePath);

        AcademicCertificate certificate = AcademicCertificate.builder()
                .studentModality(studentModality)
                .certificateNumber(certificateNumber)
                .issueDate(LocalDateTime.now())
                .filePath(filePath.toString())
                .fileHash(fileHash)
                .status(CertificateStatus.GENERATED)
                .build();

        return certificateRepository.save(certificate);
    }

    private void generatePdfDocument(Path filePath, StudentModality studentModality, String certificateNumber) {
        try {

            Document document = new Document(PageSize.A4, 50, 50, 60, 60);
            PdfWriter.getInstance(document, new FileOutputStream(filePath.toFile()));
            document.open();

            User student = studentModality.getStudent();
            User director = studentModality.getProjectDirector();


            BaseColor institutionalBlue = new BaseColor(0, 51, 102); // Azul institucional
            BaseColor accentGray = new BaseColor(80, 80, 80);

            Font titleMainFont = new Font(Font.FontFamily.HELVETICA, 22, Font.BOLD, institutionalBlue);
            Font institutionFont = new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD, BaseColor.BLACK);
            Font facultyFont = new Font(Font.FontFamily.HELVETICA, 13, Font.BOLD, accentGray);
            Font programFont = new Font(Font.FontFamily.HELVETICA, 11, Font.NORMAL, accentGray);
            Font sectionTitleFont = new Font(Font.FontFamily.HELVETICA, 13, Font.BOLD, institutionalBlue);
            Font actaNumberFont = new Font(Font.FontFamily.HELVETICA, 10, Font.ITALIC, accentGray);
            Font labelFont = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD, BaseColor.BLACK);
            Font dataFont = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL, BaseColor.BLACK);
            Font bodyFont = new Font(Font.FontFamily.HELVETICA, 11, Font.NORMAL, BaseColor.BLACK);
            Font footerFont = new Font(Font.FontFamily.HELVETICA, 8, Font.ITALIC, BaseColor.GRAY);
            Font signatureFont = new Font(Font.FontFamily.HELVETICA, 9, Font.BOLD, BaseColor.BLACK);
            Font signatureLabelFont = new Font(Font.FontFamily.HELVETICA, 8, Font.NORMAL, accentGray);




            Paragraph institution = new Paragraph("UNIVERSIDAD SURCOLOMBIANA", institutionFont);
            institution.setAlignment(Element.ALIGN_CENTER);
            institution.setSpacingAfter(8);
            document.add(institution);


            Paragraph faculty = new Paragraph(
                    studentModality.getProgramDegreeModality().getAcademicProgram().getFaculty().getName().toUpperCase(),
                    facultyFont
            );
            faculty.setAlignment(Element.ALIGN_CENTER);
            faculty.setSpacingAfter(5);
            document.add(faculty);


            Paragraph program = new Paragraph(
                    studentModality.getProgramDegreeModality().getAcademicProgram().getName(),
                    programFont
            );
            program.setAlignment(Element.ALIGN_CENTER);
            program.setSpacingAfter(15);
            document.add(program);


            addHorizontalLine(document, institutionalBlue);
            document.add(Chunk.NEWLINE);



            Paragraph mainTitle = new Paragraph("ACTA DE APROBACIÓN", titleMainFont);
            mainTitle.setAlignment(Element.ALIGN_CENTER);
            mainTitle.setSpacingAfter(5);
            document.add(mainTitle);

            Paragraph subtitle = new Paragraph("MODALIDAD DE GRADO", titleMainFont);
            subtitle.setAlignment(Element.ALIGN_CENTER);
            subtitle.setSpacingAfter(10);
            document.add(subtitle);


            Paragraph actaNum = new Paragraph("Acta No. " + certificateNumber, actaNumberFont);
            actaNum.setAlignment(Element.ALIGN_CENTER);
            actaNum.setSpacingAfter(25);
            document.add(actaNum);



            Paragraph resultSection = new Paragraph("RESULTADO DE LA EVALUACIÓN", sectionTitleFont);
            resultSection.setSpacingBefore(10);
            resultSection.setSpacingAfter(12);
            document.add(resultSection);

            Paragraph resultText = new Paragraph(
                    "Una vez realizada la sustentación correspondiente y efectuada la evaluación " +
                    "por parte de los jurados designados, el Comité de Currículo del programa académico " +
                    "certifica que el(la) estudiante que a continuación se identifica ha APROBADO " +
                    "satisfactoriamente su modalidad de grado, cumpliendo con los requisitos académicos " +
                    "y normativos establecidos por la Universidad Surcolombiana.",
                    bodyFont
            );
            resultText.setAlignment(Element.ALIGN_JUSTIFIED);
            resultText.setSpacingAfter(20);
            document.add(resultText);



            Paragraph studentSection = new Paragraph("DATOS DEL ESTUDIANTE", sectionTitleFont);
            studentSection.setSpacingAfter(10);
            document.add(studentSection);

            String studentCode = "No registrado";
            try {
                StudentProfile profile = studentProfileRepository.findByUserId(student.getId()).orElse(null);
                if (profile != null && profile.getStudentCode() != null) {
                    studentCode = profile.getStudentCode();
                }
            } catch (Exception e) {
                log.warn("No se pudo obtener el código del estudiante: {}", e.getMessage());
            }

            PdfPTable studentTable = createStyledDataTable(2);
            addStyledTableRow(studentTable, "Nombre completo:",
                    student.getName() + " " + student.getLastName(), labelFont, dataFont);
            addStyledTableRow(studentTable, "Código estudiantil:", studentCode, labelFont, dataFont);
            addStyledTableRow(studentTable, "Correo electrónico:", student.getEmail(), labelFont, dataFont);

            studentTable.setSpacingAfter(20);
            document.add(studentTable);



            Paragraph modalitySection = new Paragraph("INFORMACIÓN DE LA MODALIDAD", sectionTitleFont);
            modalitySection.setSpacingAfter(10);
            document.add(modalitySection);

            PdfPTable modalityTable = createStyledDataTable(2);
            addStyledTableRow(modalityTable, "Modalidad de grado:",
                    studentModality.getProgramDegreeModality().getDegreeModality().getName(),
                    labelFont, dataFont);
            addStyledTableRow(modalityTable, "Director de proyecto:",
                    director != null ? director.getName() + " " + director.getLastName() : "No asignado",
                    labelFont, dataFont);
            addStyledTableRow(modalityTable, "Fecha de sustentación:",
                    studentModality.getDefenseDate() != null ?
                            studentModality.getDefenseDate().format(DateTimeFormatter.ofPattern("dd 'de' MMMM 'de' yyyy, HH:mm"))
                            : "No registrada",
                    labelFont, dataFont);


            AcademicDistinction distinction = studentModality.getAcademicDistinction();
            if (distinction != null && distinction != AcademicDistinction.NO_DISTINCTION) {
                addStyledTableRow(modalityTable, "Mención académica:",
                        translateDistinction(distinction), labelFont,
                        new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD, new BaseColor(0, 100, 0)));
            }

            modalityTable.setSpacingAfter(25);
            document.add(modalityTable);



            Paragraph issueDateParagraph = new Paragraph();
            issueDateParagraph.add(new Chunk("Fecha de emisión: ", labelFont));
            issueDateParagraph.add(new Chunk(
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd 'de' MMMM 'de' yyyy")),
                    dataFont
            ));
            issueDateParagraph.setAlignment(Element.ALIGN_RIGHT);
            issueDateParagraph.setSpacingBefore(10);
            issueDateParagraph.setSpacingAfter(40);
            document.add(issueDateParagraph);



            PdfPTable signaturesTable = new PdfPTable(2);
            signaturesTable.setWidthPercentage(100);
            signaturesTable.setSpacingBefore(20);
            signaturesTable.setSpacingAfter(30);


            PdfPCell signCell1 = new PdfPCell();
            signCell1.setBorder(Rectangle.NO_BORDER);
            signCell1.setHorizontalAlignment(Element.ALIGN_CENTER);
            signCell1.setPaddingTop(20);

            Paragraph sign1 = new Paragraph();
            sign1.add(new Chunk("\n\n\n\n", dataFont)); // Espacio para firma manuscrita
            sign1.add(new Chunk("_________________________________\n", dataFont));
            sign1.add(new Chunk("Firma\n", signatureFont));
            sign1.add(new Chunk("Comité de Currículo\n", signatureLabelFont));
            sign1.add(new Chunk(studentModality.getProgramDegreeModality().getAcademicProgram().getName(),
                    footerFont));
            sign1.setAlignment(Element.ALIGN_CENTER);
            signCell1.addElement(sign1);
            signaturesTable.addCell(signCell1);


            PdfPCell signCell2 = new PdfPCell();
            signCell2.setBorder(Rectangle.NO_BORDER);
            signCell2.setHorizontalAlignment(Element.ALIGN_CENTER);
            signCell2.setPaddingTop(20);

            Paragraph sign2 = new Paragraph();
            sign2.add(new Chunk("\n\n\n\n", dataFont));
            sign2.add(new Chunk("_________________________________\n", dataFont));
            sign2.add(new Chunk("Firma\n", signatureFont));
            sign2.add(new Chunk("Jefatura de Programa\n", signatureLabelFont));
            sign2.add(new Chunk(studentModality.getProgramDegreeModality().getAcademicProgram().getName(),
                    footerFont));
            sign2.setAlignment(Element.ALIGN_CENTER);
            signCell2.addElement(sign2);
            signaturesTable.addCell(signCell2);

            document.add(signaturesTable);




            addHorizontalLine(document, BaseColor.LIGHT_GRAY);

            Paragraph legalNote = new Paragraph(
                    "Este documento certifica la aprobación oficial de la modalidad de grado " +
                    "conforme a la normatividad académica vigente de la Universidad Surcolombiana. " +
                    "La información contenida en este acta es auténtica y verificable.",
                    footerFont
            );
            legalNote.setAlignment(Element.ALIGN_CENTER);
            legalNote.setSpacingBefore(10);
            legalNote.setSpacingAfter(8);
            document.add(legalNote);

            Paragraph verificationCode = new Paragraph(
                    "Código de verificación: " + certificateNumber,
                    new Font(Font.FontFamily.COURIER, 8, Font.BOLD, accentGray)
            );
            verificationCode.setAlignment(Element.ALIGN_CENTER);
            document.add(verificationCode);

            document.close();
            log.info("PDF generado exitosamente en: {}", filePath);

        } catch (DocumentException | IOException e) {
            log.error("Error generando PDF: {}", e.getMessage(), e);
            throw new RuntimeException("Error al generar el certificado PDF", e);
        }
    }


    private PdfPTable createStyledDataTable(int columns) {
        PdfPTable table = new PdfPTable(columns);
        table.setWidthPercentage(100);
        table.setSpacingBefore(5);
        table.setSpacingAfter(5);
        return table;
    }


    private void addStyledTableRow(PdfPTable table, String label, String value, Font labelFont, Font valueFont) {

        PdfPCell labelCell = new PdfPCell(new Phrase(label, labelFont));
        labelCell.setBorder(Rectangle.NO_BORDER);
        labelCell.setBackgroundColor(new BaseColor(245, 245, 245)); // Fondo gris claro
        labelCell.setPadding(8);
        labelCell.setHorizontalAlignment(Element.ALIGN_LEFT);
        table.addCell(labelCell);


        PdfPCell valueCell = new PdfPCell(new Phrase(value, valueFont));
        valueCell.setBorder(Rectangle.NO_BORDER);
        valueCell.setPadding(8);
        valueCell.setHorizontalAlignment(Element.ALIGN_LEFT);
        table.addCell(valueCell);
    }


    private void addHorizontalLine(Document document, BaseColor color) throws DocumentException {
        PdfPTable lineTable = new PdfPTable(1);
        lineTable.setWidthPercentage(100);
        lineTable.setSpacingBefore(5);
        lineTable.setSpacingAfter(5);

        PdfPCell lineCell = new PdfPCell();
        lineCell.setBorder(Rectangle.NO_BORDER);
        lineCell.setBorderWidthBottom(1.5f);
        lineCell.setBorderColorBottom(color);
        lineCell.setFixedHeight(2f);

        lineTable.addCell(lineCell);
        document.add(lineTable);
    }

    private String generateCertificateNumber(StudentModality studentModality) {
        Long programId = studentModality.getProgramDegreeModality().getAcademicProgram().getId();
        int year = LocalDateTime.now().getYear();

        long count = certificateRepository.findAll().stream()
                .filter(cert -> cert.getStudentModality()
                        .getProgramDegreeModality()
                        .getAcademicProgram()
                        .getId().equals(programId))
                .filter(cert -> cert.getIssueDate().getYear() == year)
                .count();

        return String.format("ACTA-PROG%d-%d-%04d", programId, year, count + 1);
    }

    private String calculateFileHash(Path filePath) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] fileBytes = Files.readAllBytes(filePath);
            byte[] hashBytes = digest.digest(fileBytes);

            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException | IOException e) {
            log.error("Error calculando hash del archivo: {}", e.getMessage());
            return "";
        }
    }

    private String translateDistinction(AcademicDistinction distinction) {
        if (distinction == null) {
            return "Sin distinción";
        }

        return switch (distinction) {

            case AGREED_APPROVED -> "Aprobado sin distinción";
            case AGREED_MERITORIOUS -> "Aprobado con mención meritoria";
            case AGREED_LAUREATE -> "Aprobado con mención laureada";
            case AGREED_REJECTED -> "Rechazado";

            // Desacuerdo pendiente
            case DISAGREEMENT_PENDING_TIEBREAKER -> "En proceso de evaluación por desempate";


            case TIEBREAKER_APPROVED -> "Aprobado sin distinción (por desempate)";
            case TIEBREAKER_MERITORIOUS -> "Aprobado con mención meritoria (por desempate)";
            case TIEBREAKER_LAUREATE -> "Aprobado con mención laureada (por desempate)";
            case TIEBREAKER_REJECTED -> "Rechazado (por desempate)";


            case NO_DISTINCTION -> "Sin distinción";
            default -> "Sin distinción";
        };
    }

    public Path getCertificatePath(Long studentModalityId) {
        AcademicCertificate certificate = certificateRepository.findByStudentModalityId(studentModalityId)
                .orElseThrow(() -> new RuntimeException("Certificado no encontrado"));
        return Paths.get(certificate.getFilePath());
    }

    @Transactional
    public void updateCertificateStatus(Long certificateId, CertificateStatus status) {
        AcademicCertificate certificate = certificateRepository.findById(certificateId)
                .orElseThrow(() -> new RuntimeException("Certificado no encontrado"));
        certificate.setStatus(status);
        certificateRepository.save(certificate);
    }
}


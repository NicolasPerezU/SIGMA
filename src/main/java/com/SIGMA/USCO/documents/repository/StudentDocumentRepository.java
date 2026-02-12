package com.SIGMA.USCO.documents.repository;

import com.SIGMA.USCO.documents.entity.DocumentType;
import com.SIGMA.USCO.documents.entity.StudentDocument;
import com.SIGMA.USCO.documents.entity.StudentDocumentStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StudentDocumentRepository extends JpaRepository<StudentDocument, Long> {
    List<StudentDocument> findByStudentModalityId(Long studentModalityId);

    Optional<StudentDocument> findByStudentModalityIdAndDocumentConfigId(
            Long modalityId,
            Long documentConfigId
    );

    Optional<StudentDocument> findByStudentModalityIdAndDocumentConfig_DocumentName(Long studentModalityId, String documentName);

    /**
     * Busca documentos por modalidad del estudiante y tipo de documento
     *
     * @param studentModalityId ID de la modalidad del estudiante
     * @param documentType Tipo de documento (MANDATORY, SECONDARY, CANCELLATION)
     * @return Lista de documentos que coinciden con el tipo especificado
     */
    List<StudentDocument> findByStudentModalityIdAndDocumentConfig_DocumentType(
            Long studentModalityId,
            DocumentType documentType
    );

}

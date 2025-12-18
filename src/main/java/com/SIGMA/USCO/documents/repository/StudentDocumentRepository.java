package com.SIGMA.USCO.documents.repository;

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


}

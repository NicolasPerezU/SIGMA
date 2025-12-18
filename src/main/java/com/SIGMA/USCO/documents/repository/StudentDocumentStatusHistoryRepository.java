package com.SIGMA.USCO.documents.repository;

import com.SIGMA.USCO.documents.entity.StudentDocumentStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StudentDocumentStatusHistoryRepository extends JpaRepository<StudentDocumentStatusHistory, Long> {

    List<StudentDocumentStatusHistory>
    findByStudentDocumentIdOrderByChangeDateAsc(Long studentDocumentId);
}

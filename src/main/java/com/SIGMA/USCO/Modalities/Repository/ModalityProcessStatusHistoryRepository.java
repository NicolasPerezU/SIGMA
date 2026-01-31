package com.SIGMA.USCO.Modalities.Repository;

import com.SIGMA.USCO.Modalities.Entity.ModalityProcessStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Arrays;
import java.util.List;

public interface ModalityProcessStatusHistoryRepository extends JpaRepository<ModalityProcessStatusHistory, Long> {

    List<ModalityProcessStatusHistory> findByStudentModalityIdOrderByChangeDateAsc(Long studentModalityId);


    Arrays findByStudentModalityIdOrderByChangeDateDesc(Long id);
}

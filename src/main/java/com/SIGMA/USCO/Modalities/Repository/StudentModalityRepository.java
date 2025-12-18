package com.SIGMA.USCO.Modalities.Repository;

import com.SIGMA.USCO.Modalities.Entity.ModalityProcessStatus;
import com.SIGMA.USCO.Modalities.Entity.StudentModality;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StudentModalityRepository extends JpaRepository<StudentModality, Long> {


    Optional<StudentModality> findByStudentIdAndStatus(Long studentId, ModalityProcessStatus status);


    List<StudentModality> findByStudentId(Long studentId);


    List<StudentModality> findByStatus(ModalityProcessStatus status);

    boolean existsByStudentIdAndStatusIn(Long studentId, List<ModalityProcessStatus> statuses);

    boolean existsByStudent_IdAndModality_Id(Long studentId, Long modalityId);

    Optional<StudentModality> findTopByStudentIdOrderByUpdatedAtDesc(Long studentId);


}

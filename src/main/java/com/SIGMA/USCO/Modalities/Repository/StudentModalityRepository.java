package com.SIGMA.USCO.Modalities.Repository;

import com.SIGMA.USCO.Modalities.Entity.enums.ModalityProcessStatus;
import com.SIGMA.USCO.Modalities.Entity.StudentModality;
import com.SIGMA.USCO.Modalities.Entity.enums.ModalityStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface StudentModalityRepository extends JpaRepository<StudentModality, Long> {


    Optional<StudentModality> findByStudentIdAndStatus(Long studentId, ModalityProcessStatus status);


    List<StudentModality> findByStudentId(Long studentId);


    List<StudentModality> findByStatus(ModalityProcessStatus status);

    boolean existsByStudentIdAndStatusIn(Long studentId, List<ModalityProcessStatus> statuses);

    boolean existsByStudent_IdAndModality_Id(Long studentId, Long modalityId);

    Optional<StudentModality> findTopByStudentIdOrderByUpdatedAtDesc(Long studentId);

    List<StudentModality> findByModality_Status(ModalityStatus status);

    List<StudentModality> findBySelectionDateBetween(LocalDateTime start, LocalDateTime end);


    List<StudentModality> findByProjectDirector_Id(Long directorId);

    List<StudentModality> findByProjectDirector_IdAndSelectionDateBetween(Long directorId, LocalDateTime localDateTime, LocalDateTime localDateTime1);



}

package com.SIGMA.USCO.Modalities.Repository;

import com.SIGMA.USCO.Modalities.Entity.ExaminerEvaluation;
import com.SIGMA.USCO.Modalities.Entity.enums.ExaminerDecision;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExaminerEvaluationRepository extends JpaRepository<ExaminerEvaluation, Long> {


    Optional<ExaminerEvaluation> findByDefenseExaminerId(Long defenseExaminerId);


    @Query("SELECT ee FROM ExaminerEvaluation ee " +
           "JOIN ee.defenseExaminer de " +
           "WHERE de.studentModality.id = :studentModalityId " +
           "ORDER BY ee.evaluationDate ASC")
    List<ExaminerEvaluation> findByStudentModalityId(@Param("studentModalityId") Long studentModalityId);


    boolean existsByDefenseExaminerId(Long defenseExaminerId);


    @Query("SELECT COUNT(ee) FROM ExaminerEvaluation ee " +
           "JOIN ee.defenseExaminer de " +
           "WHERE de.studentModality.id = :studentModalityId")
    long countByStudentModalityId(@Param("studentModalityId") Long studentModalityId);


    @Query("SELECT ee FROM ExaminerEvaluation ee " +
           "JOIN ee.defenseExaminer de " +
           "WHERE de.studentModality.id = :studentModalityId " +
           "AND de.examinerType IN ('PRIMARY_EXAMINER_1', 'PRIMARY_EXAMINER_2') " +
           "ORDER BY de.examinerType ASC")
    List<ExaminerEvaluation> findPrimaryEvaluationsByStudentModalityId(@Param("studentModalityId") Long studentModalityId);


    @Query("SELECT CASE WHEN COUNT(ee) = 2 THEN true ELSE false END " +
           "FROM ExaminerEvaluation ee " +
           "JOIN ee.defenseExaminer de " +
           "WHERE de.studentModality.id = :studentModalityId " +
           "AND de.examinerType IN ('PRIMARY_EXAMINER_1', 'PRIMARY_EXAMINER_2')")
    boolean bothPrimaryExaminersHaveEvaluated(@Param("studentModalityId") Long studentModalityId);


    @Query("SELECT ee FROM ExaminerEvaluation ee " +
           "JOIN ee.defenseExaminer de " +
           "WHERE de.studentModality.id = :studentModalityId " +
           "AND de.examinerType = 'TIEBREAKER_EXAMINER'")
    Optional<ExaminerEvaluation> findTiebreakerEvaluationByStudentModalityId(@Param("studentModalityId") Long studentModalityId);


    @Query("SELECT CASE WHEN COUNT(ee) > 0 THEN true ELSE false END " +
           "FROM ExaminerEvaluation ee " +
           "JOIN ee.defenseExaminer de " +
           "WHERE de.studentModality.id = :studentModalityId " +
           "AND de.examinerType = 'TIEBREAKER_EXAMINER'")
    boolean tiebreakerHasEvaluated(@Param("studentModalityId") Long studentModalityId);


    @Query("SELECT ee FROM ExaminerEvaluation ee " +
           "WHERE ee.decision = :decision " +
           "ORDER BY ee.evaluationDate DESC")
    List<ExaminerEvaluation> findByDecision(@Param("decision") ExaminerDecision decision);


    @Query("SELECT AVG(ee.grade) FROM ExaminerEvaluation ee " +
           "JOIN ee.defenseExaminer de " +
           "WHERE de.studentModality.id = :studentModalityId " +
           "AND de.examinerType IN ('PRIMARY_EXAMINER_1', 'PRIMARY_EXAMINER_2')")
    Double calculateAverageGradeOfPrimaryExaminers(@Param("studentModalityId") Long studentModalityId);


    @Query("SELECT MIN(ee.grade) FROM ExaminerEvaluation ee " +
           "JOIN ee.defenseExaminer de " +
           "WHERE de.studentModality.id = :studentModalityId " +
           "AND de.examinerType IN ('PRIMARY_EXAMINER_1', 'PRIMARY_EXAMINER_2')")
    Double findMinimumGradeOfPrimaryExaminers(@Param("studentModalityId") Long studentModalityId);


    @Query("SELECT MAX(ee.grade) FROM ExaminerEvaluation ee " +
           "JOIN ee.defenseExaminer de " +
           "WHERE de.studentModality.id = :studentModalityId " +
           "AND de.examinerType IN ('PRIMARY_EXAMINER_1', 'PRIMARY_EXAMINER_2')")
    Double findMaximumGradeOfPrimaryExaminers(@Param("studentModalityId") Long studentModalityId);


    @Query("SELECT ee FROM ExaminerEvaluation ee " +
           "JOIN ee.defenseExaminer de " +
           "WHERE de.examiner.id = :examinerId " +
           "ORDER BY ee.evaluationDate DESC")
    List<ExaminerEvaluation> findByExaminerId(@Param("examinerId") Long examinerId);


    @Query("SELECT COUNT(ee) FROM ExaminerEvaluation ee " +
           "JOIN ee.defenseExaminer de " +
           "WHERE de.examiner.id = :examinerId")
    long countByExaminerId(@Param("examinerId") Long examinerId);


    @Query("SELECT ee FROM ExaminerEvaluation ee " +
           "WHERE ee.decision = 'REJECTED' " +
           "ORDER BY ee.evaluationDate DESC")
    List<ExaminerEvaluation> findAllRejectedEvaluations();


    @Query("SELECT ee FROM ExaminerEvaluation ee " +
           "WHERE ee.isFinalDecision = true " +
           "ORDER BY ee.evaluationDate DESC")
    List<ExaminerEvaluation> findAllFinalDecisions();


    @Query("SELECT CASE WHEN COUNT(DISTINCT ee.decision) = 1 AND COUNT(ee) = 2 " +
           "THEN true ELSE false END " +
           "FROM ExaminerEvaluation ee " +
           "JOIN ee.defenseExaminer de " +
           "WHERE de.studentModality.id = :studentModalityId " +
           "AND de.examinerType IN ('PRIMARY_EXAMINER_1', 'PRIMARY_EXAMINER_2')")
    boolean primaryExaminersHaveConsensus(@Param("studentModalityId") Long studentModalityId);


    @Query("SELECT AVG(ee.grade) FROM ExaminerEvaluation ee " +
           "JOIN ee.defenseExaminer de " +
           "WHERE de.examiner.id = :examinerId")
    Double calculateAverageGradeByExaminer(@Param("examinerId") Long examinerId);
}


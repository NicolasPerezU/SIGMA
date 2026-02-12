package com.SIGMA.USCO.Modalities.Repository;

import com.SIGMA.USCO.Modalities.Entity.enums.ModalityProcessStatus;
import com.SIGMA.USCO.Modalities.Entity.StudentModality;
import com.SIGMA.USCO.Modalities.Entity.enums.ModalityStatus;
import com.SIGMA.USCO.Users.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface StudentModalityRepository extends JpaRepository<StudentModality, Long> {


    List<StudentModality> findByStatus(ModalityProcessStatus status);

    List<StudentModality> findByStatusIn(List<ModalityProcessStatus> statuses);


    boolean existsByStudentIdAndStatusIn(Long studentId, List<ModalityProcessStatus> statuses);

    boolean existsByStudent_IdAndProgramDegreeModality_DegreeModality_Id(
            Long studentId,
            Long modalityId
    );


    Optional<StudentModality> findTopByStudentIdOrderByUpdatedAtDesc(Long studentId);



    List<StudentModality> findByStatusInAndStudent_NameContainingIgnoreCaseOrStatusInAndStudent_LastNameContainingIgnoreCase(
            List<ModalityProcessStatus> statuses1,
            String name1,
            List<ModalityProcessStatus> statuses2,
            String name2
    );

    List<StudentModality> findByStudent_NameContainingIgnoreCaseOrStudent_LastNameContainingIgnoreCase(
            String name1,
            String name2
    );

    Optional<StudentModality> findByStudent(User student);


    boolean existsByStudent_IdAndProgramDegreeModality_Id(Long id, Long id1);

    @Query("""
SELECT sm FROM StudentModality sm
WHERE sm.programDegreeModality.academicProgram.id IN :programIds
AND sm.status IN :statuses
AND (
    LOWER(sm.student.name) LIKE LOWER(CONCAT('%', :name, '%'))
    OR LOWER(sm.student.lastName) LIKE LOWER(CONCAT('%', :name, '%'))
)
""")
    List<StudentModality> findForProgramHeadWithStatusAndName(
            List<Long> programIds,
            List<ModalityProcessStatus> statuses,
            String name
    );

    @Query("""
SELECT sm FROM StudentModality sm
WHERE sm.programDegreeModality.academicProgram.id IN :programIds
AND sm.status IN :statuses
""")
    List<StudentModality> findForProgramHeadWithStatus(
            List<Long> programIds,
            List<ModalityProcessStatus> statuses
    );

    @Query("""
SELECT sm FROM StudentModality sm
WHERE sm.programDegreeModality.academicProgram.id IN :programIds
AND (
    LOWER(sm.student.name) LIKE LOWER(CONCAT('%', :name, '%'))
    OR LOWER(sm.student.lastName) LIKE LOWER(CONCAT('%', :name, '%'))
)
""")
    List<StudentModality> findForProgramHeadWithName(
            List<Long> programIds,
            String name
    );

    @Query("""
SELECT sm FROM StudentModality sm
WHERE sm.programDegreeModality.academicProgram.id IN :programIds
""")
    List<StudentModality> findForProgramHead(
            List<Long> programIds
    );


    List<StudentModality> findByStatusAndProgramDegreeModality_AcademicProgram_IdIn(
            ModalityProcessStatus status,
            List<Long> academicProgramIds
    );

    @Query("""
SELECT sm FROM StudentModality sm
WHERE sm.projectDirector.id = :directorId
AND sm.status IN :statuses
AND (
    LOWER(sm.student.name) LIKE LOWER(CONCAT('%', :name, '%'))
    OR LOWER(sm.student.lastName) LIKE LOWER(CONCAT('%', :name, '%'))
)
""")
    List<StudentModality> findForProjectDirectorWithStatusAndName(
            Long directorId,
            List<ModalityProcessStatus> statuses,
            String name
    );

    @Query("""
SELECT sm FROM StudentModality sm
WHERE sm.projectDirector.id = :directorId
AND sm.status IN :statuses
""")
    List<StudentModality> findForProjectDirectorWithStatus(
            Long directorId,
            List<ModalityProcessStatus> statuses
    );

    @Query("""
SELECT sm FROM StudentModality sm
WHERE sm.projectDirector.id = :directorId
AND (
    LOWER(sm.student.name) LIKE LOWER(CONCAT('%', :name, '%'))
    OR LOWER(sm.student.lastName) LIKE LOWER(CONCAT('%', :name, '%'))
)
""")
    List<StudentModality> findForProjectDirectorWithName(
            Long directorId,
            String name
    );

    @Query("""
SELECT sm FROM StudentModality sm
WHERE sm.projectDirector.id = :directorId
""")
    List<StudentModality> findForProjectDirector(
            Long directorId
    );

    @Query("""
SELECT DISTINCT sm FROM StudentModality sm
JOIN DefenseExaminer de ON de.studentModality.id = sm.id
WHERE de.examiner.id = :examinerId
AND sm.status IN :statuses
AND (
    LOWER(sm.student.name) LIKE LOWER(CONCAT('%', :name, '%'))
    OR LOWER(sm.student.lastName) LIKE LOWER(CONCAT('%', :name, '%'))
)
ORDER BY sm.updatedAt DESC
""")
    List<StudentModality> findForExaminerWithStatusAndName(
            Long examinerId,
            List<ModalityProcessStatus> statuses,
            String name
    );

    @Query("""
SELECT DISTINCT sm FROM StudentModality sm
JOIN DefenseExaminer de ON de.studentModality.id = sm.id
WHERE de.examiner.id = :examinerId
AND sm.status IN :statuses
ORDER BY sm.updatedAt DESC
""")
    List<StudentModality> findForExaminerWithStatus(
            Long examinerId,
            List<ModalityProcessStatus> statuses
    );

    @Query("""
SELECT DISTINCT sm FROM StudentModality sm
JOIN DefenseExaminer de ON de.studentModality.id = sm.id
WHERE de.examiner.id = :examinerId
AND (
    LOWER(sm.student.name) LIKE LOWER(CONCAT('%', :name, '%'))
    OR LOWER(sm.student.lastName) LIKE LOWER(CONCAT('%', :name, '%'))
)
ORDER BY sm.updatedAt DESC
""")
    List<StudentModality> findForExaminerWithName(
            Long examinerId,
            String name
    );

    @Query("""
SELECT DISTINCT sm FROM StudentModality sm
JOIN DefenseExaminer de ON de.studentModality.id = sm.id
WHERE de.examiner.id = :examinerId
ORDER BY sm.updatedAt DESC
""")
    List<StudentModality> findForExaminer(
            Long examinerId
    );
}

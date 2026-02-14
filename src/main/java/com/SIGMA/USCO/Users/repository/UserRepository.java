package com.SIGMA.USCO.Users.repository;


import com.SIGMA.USCO.Users.Entity.enums.Status;
import com.SIGMA.USCO.Users.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);

    List<User> findAllByRoles_Name(String roleName);


    List<User> findByStatus(Status status);

    // ==================== NUEVOS MÉTODOS PARA MODALIDADES GRUPALES ====================

    /**
     * Busca estudiantes activos de un programa académico específico
     * @param programId ID del programa académico
     * @param roleName Nombre del rol (típicamente "STUDENT")
     * @return Lista de estudiantes
     */
    @Query("""
        SELECT DISTINCT u FROM User u
        JOIN u.roles r
        JOIN StudentProfile sp ON sp.user.id = u.id
        WHERE r.name = :roleName
        AND sp.academicProgram.id = :programId
        AND u.status = 'ACTIVE'
        ORDER BY u.lastName, u.name
        """)
    List<User> findStudentsByAcademicProgram(
            @Param("programId") Long programId,
            @Param("roleName") String roleName
    );

    /**
     * Busca estudiantes de un programa académico con filtro de nombre
     * @param programId ID del programa académico
     * @param roleName Nombre del rol
     * @param searchTerm Término de búsqueda (nombre o apellido)
     * @return Lista de estudiantes filtrados
     */
    @Query("""
        SELECT DISTINCT u FROM User u
        JOIN u.roles r
        JOIN StudentProfile sp ON sp.user.id = u.id
        WHERE r.name = :roleName
        AND sp.academicProgram.id = :programId
        AND u.status = 'ACTIVE'
        AND (
            LOWER(u.name) LIKE LOWER(CONCAT('%', :searchTerm, '%'))
            OR LOWER(u.lastName) LIKE LOWER(CONCAT('%', :searchTerm, '%'))
        )
        ORDER BY u.lastName, u.name
        """)
    List<User> findStudentsByAcademicProgramAndName(
            @Param("programId") Long programId,
            @Param("roleName") String roleName,
            @Param("searchTerm") String searchTerm
    );

    /**
     * Busca estudiantes elegibles para invitar a una modalidad grupal
     * Excluye estudiantes que ya tienen modalidad activa o invitaciones pendientes
     * @param programId ID del programa académico
     * @param roleName Nombre del rol
     * @param excludedUserIds IDs de usuarios a excluir
     * @return Lista de estudiantes elegibles
     */
    @Query("""
        SELECT DISTINCT u FROM User u
        JOIN u.roles r
        JOIN StudentProfile sp ON sp.user.id = u.id
        WHERE r.name = :roleName
        AND sp.academicProgram.id = :programId
        AND u.status = 'ACTIVE'
        AND u.id NOT IN :excludedUserIds
        ORDER BY u.lastName, u.name
        """)
    List<User> findEligibleStudentsForInvitation(
            @Param("programId") Long programId,
            @Param("roleName") String roleName,
            @Param("excludedUserIds") List<Long> excludedUserIds
    );

    /**
     * Busca estudiantes elegibles con filtro de nombre
     * @param programId ID del programa académico
     * @param roleName Nombre del rol
     * @param excludedUserIds IDs de usuarios a excluir
     * @param searchTerm Término de búsqueda
     * @return Lista de estudiantes elegibles filtrados
     */
    @Query("""
        SELECT DISTINCT u FROM User u
        JOIN u.roles r
        JOIN StudentProfile sp ON sp.user.id = u.id
        WHERE r.name = :roleName
        AND sp.academicProgram.id = :programId
        AND u.status = 'ACTIVE'
        AND u.id NOT IN :excludedUserIds
        AND (
            LOWER(u.name) LIKE LOWER(CONCAT('%', :searchTerm, '%'))
            OR LOWER(u.lastName) LIKE LOWER(CONCAT('%', :searchTerm, '%'))
        )
        ORDER BY u.lastName, u.name
        """)
    List<User> findEligibleStudentsForInvitationByName(
            @Param("programId") Long programId,
            @Param("roleName") String roleName,
            @Param("excludedUserIds") List<Long> excludedUserIds,
            @Param("searchTerm") String searchTerm
    );

    /*
     * MÉTODOS COMENTADOS - No se usan actualmente y causan problemas de validación
     * Si se necesitan en el futuro, descomentar y corregir las queries
     */

    /*
    @Query("""
        SELECT u FROM User u
        LEFT JOIN FETCH StudentProfile sp ON sp.user.id = u.id
        LEFT JOIN FETCH sp.academicProgram ap
        WHERE u.id = :userId
        """)
    Optional<User> findByIdWithStudentProfile(@Param("userId") Long userId);
    */

    /*
    @Query("""
        SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END
        FROM User u
        JOIN StudentProfile sp ON sp.user.id = u.id
        WHERE u.id = :userId
        AND sp.academicProgram.id = :programId
        """)
    boolean isStudentOfProgram(@Param("userId") Long userId, @Param("programId") Long programId);
    */
}

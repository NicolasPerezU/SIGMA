package com.SIGMA.USCO.Users.repository;


import com.SIGMA.USCO.Users.Entity.StudentProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StudentProfileRepository extends JpaRepository<StudentProfile, Long> {
    Optional<StudentProfile> findByUserId(Long userId);

    boolean existsByUserId(Long userId);
}

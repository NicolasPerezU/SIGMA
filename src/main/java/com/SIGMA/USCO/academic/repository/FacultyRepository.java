package com.SIGMA.USCO.academic.repository;

import com.SIGMA.USCO.academic.entity.Faculty;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FacultyRepository extends JpaRepository<Faculty, Long> {

    boolean existsByCodeIgnoreCase(String code);

    boolean existsByNameIgnoreCase(String name);

    List<Faculty> findByActiveTrue();


}

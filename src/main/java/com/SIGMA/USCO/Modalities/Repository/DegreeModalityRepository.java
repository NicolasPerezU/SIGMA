package com.SIGMA.USCO.Modalities.Repository;

import com.SIGMA.USCO.Modalities.Entity.DegreeModality;
import com.SIGMA.USCO.Modalities.Entity.enums.ModalityStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DegreeModalityRepository extends JpaRepository<DegreeModality, Long> {


    boolean existsByNameIgnoreCase(String name);

    List<DegreeModality> findByStatus(ModalityStatus status);

    boolean existsByNameIgnoreCaseAndFacultyId(String name, Long id);
}

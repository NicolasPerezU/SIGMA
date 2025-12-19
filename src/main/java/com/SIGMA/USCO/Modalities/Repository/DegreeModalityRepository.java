package com.SIGMA.USCO.Modalities.Repository;

import com.SIGMA.USCO.Modalities.Entity.DegreeModality;
import com.SIGMA.USCO.Modalities.Entity.enums.ModalityStatus;
import com.SIGMA.USCO.Modalities.Entity.enums.ModalityType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DegreeModalityRepository extends JpaRepository<DegreeModality, Long> {

    Optional<DegreeModality> findByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCase(String name);

    List<DegreeModality> findByStatus(ModalityStatus status);

    List<DegreeModality> findByType(ModalityType type);

}

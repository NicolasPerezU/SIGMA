package com.SIGMA.USCO.Modalities.Repository;

import com.SIGMA.USCO.Modalities.Entity.ModalityRequirements;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ModalityRequirementsRepository extends JpaRepository<ModalityRequirements, Long> {

    List<ModalityRequirements> findByModalityId(Long modalityId);

    List<ModalityRequirements> findByModalityIdAndActiveTrue(Long modalityId);
}

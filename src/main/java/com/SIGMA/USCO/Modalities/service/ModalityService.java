package com.SIGMA.USCO.Modalities.service;

import com.SIGMA.USCO.Modalities.Entity.*;
import com.SIGMA.USCO.Modalities.Repository.DegreeModalityRepository;
import com.SIGMA.USCO.Modalities.Repository.ModalityRequirementsRepository;
import com.SIGMA.USCO.Modalities.dto.ModalityRequest;
import com.SIGMA.USCO.Modalities.dto.RequirementsRequest;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ModalityService {

    private final DegreeModalityRepository degreeModalityRepository;
    private final ModalityRequirementsRepository modalityRequirementsRepository;

    public ResponseEntity<?> createModality(ModalityRequest request){

        if (degreeModalityRepository.existsByNameIgnoreCase(request.getName())){
            return ResponseEntity.badRequest().body("La modalidad con el nombre " + request.getName() + " ya existe.");
        }
        ModalityType type = ModalityType.valueOf(request.getType().toString());

        DegreeModality newModality = DegreeModality.builder()
                .name(request.getName())
                .description(request.getDescription())
                .creditsRequired(request.getCreditsRequired())
                .type(type)
                .status(ModalityStatus.ACTIVE)
                .createdAt(java.time.LocalDateTime.now())
                .updatedAt(java.time.LocalDateTime.now())
                .build();

        return ResponseEntity.ok(degreeModalityRepository.save(newModality));
    }

    public ResponseEntity<?> createModalityRequirements(Long modalityId, List<RequirementsRequest> requirements) {

        if ( !degreeModalityRepository.existsById(modalityId) ) {
            return ResponseEntity.badRequest().body("La modalidad con ID " + modalityId + " no existe.");
        }
        DegreeModality modality = degreeModalityRepository.findById(modalityId).orElseThrow();


        for (RequirementsRequest requirementRequest : requirements) {
            ModalityRequirements requirement = ModalityRequirements.builder()
                    .modality(modality)
                    .requirementName(requirementRequest.getRequirementName())
                    .description(requirementRequest.getDescription())
                    .ruleType(requirementRequest.getRuleType())
                    .expectedValue(requirementRequest.getExpectedValue())
                    .active(true)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();

            modalityRequirementsRepository.save(requirement);
        }

        return ResponseEntity.ok("Requisitos creados exitosamente");
    }


}

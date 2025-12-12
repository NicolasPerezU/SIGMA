package com.SIGMA.USCO.Modalities.Controller;

import com.SIGMA.USCO.Modalities.dto.ModalityRequest;
import com.SIGMA.USCO.Modalities.dto.RequirementsRequest;
import com.SIGMA.USCO.Modalities.service.ModalityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/modalities")
@RequiredArgsConstructor
public class ModalityController {

    private final ModalityService modalityService;

    @PostMapping("/create")
    @PreAuthorize("hasAuthority('PERM_CREATE_MODALITY') or hasAuthority('PERM_UPDATE_MODALITY')")
    public ResponseEntity<?> createModality(@RequestBody ModalityRequest request) {
        return modalityService.createModality(request);
    }

    @PostMapping("/requirements/create/{modalityId}")
    @PreAuthorize("hasAuthority('PERM_CREATE_MODALITY') or hasAuthority('PERM_UPDATE_MODALITY')")
    public ResponseEntity<?> createModalityRequirements(@PathVariable Long modalityId, @RequestBody List<RequirementsRequest> requirements) {
        return modalityService.createModalityRequirements(modalityId, requirements);
    }


}

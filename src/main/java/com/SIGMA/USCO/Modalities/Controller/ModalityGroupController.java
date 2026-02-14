package com.SIGMA.USCO.Modalities.Controller;

import com.SIGMA.USCO.Modalities.dto.groups.InviteStudentRequest;
import com.SIGMA.USCO.Modalities.service.ModalityGroupService;
import com.SIGMA.USCO.documents.service.DocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/modality-groups")
@RequiredArgsConstructor
public class ModalityGroupController {

    private final ModalityGroupService modalityGroupService;
    private final DocumentService documentService;


    @PostMapping("/{modalityId}/start-group")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<?> startGroupModality(@PathVariable Long modalityId) {
        return modalityGroupService.startStudentModalityGroup(modalityId);
    }


    @GetMapping("/eligible-students")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<?> getEligibleStudents(@RequestParam(required = false) String nameFilter) {
        return modalityGroupService.getEligibleStudentsForInvitation(nameFilter);
    }


    @PostMapping("/invite")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<?> inviteStudent(@RequestBody InviteStudentRequest request) {
        return modalityGroupService.inviteStudentToModality(request.getStudentModalityId(), request.getInviteeId());
    }


    @PostMapping("/invitations/{invitationId}/accept")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<?> acceptInvitation(@PathVariable Long invitationId) {
        return modalityGroupService.acceptInvitation(invitationId);
    }


    @PostMapping("/invitations/{invitationId}/reject")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<?> rejectInvitation(@PathVariable Long invitationId) {
        return modalityGroupService.rejectInvitation(invitationId);
    }


}

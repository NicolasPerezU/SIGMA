package com.SIGMA.USCO.Users.controller;

import com.SIGMA.USCO.Users.dto.AssignRoleRequest;
import com.SIGMA.USCO.Users.dto.ChangeUserStatusRequest;
import com.SIGMA.USCO.Users.dto.RoleRequest;
import com.SIGMA.USCO.Users.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @PostMapping("/createRole")
    @PreAuthorize("hasAuthority('PERM_CREATE_ROLE')")
    public ResponseEntity<?> createRole(@RequestBody RoleRequest request) {
        return adminService.createRole(request);
    }

    @PutMapping("/updateRole/{id}")
    @PreAuthorize("hasAuthority('PERM_UPDATE_ROLE')")
    public ResponseEntity<?> updateRole(@PathVariable Long id, @RequestBody RoleRequest request) {
        return adminService.updateRole(id, request);
    }

    @PostMapping("/assignRole")
    @PreAuthorize("hasAuthority('PERM_ASSIGN_ROLE')")
    public ResponseEntity<?> assignRoleToUser(@RequestBody AssignRoleRequest request) {
        return adminService.assignRoleToUser(request);
    }

    @PostMapping("/changeUserStatus")
    @PreAuthorize("hasAuthority('PERM_ACTIVATE_OR_DEACTIVATE_USER')")
    public ResponseEntity<?> changeUserStatus(@RequestBody ChangeUserStatusRequest request){
        return adminService.changeUserStatus(request);
    }




}

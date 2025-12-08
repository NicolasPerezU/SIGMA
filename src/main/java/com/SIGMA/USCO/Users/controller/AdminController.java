package com.SIGMA.USCO.Users.controller;

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
    @PreAuthorize("hasRole('SUPERADMIN')")
    public ResponseEntity<?> createRole(@RequestBody RoleRequest request) {
        return adminService.createRole(request);
    }

    @PutMapping("/updateRole/{id}")
    @PreAuthorize("hasRole('SUPERADMIN')")
    public ResponseEntity<?> updateRole(@PathVariable Long id, @RequestBody RoleRequest request) {
        return adminService.updateRole(id, request);
    }


}

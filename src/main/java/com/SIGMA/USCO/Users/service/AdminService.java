package com.SIGMA.USCO.Users.service;

import com.SIGMA.USCO.Users.Entity.Permission;
import com.SIGMA.USCO.Users.Entity.Role;
import com.SIGMA.USCO.Users.dto.RoleRequest;
import com.SIGMA.USCO.Users.repository.PermissionRepository;
import com.SIGMA.USCO.Users.repository.RoleRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    public ResponseEntity<?> createRole(RoleRequest request) {

        if (request.getName() == null || request.getName().isBlank()) {
            return ResponseEntity.badRequest().body("El nombre del rol es obligatorio");
        }

        if (roleRepository.findByName(request.getName()).isPresent()) {
            return ResponseEntity.badRequest().body("El rol ya existe.");
        }

        Set<Permission> permissions = Set.of();

        if (request.getPermissionIds() != null && !request.getPermissionIds().isEmpty()) {
            permissions = permissionRepository.findAllById(request.getPermissionIds())
                    .stream().collect(Collectors.toSet());
        }

        Role role = Role.builder()
                .name(request.getName().toUpperCase())
                .permissions(permissions)
                .build();

        roleRepository.save(role);

        return ResponseEntity.ok(role);
    }

    public ResponseEntity<?> updateRole(Long id, RoleRequest request){
        if (request.getName() == null || request.getName().isBlank()) {
            return ResponseEntity.badRequest().body("El nombre del rol es obligatorio");
        }

        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rol no encontrado"));

        if (roleRepository.findByName(request.getName()).isPresent() && !role.getName().equalsIgnoreCase(request.getName())) {
            return ResponseEntity.badRequest().body("El rol ya existe.");
        }

        Set<Permission> permissions = Set.of();

        if (request.getPermissionIds() != null && !request.getPermissionIds().isEmpty()) {
            permissions = permissionRepository.findAllById(request.getPermissionIds())
                    .stream().collect(Collectors.toSet());
        }

        role.setName(request.getName().toUpperCase());
        role.setPermissions(permissions);

        roleRepository.save(role);

        return ResponseEntity.ok(role);
    }
}

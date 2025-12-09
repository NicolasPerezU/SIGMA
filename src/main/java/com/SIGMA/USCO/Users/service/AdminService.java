package com.SIGMA.USCO.Users.service;

import com.SIGMA.USCO.Users.Entity.Permission;
import com.SIGMA.USCO.Users.Entity.Role;
import com.SIGMA.USCO.Users.Entity.Status;
import com.SIGMA.USCO.Users.Entity.User;
import com.SIGMA.USCO.Users.dto.AssignRoleRequest;
import com.SIGMA.USCO.Users.dto.ChangeUserStatusRequest;
import com.SIGMA.USCO.Users.dto.RoleRequest;
import com.SIGMA.USCO.Users.repository.PermissionRepository;
import com.SIGMA.USCO.Users.repository.RoleRepository;
import com.SIGMA.USCO.Users.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final UserRepository userRepository;

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

    public ResponseEntity<?> assignRoleToUser(AssignRoleRequest request){

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Role role = roleRepository.findById(request.getRoleId())
                .orElseThrow(() -> new RuntimeException("Rol no encontrado"));

        user.getRoles().clear();
        user.getRoles().add(role);
        user.setLastUpdateDate(LocalDateTime.now());
        userRepository.save(user);

        return ResponseEntity.ok("Rol asignado correctamente al usuario.");
    }

    public ResponseEntity<?> changeUserStatus(ChangeUserStatusRequest request){

        if (request.getStatus() == null) {
            return ResponseEntity.badRequest().body("El estado debe ser ACTIVE o INACTIVE.");
        }

        Status newStatus;
        try {
            newStatus = Status.valueOf(request.getStatus().toUpperCase());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("El estado debe ser ACTIVE o INACTIVE.");
        }

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        user.setStatus(newStatus);
        user.setLastUpdateDate(LocalDateTime.now());
        userRepository.save(user);
        return ResponseEntity.ok("Estado del usuario actualizado correctamente.");

    }


}

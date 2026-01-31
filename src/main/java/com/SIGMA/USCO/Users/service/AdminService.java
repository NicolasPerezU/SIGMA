package com.SIGMA.USCO.Users.service;

import com.SIGMA.USCO.Modalities.Entity.DegreeModality;
import com.SIGMA.USCO.Modalities.Entity.enums.ModalityStatus;
import com.SIGMA.USCO.Modalities.Repository.DegreeModalityRepository;
import com.SIGMA.USCO.Modalities.dto.ModalityDTO;
import com.SIGMA.USCO.Users.Entity.Permission;
import com.SIGMA.USCO.Users.Entity.Role;
import com.SIGMA.USCO.Users.Entity.Status;
import com.SIGMA.USCO.Users.Entity.User;
import com.SIGMA.USCO.Users.dto.request.PermissionDTO;
import com.SIGMA.USCO.Users.dto.request.RoleRequest;
import com.SIGMA.USCO.Users.dto.request.UpdateUserRequest;
import com.SIGMA.USCO.Users.dto.response.UserResponse;
import com.SIGMA.USCO.Users.repository.PermissionRepository;
import com.SIGMA.USCO.Users.repository.RoleRepository;
import com.SIGMA.USCO.Users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final UserRepository userRepository;
    private final DegreeModalityRepository degreeModalityRepository;


    public ResponseEntity<?> getRoles() {

        return ResponseEntity.ok(roleRepository.findAll().stream().map(role -> RoleRequest.builder()
                                .name(role.getName())
                                .permissionIds(
                                        role.getPermissions()
                                                .stream()
                                                .map(Permission::getId)
                                                .collect(Collectors.toSet()))
                                .build()
                        )
                        .toList()
        );
    }

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

        return ResponseEntity.ok(" Rol creado correctamente.");
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

        return ResponseEntity.ok(" Rol actualizado correctamente.");
    }

    public ResponseEntity<?> assignRoleToUser(UpdateUserRequest request){

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

    public ResponseEntity<?> changeUserStatus(UpdateUserRequest request){

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

    public ResponseEntity<?> createPermission (PermissionDTO request){

        if (request.getName() == null || request.getName().isBlank()) {
            return ResponseEntity.badRequest().body("El nombre del permiso es obligatorio");
        }

        if (permissionRepository.findByName(request.getName()).isPresent()) {
            return ResponseEntity.badRequest().body("El permiso ya existe.");
        }

        Permission permission = Permission.builder()
                .name(request.getName().toUpperCase())
                .build();

        permissionRepository.save(permission);

        return ResponseEntity.ok(" Permiso creado correctamente.");

    }

    public ResponseEntity<?> getPermissions() {

        return ResponseEntity.ok(permissionRepository.findAll().stream().map(permission -> PermissionDTO.builder()
                                .id(permission.getId())
                                .name(permission.getName())
                                .build()
                        )
                        .toList()
        );
    }

    public ResponseEntity<?> getUsers(String status) {

        List<User> users;

        if (status == null || status.isBlank()) {
            users = userRepository.findAll();
        } else {
            Status userStatus;
            try {
                userStatus = Status.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest()
                        .body("Estado inválido. Use ACTIVE o INACTIVE");
            }
            users = userRepository.findByStatus(userStatus);
        }

        return ResponseEntity.ok(
                users.stream()
                        .map(user -> UserResponse.builder()
                                .id(user.getId())
                                .name(user.getName())
                                .email(user.getEmail())
                                .status(user.getStatus())
                                .roles(
                                        user.getRoles().stream()
                                                .map(Role::getName)
                                                .collect(Collectors.toSet())
                                )
                                .createdDate(user.getCreationDate())
                                .build()
                        )
                        .toList()
        );
    }

    public ResponseEntity<?> desactiveUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (user.getStatus() == Status.INACTIVE) {
            return ResponseEntity.badRequest().body("El usuario ya está inactivo.");
        }

        user.setStatus(Status.INACTIVE);
        user.setLastUpdateDate(LocalDateTime.now());
        userRepository.save(user);

        return ResponseEntity.ok("Usuario desactivado correctamente.");
    }

    public ResponseEntity<List<ModalityDTO>> getModalities(ModalityStatus status) {

        List<DegreeModality> modalities;

        if (status != null) {
            modalities = degreeModalityRepository.findByStatus(status);
        } else {
            modalities = degreeModalityRepository.findAll();
        }

        List<ModalityDTO> response = modalities.stream()
                .map(mod -> ModalityDTO.builder()
                        .id(mod.getId())
                        .name(mod.getName())
                        .description(mod.getDescription())
                        .creditsRequired(mod.getCreditsRequired())
                        .status(mod.getStatus())
                        .type(mod.getType())
                        .build())
                .toList();

        return ResponseEntity.ok(response);
    }

}

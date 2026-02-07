package com.SIGMA.USCO.Users.service;

import com.SIGMA.USCO.Modalities.Entity.DegreeModality;
import com.SIGMA.USCO.Modalities.Entity.enums.ModalityStatus;
import com.SIGMA.USCO.Modalities.Repository.DegreeModalityRepository;
import com.SIGMA.USCO.Modalities.Repository.ModalityRequirementsRepository;
import com.SIGMA.USCO.Modalities.dto.ModalityDTO;
import com.SIGMA.USCO.Modalities.dto.RequirementDTO;
import com.SIGMA.USCO.Users.Entity.Permission;
import com.SIGMA.USCO.Users.Entity.ProgramAuthority;
import com.SIGMA.USCO.Users.Entity.Role;
import com.SIGMA.USCO.Users.Entity.enums.ProgramRole;
import com.SIGMA.USCO.Users.Entity.enums.Status;
import com.SIGMA.USCO.Users.Entity.User;
import com.SIGMA.USCO.Users.dto.request.assignAuthorityProgram;
import com.SIGMA.USCO.Users.dto.request.PermissionDTO;
import com.SIGMA.USCO.Users.dto.request.RoleRequest;
import com.SIGMA.USCO.Users.dto.request.UpdateUserRequest;
import com.SIGMA.USCO.Users.dto.response.UserResponse;
import com.SIGMA.USCO.Users.repository.PermissionRepository;
import com.SIGMA.USCO.Users.repository.ProgramAuthorityRepository;
import com.SIGMA.USCO.Users.repository.RoleRepository;
import com.SIGMA.USCO.Users.repository.UserRepository;
import com.SIGMA.USCO.academic.entity.AcademicProgram;
import com.SIGMA.USCO.academic.repository.AcademicProgramRepository;
import com.SIGMA.USCO.documents.dto.RequiredDocumentDTO;
import com.SIGMA.USCO.documents.repository.RequiredDocumentRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final UserRepository userRepository;
    private final DegreeModalityRepository degreeModalityRepository;
    private final ModalityRequirementsRepository modalityRequirementsRepository;
    private final RequiredDocumentRepository requiredDocumentRepository;
    private final AcademicProgramRepository academicProgramRepository;
    private final ProgramAuthorityRepository programAuthorityRepository;


    public ResponseEntity<?> getRoles() {

        return ResponseEntity.ok(roleRepository.findAll().stream().map(role -> RoleRequest.builder()
                                .id(role.getId())
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

        Optional<Role> existingRole = roleRepository.findByNameIgnoreCase(request.getName());

        if (existingRole.isPresent() && !existingRole.get().getId().equals(id)) {
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

    @Transactional
    public ProgramAuthority assignProgramHead(assignAuthorityProgram request){

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        AcademicProgram program = academicProgramRepository.findById(request.getAcademicProgramId())
                .orElseThrow(() -> new RuntimeException("Programa académico no encontrado"));

        Role programHeadRole = roleRepository.findByName("PROGRAM_HEAD")
                .orElseThrow(() -> new RuntimeException("Rol PROGRAM_HEAD no encontrado"));

        if (!user.getRoles().contains(programHeadRole)) {
            user.getRoles().add(programHeadRole);
            userRepository.save(user);
        }

        ProgramAuthority authority = ProgramAuthority.builder()
                .user(user)
                .academicProgram(program)
                .role(ProgramRole.PROGRAM_HEAD)
                .build();

        programAuthorityRepository.save(authority);
        return authority;
    }

    @Transactional
    public ProgramAuthority assignProjectDirector(assignAuthorityProgram request){

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        AcademicProgram program = academicProgramRepository.findById(request.getAcademicProgramId())
                .orElseThrow(() -> new RuntimeException("Programa académico no encontrado"));

        Role projectDirector = roleRepository.findByName("PROJECT_DIRECTOR")
                .orElseThrow(() -> new RuntimeException("Rol PROJECT_DIRECTOR no encontrado"));

        if (!user.getRoles().contains(projectDirector)) {
            user.getRoles().add(projectDirector);
            userRepository.save(user);
        }

        ProgramAuthority authority = ProgramAuthority.builder()
                .user(user)
                .academicProgram(program)
                .role(ProgramRole.PROJECT_DIRECTOR)
                .build();

        programAuthorityRepository.save(authority);
        return authority;
    }

    @Transactional
    public ProgramAuthority assignCommittee(assignAuthorityProgram request){

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        AcademicProgram program = academicProgramRepository.findById(request.getAcademicProgramId())
                .orElseThrow(() -> new RuntimeException("Programa académico no encontrado"));

        Role committee = roleRepository.findByName("PROGRAM_CURRICULUM_COMMITTEE")
                .orElseThrow(() -> new RuntimeException("Rol PROGRAM_CURRICULUM_COMMITTEE no encontrado"));

        if (!user.getRoles().contains(committee)) {
            user.getRoles().add(committee);
            userRepository.save(user);
        }

        ProgramAuthority authority = ProgramAuthority.builder()
                .user(user)
                .academicProgram(program)
                .role(ProgramRole.PROGRAM_CURRICULUM_COMMITTEE)
                .build();

        programAuthorityRepository.save(authority);
        return authority;
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
                        .status(mod.getStatus())


                        .facultyId(mod.getFaculty().getId())
                        .facultyName(mod.getFaculty().getName())


                        .requirements(
                                modalityRequirementsRepository.findByModalityId(mod.getId())
                                        .stream()
                                        .map(req -> RequirementDTO.builder()
                                                .id(req.getId())
                                                .requirementName(req.getRequirementName())
                                                .description(req.getDescription())
                                                .ruleType(req.getRuleType())
                                                .expectedValue(req.getExpectedValue())
                                                .active(req.isActive())
                                                .build())
                                        .toList()
                        )


                        .documents(
                                requiredDocumentRepository.findByModalityId(mod.getId())
                                        .stream()
                                        .map(doc -> RequiredDocumentDTO.builder()
                                                .id(doc.getId())
                                                .modalityId( doc.getModality().getId())
                                                .documentName(doc.getDocumentName())
                                                .description(doc.getDescription())
                                                .allowedFormat(doc.getAllowedFormat())
                                                .maxFileSizeMB(doc.getMaxFileSizeMB())
                                                .mandatory(doc.isMandatory())
                                                .active(doc.isActive())
                                                .build())
                                        .toList()
                        )

                        .build()
                )
                .toList();

        return ResponseEntity.ok(response);
    }




}

package com.SIGMA.USCO.config;

import com.SIGMA.USCO.Users.Entity.Permission;
import com.SIGMA.USCO.Users.Entity.Role;
import com.SIGMA.USCO.Users.repository.PermissionRepository;
import com.SIGMA.USCO.Users.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Set;

@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    @Bean
    CommandLineRunner initData() {
        return args -> {

            // Crear permisos base
            Permission verDocumentos = createPermission("VER_DOCUMENTOS_ESTUDIANTE");
            Permission crearUsuario = createPermission("CREAR_USUARIO");
            Permission editarUsuario = createPermission("EDITAR_USUARIO");
            Permission activateOrDeactivateUser = createPermission("ACTIVATE_OR_DEACTIVATE_USER");
            Permission createRole = createPermission("CREATE_ROLE");
            Permission updateRole = createPermission("UPDATE_ROLE");
            Permission assignRole = createPermission("ASSIGN_ROLE");
            Permission createModality = createPermission("CREATE_MODALITY");
            Permission updateModality = createPermission("UPDATE_MODALITY");
            Permission createRequiredDocument = createPermission("CREATE_REQUIRED_DOCUMENT");
            Permission updateRequiredDocument = createPermission("UPDATE_REQUIRED_DOCUMENT");
            Permission reviewDocuments = createPermission("REVIEW_DOCUMENTS");
            Permission viewDocuments = createPermission("VIEW_DOCUMENTS");
            Permission approveModality = createPermission("APPROVE_MODALITY");
            Permission viewAllModalities = createPermission("VIEW_ALL_MODALITIES");
            Permission approveCancellation = createPermission("APPROVE_CANCELLATION");
            Permission rejectCancellation = createPermission("REJECT_CANCELLATION");
            Permission assignProjectDirector = createPermission("ASSIGN_PROJECT_DIRECTOR");
            Permission scheduleDefense = createPermission("SCHEDULE_DEFENSE");


            // Crear roles y asignar permisos
            createRole("SUPERADMIN", Set.of(verDocumentos, crearUsuario, editarUsuario, activateOrDeactivateUser, createRole, updateRole, assignRole, createModality, updateModality));

            createRole("SECRETARY", Set.of(verDocumentos, reviewDocuments, viewDocuments, approveModality, viewAllModalities) );

            createRole("COUNCIL", Set.of(verDocumentos,approveCancellation, rejectCancellation, assignProjectDirector, scheduleDefense) );

            createRole("STUDENT", Set.of(verDocumentos) );

            createRole("PROJECT_DIRECTOR", Set.of(verDocumentos) );
        };
    }

    private Permission createPermission(String name) {
        return permissionRepository.findByName(name)
                .orElseGet(() -> permissionRepository.save(
                        Permission.builder().name(name).build()
                ));
    }

    private void createRole(String name,Set<Permission> permissions) {
        roleRepository.findByName(name)
                .orElseGet(() -> roleRepository.save(
                        Role.builder()
                                .name(name)
                                .permissions(permissions)
                                .build()
                ));
    }

}

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


            // Crear roles y asignar permisos
            createRole("SUPERADMIN", Set.of(verDocumentos, crearUsuario, editarUsuario, activateOrDeactivateUser, createRole, updateRole, assignRole));

            createRole("SECRETARY", Set.of(verDocumentos));

            createRole("COUNCIL", Set.of(verDocumentos) );

            createRole("STUDENT", Set.of(verDocumentos) );
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

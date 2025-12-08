package com.SIGMA.USCO.Users.repository;


import com.SIGMA.USCO.Users.Entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PermissionRepository extends JpaRepository<Permission, Long> {

    Optional<Permission> findByName(String name);

}

package com.SIGMA.USCO.Users.repository;


import com.SIGMA.USCO.Users.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
}

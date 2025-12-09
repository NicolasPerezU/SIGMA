package com.SIGMA.USCO.Users.repository;


import com.SIGMA.USCO.Users.Entity.BlackListedToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BlackListedTokenRepository extends JpaRepository<BlackListedToken, Long> {
    boolean existsByToken(String token);
    Optional<BlackListedToken> findByToken(String token);
}

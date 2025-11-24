package com.parmida98.Spring_Security.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CustomUserRepository extends JpaRepository<CustomUser, UUID> {

    // Method will be called within UserDetailsService
    Optional<CustomUser> findUserByUsername(String username);
}

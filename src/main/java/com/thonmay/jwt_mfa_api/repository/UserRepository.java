package com.thonmay.jwt_mfa_api.repository;

import com.thonmay.jwt_mfa_api.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
}

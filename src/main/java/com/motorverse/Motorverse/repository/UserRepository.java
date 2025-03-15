package com.motorverse.Motorverse.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.motorverse.Motorverse.entity.User;

public interface UserRepository extends JpaRepository<User, Integer> {
    // Optional: Add custom query method if needed
    boolean existsByEmail(String email);
}
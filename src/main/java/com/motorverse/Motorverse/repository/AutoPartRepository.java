package com.motorverse.Motorverse.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.motorverse.Motorverse.entity.AutoPart;

public interface AutoPartRepository extends JpaRepository<AutoPart, Integer> {
    // This interface will automatically inherit CRUD operations from JpaRepository
    // No additional methods are needed for basic functionality

}

package com.motorverse.Motorverse.repository;

import com.motorverse.Motorverse.entity.AdminActionLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminActionLogRepository extends JpaRepository<AdminActionLog, Integer> {
}
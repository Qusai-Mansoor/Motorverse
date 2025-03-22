package com.motorverse.Motorverse.repository;

import com.motorverse.Motorverse.entity.SupportTicket;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SupportTicketRepository extends JpaRepository<SupportTicket, Integer> {
    List<SupportTicket> findByUserIdOrderByCreatedAtDesc(int userId);
}

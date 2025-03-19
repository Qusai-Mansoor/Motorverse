package com.motorverse.Motorverse.repository;


import com.motorverse.Motorverse.entity.Purchase;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PurchaseRepository extends JpaRepository<Purchase, Integer> {
}

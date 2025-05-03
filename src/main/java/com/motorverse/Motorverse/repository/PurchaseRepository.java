package com.motorverse.Motorverse.repository;

import com.motorverse.Motorverse.entity.Purchase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PurchaseRepository extends JpaRepository<Purchase, Integer> {
    List<Purchase> findByUserId(int userId);
    List<Purchase> findByVehicleId(int vehicleId);
    List<Purchase> findByTransactionId(String transactionId);
}

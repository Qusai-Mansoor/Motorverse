package com.motorverse.Motorverse.repository;

import com.motorverse.Motorverse.entity.Rental;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RentalRepository extends JpaRepository<Rental, Integer> {
    List<Rental> findByUserId(int userId);
    List<Rental> findByUserIdAndStatus(int userId, Rental.Status status);
    List<Rental> findByVehicleId(int vehicleId);
}
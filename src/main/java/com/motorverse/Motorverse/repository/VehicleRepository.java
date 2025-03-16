package com.motorverse.Motorverse.repository;

import com.motorverse.Motorverse.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VehicleRepository extends JpaRepository<Vehicle, Integer> {
    // Custom query to find available vehicles for sale
    List<Vehicle> findByStatus(Vehicle.Status status);
}
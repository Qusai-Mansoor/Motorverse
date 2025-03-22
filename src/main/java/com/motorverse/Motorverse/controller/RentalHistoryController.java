package com.motorverse.Motorverse.controller;

import com.motorverse.Motorverse.entity.Rental;
import com.motorverse.Motorverse.entity.Vehicle;
import com.motorverse.Motorverse.repository.RentalRepository;
import com.motorverse.Motorverse.repository.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/rental-history")
public class RentalHistoryController {
    
    @Autowired
    private RentalRepository rentalRepository;
    
    @Autowired
    private VehicleRepository vehicleRepository;
    
    /**
     * Get all rentals (past and current) for a specific user
     */
    @GetMapping("/user/{userId}")
    public List<Map<String, Object>> getUserRentalHistory(@PathVariable int userId) {
        // Find all rentals for the user, regardless of status
        List<Rental> rentals = rentalRepository.findByUserId(userId);
        
        // Sort rentals by start date (newest first)
        rentals.sort((r1, r2) -> r2.getStartDate().compareTo(r1.getStartDate()));
        
        // Build response with rental and vehicle details
        return rentals.stream().map(rental -> {
            Map<String, Object> rentalInfo = new HashMap<>();
            
            // Add rental information
            rentalInfo.put("rental", rental);
            
            // Find and add vehicle information
            Vehicle vehicle = vehicleRepository.findById(rental.getVehicleId())
                    .orElse(null);
            
            if (vehicle != null) {
                rentalInfo.put("vehicle", vehicle);
            }
            
            return rentalInfo;
        }).collect(Collectors.toList());
    }
    
    /**
     * Get details of a specific rental
     */
    @GetMapping("/{rentalId}")
    public Map<String, Object> getRentalDetails(@PathVariable int rentalId) {
        Map<String, Object> result = new HashMap<>();
        
        Rental rental = rentalRepository.findById(rentalId)
                .orElseThrow(() -> new RuntimeException("Rental not found"));
        
        Vehicle vehicle = vehicleRepository.findById(rental.getVehicleId())
                .orElseThrow(() -> new RuntimeException("Vehicle not found"));
        
        result.put("rental", rental);
        result.put("vehicle", vehicle);
        
        return result;
    }
}

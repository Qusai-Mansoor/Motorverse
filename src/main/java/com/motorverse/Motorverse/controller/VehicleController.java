package com.motorverse.Motorverse.controller;

import com.motorverse.Motorverse.entity.Vehicle;
import com.motorverse.Motorverse.repository.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/vehicles")
public class VehicleController {
    @Autowired
    private VehicleRepository vehicleRepository;

    @GetMapping("/buy")
    public List<Vehicle> getVehiclesForSale() {
        return vehicleRepository.findByStatus(Vehicle.Status.AVAILABLE);
    }
}
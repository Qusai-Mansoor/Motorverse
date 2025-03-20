package com.motorverse.Motorverse.controller;

import com.motorverse.Motorverse.entity.Purchase;
import com.motorverse.Motorverse.entity.Rental;
import com.motorverse.Motorverse.entity.Vehicle;
import com.motorverse.Motorverse.repository.VehicleRepository;
import com.motorverse.Motorverse.repository.PurchaseRepository;
import com.motorverse.Motorverse.repository.RentalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;

@RestController
@RequestMapping("/api/vehicles")
public class VehicleController {
    @Autowired
    private VehicleRepository vehicleRepository;
    @Autowired
    private PurchaseRepository purchaseRepository;
    @Autowired
    private RentalRepository rentalRepository;

    @GetMapping("/buy")
    public List<Vehicle> getVehiclesForSale() {
        return vehicleRepository.findByStatus(Vehicle.Status.AVAILABLE);
    }

    @GetMapping("/rent_show")
    public List<Vehicle> getVehiclesForRent() {
        return vehicleRepository.findByStatus(Vehicle.Status.AVAILABLE);
    }

    @GetMapping("/{id:[\\d]+}")  // Modified to only match digits
    public Vehicle getVehicleById(@PathVariable int id) {
        return vehicleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vehicle not found"));
    }

    @PostMapping("/purchase")
    public Purchase purchaseVehicle(@RequestBody PurchaseRequest request) {
        // Basic validation (replace with proper auth later)
        if (request.getUserId() <= 0) {
            throw new RuntimeException("User must be logged in");
        }

        Vehicle vehicle = vehicleRepository.findById(request.getVehicleId())
                .orElseThrow(() -> new RuntimeException("Vehicle not found"));
        if (vehicle.getStatus() != Vehicle.Status.AVAILABLE) {
            throw new RuntimeException("Vehicle not available");
        }

        Purchase purchase = new Purchase();
        purchase.setUserId(request.getUserId());
        purchase.setVehicleId(request.getVehicleId());
        purchase.setPaymentMethod(Purchase.PaymentMethod.valueOf(request.getPaymentMethod()));
        purchase.setAmount(vehicle.getPrice());
        purchase.setTransactionId("TXN" + System.currentTimeMillis());
        purchase.setStatus(Purchase.Status.COMPLETED);
        purchase.setPurchaseDate(LocalDateTime.now());

        vehicle.setStatus(Vehicle.Status.SOLD);
        vehicleRepository.save(vehicle);
        

        return purchaseRepository.save(purchase);
    }

    @PostMapping("/rent")
    public Rental rentVehicle(@RequestBody RentalRequest request) {
        if (request.getUserId() <= 0) {
            throw new RuntimeException("User must be logged in");
        }

        Vehicle vehicle = vehicleRepository.findById(request.getVehicleId())
                .orElseThrow(() -> new RuntimeException("Vehicle not found"));
        if (vehicle.getStatus() != Vehicle.Status.AVAILABLE) {
            throw new RuntimeException("Vehicle not available for rent");
        }

        // Calculate end date based on days
        LocalDateTime endDate = LocalDateTime.now().plusDays(request.getDays());

        Rental rental = new Rental();
        rental.setUserId(request.getUserId());
        rental.setVehicleId(request.getVehicleId());
        rental.setEndDate(endDate);

        vehicle.setStatus(Vehicle.Status.RENTED);
        vehicleRepository.save(vehicle);

        return rentalRepository.save(rental);
    }

    @GetMapping("/user-rentals/{userId}")
    public List<Map<String, Object>> getUserRentals(@PathVariable int userId) {
        List<Rental> rentals = rentalRepository.findByUserIdAndStatus(userId, Rental.Status.RENTED);
        List<Map<String, Object>> result = new ArrayList<>();
        
        for (Rental rental : rentals) {
            Map<String, Object> rentalDetails = new HashMap<>();
            Vehicle vehicle = vehicleRepository.findById(rental.getVehicleId())
                    .orElseThrow(() -> new RuntimeException("Vehicle not found"));
            
            rentalDetails.put("rental", rental);
            rentalDetails.put("vehicle", vehicle);
            result.add(rentalDetails);
        }
        
        return result;
    }
    
    @PostMapping("/return-vehicle")
    public Map<String, Object> returnVehicle(@RequestBody ReturnVehicleRequest request) {
        Rental rental = rentalRepository.findById(request.getRentalId())
                .orElseThrow(() -> new RuntimeException("Rental not found"));
        
        if (rental.getStatus() == Rental.Status.RETURNED) {
            throw new RuntimeException("This vehicle has already been returned");
        }
        
        Vehicle vehicle = vehicleRepository.findById(rental.getVehicleId())
                .orElseThrow(() -> new RuntimeException("Vehicle not found"));
        
        // Calculate any additional charges
        double additionalCharges = 0;
        
        // Check if return is late
        if (LocalDateTime.now().isAfter(rental.getEndDate())) {
            long daysLate = java.time.Duration.between(rental.getEndDate(), LocalDateTime.now()).toDays();
            if (daysLate > 0) {
                // Charge per late day (using the daily rent rate)
                additionalCharges += daysLate * vehicle.getRentRate();
            }
        }
        
        // Add damage charges if any
        if (request.getDamagePercentage() > 0) {
            // Calculate damage fee as a percentage of the vehicle's value
            additionalCharges += (request.getDamagePercentage() / 100.0) * vehicle.getPrice();
        }
        
        // Update rental status
        rental.setStatus(Rental.Status.RETURNED);
        rentalRepository.save(rental);
        
        // Update vehicle status
        vehicle.setStatus(Vehicle.Status.AVAILABLE);
        vehicleRepository.save(vehicle);
        
        // Return response with charges
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("additionalCharges", additionalCharges);
        return response;
    }
}

class PurchaseRequest {
    private int userId;
    private int vehicleId;
    private String paymentMethod;

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public int getVehicleId() { return vehicleId; }
    public void setVehicleId(int vehicleId) { this.vehicleId = vehicleId; }
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
}

class RentalRequest {
    private int userId;
    private int vehicleId;
    private int days;
    private String paymentMethod;

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public int getVehicleId() { return vehicleId; }
    public void setVehicleId(int vehicleId) { this.vehicleId = vehicleId; }
    public int getDays() { return days; }
    public void setDays(int days) { this.days = days; }
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
}

class ReturnVehicleRequest {
    private int rentalId;
    private double damagePercentage;
    
    public int getRentalId() { return rentalId; }
    public void setRentalId(int rentalId) { this.rentalId = rentalId; }
    public double getDamagePercentage() { return damagePercentage; }
    public void setDamagePercentage(double damagePercentage) { this.damagePercentage = damagePercentage; }
}
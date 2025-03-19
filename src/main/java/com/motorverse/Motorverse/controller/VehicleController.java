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
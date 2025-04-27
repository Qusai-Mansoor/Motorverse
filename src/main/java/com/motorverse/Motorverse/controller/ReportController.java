package com.motorverse.Motorverse.controller;

import com.motorverse.Motorverse.entity.ReportPurchase;
import com.motorverse.Motorverse.entity.ReportRental;
import com.motorverse.Motorverse.repository.ReportPurchaseRepository;
import com.motorverse.Motorverse.repository.ReportRentalRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private static final Logger logger = LoggerFactory.getLogger(ReportController.class);

    @Autowired
    private ReportPurchaseRepository purchaseRepository;

    @Autowired
    private ReportRentalRepository rentalRepository;

    @GetMapping("/purchase")
    public ResponseEntity<PurchaseReportResponse> generatePurchaseReport() {
        logger.info("Generating purchase report");

        try {
            // Fetch all purchases
            List<ReportPurchase> purchases = purchaseRepository.findAll();
            logger.info("Fetched {} purchases", purchases.size());

            // Transform purchases into report entries
            List<PurchaseReportEntry> entries = purchases.stream().map(purchase -> {
                PurchaseReportEntry entry = new PurchaseReportEntry();
                entry.setId(purchase.getId());
                entry.setUserId(purchase.getUserId());
                entry.setVehicleId(purchase.getVehicleId());
                entry.setPurchaseDate(purchase.getPurchaseDate());
                entry.setPaymentMethod(purchase.getPaymentMethod());
                entry.setAmount(purchase.getAmount() != null ? purchase.getAmount() : 0.0);
                entry.setTransactionId(purchase.getTransactionId());
                entry.setStatus(purchase.getStatus());
                return entry;
            }).toList();

            PurchaseReportResponse response = new PurchaseReportResponse();
            response.setPurchases(entries);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error generating purchase report: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body(new PurchaseReportResponse("Internal server error: " + e.getMessage()));
        }
    }

    @GetMapping("/rental")
    public ResponseEntity<RentalReportResponse> generateRentalReport() {
        logger.info("Generating rental report");

        try {
            // Fetch all rentals
            List<ReportRental> rentals = rentalRepository.findAll();
            logger.info("Fetched {} rentals", rentals.size());

            // Transform rentals into report entries
            List<RentalReportEntry> entries = rentals.stream().map(rental -> {
                RentalReportEntry entry = new RentalReportEntry();
                entry.setId(rental.getId());
                entry.setUserId(rental.getUserId());
                entry.setVehicleId(rental.getVehicleId());
                entry.setStartDate(rental.getStartDate());
                entry.setEndDate(rental.getEndDate());
                entry.setReturnedDate(rental.getReturnedDate());
                entry.setStatus(rental.getStatus());
                return entry;
            }).toList();

            RentalReportResponse response = new RentalReportResponse();
            response.setRentals(entries);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error generating rental report: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body(new RentalReportResponse("Internal server error: " + e.getMessage()));
        }
    }
}

class PurchaseReportEntry {
    private Integer id;
    private Integer userId;
    private Integer vehicleId;
    private LocalDateTime purchaseDate;
    private String paymentMethod;
    private double amount;
    private String transactionId;
    private String status;

    // Getters and Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }
    public Integer getVehicleId() { return vehicleId; }
    public void setVehicleId(Integer vehicleId) { this.vehicleId = vehicleId; }
    public LocalDateTime getPurchaseDate() { return purchaseDate; }
    public void setPurchaseDate(LocalDateTime purchaseDate) { this.purchaseDate = purchaseDate; }
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}

class PurchaseReportResponse {
    private List<PurchaseReportEntry> purchases;
    private String error;

    // Constructors
    public PurchaseReportResponse() {
        this.purchases = null;
        this.error = null;
    }

    public PurchaseReportResponse(String error) {
        this.purchases = null;
        this.error = error;
    }

    // Getters and Setters
    public List<PurchaseReportEntry> getPurchases() { return purchases; }
    public void setPurchases(List<PurchaseReportEntry> purchases) { this.purchases = purchases; }
    public String getError() { return error; }
    public void setError(String error) { this.error = error; }
}

class RentalReportEntry {
    private Integer id;
    private Integer userId;
    private Integer vehicleId;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private LocalDateTime returnedDate;
    private String status;

    // Getters and Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }
    public Integer getVehicleId() { return vehicleId; }
    public void setVehicleId(Integer vehicleId) { this.vehicleId = vehicleId; }
    public LocalDateTime getStartDate() { return startDate; }
    public void setStartDate(LocalDateTime startDate) { this.startDate = startDate; }
    public LocalDateTime getEndDate() { return endDate; }
    public void setEndDate(LocalDateTime endDate) { this.endDate = endDate; }
    public LocalDateTime getReturnedDate() { return returnedDate; }
    public void setReturnedDate(LocalDateTime returnedDate) { this.returnedDate = returnedDate; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}

class RentalReportResponse {
    private List<RentalReportEntry> rentals;
    private String error;

    // Constructors
    public RentalReportResponse() {
        this.rentals = null;
        this.error = null;
    }

    public RentalReportResponse(String error) {
        this.rentals = null;
        this.error = error;
    }

    // Getters and Setters
    public List<RentalReportEntry> getRentals() { return rentals; }
    public void setRentals(List<RentalReportEntry> rentals) { this.rentals = rentals; }
    public String getError() { return error; }
    public void setError(String error) { this.error = error; }
}
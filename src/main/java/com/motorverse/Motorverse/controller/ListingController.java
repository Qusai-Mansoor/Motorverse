package com.motorverse.Motorverse.controller;

import com.motorverse.Motorverse.entity.AdminActionLog;
import com.motorverse.Motorverse.entity.Listing;
import com.motorverse.Motorverse.entity.Purchase;
import com.motorverse.Motorverse.entity.Rental;
import com.motorverse.Motorverse.entity.Vehicle;
import com.motorverse.Motorverse.repository.AdminActionLogRepository;
import com.motorverse.Motorverse.repository.ListingRepository;
import com.motorverse.Motorverse.repository.PurchaseRepository;
import com.motorverse.Motorverse.repository.RentalRepository;
import com.motorverse.Motorverse.repository.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/listings")
public class ListingController {
    @Autowired
    private ListingRepository listingRepository;

    @Autowired
    private VehicleRepository vehicleRepository;
    
    @Autowired
    private RentalRepository rentalRepository;
    
    @Autowired
    private PurchaseRepository purchaseRepository;

    @Autowired
    private AdminActionLogRepository adminActionLogRepository;

    // Get all active listings for sale
    @GetMapping("/buy")
    public List<Listing> getListingsForSale() {
        return listingRepository.findByListingTypeAndStatus(Listing.ListingType.SALE, Listing.ListingStatus.ACTIVE);
    }

    // Get all active listings for rent
    @GetMapping("/rent")
    public List<Listing> getListingsForRent() {
        return listingRepository.findByListingTypeAndStatus(Listing.ListingType.RENT, Listing.ListingStatus.ACTIVE);
    }

    // Get a listing by ID
    @GetMapping("/{id}")
    public ResponseEntity<Listing> getListingById(@PathVariable int id) {
        return listingRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Get all listings (for admin)
    @GetMapping("/all")
    public ResponseEntity<List<Listing>> getAllListings() {
        List<Listing> listings = listingRepository.findAll();
        return ResponseEntity.ok(listings);
    }

    // Create a new listing and sync with vehicles table
    @PostMapping
    public ResponseEntity<Listing> createListing(@RequestBody ListingRequest request) {
        // Validate user is logged in
        if (request.getUserId() <= 0) {
            throw new RuntimeException("User must be logged in to create a listing");
        }

        // Create Listing entity
        Listing listing = new Listing(
            request.getUserId(),
            request.getName(),
            request.getYear(),
            request.getPrice() != null ? request.getPrice() : 0.0,
            request.getRentRate() != null ? request.getRentRate() : 0.0,
            Listing.ListingType.valueOf(request.getListingType()),
            request.getDescription(),
            request.getPicture(),
            request.getLocation(),
            request.getMileage(),
            request.getFuelType() != null ? Listing.FuelType.valueOf(request.getFuelType()) : null,
            request.getTransmission() != null ? Listing.Transmission.valueOf(request.getTransmission()) : null,
            request.getCondition() != null ? Listing.Condition.valueOf(request.getCondition()) : null,
            request.getAvailableFrom(),
            request.getAvailableUntil()
        );

        // Save listing first
        Listing savedListing = listingRepository.save(listing);

        // Create corresponding Vehicle entity
        Vehicle vehicle = new Vehicle();
        vehicle.setName(request.getName());
        vehicle.setYear(request.getYear());
        vehicle.setPrice(request.getPrice() != null ? request.getPrice() : 0.0);
        vehicle.setRentRate(request.getRentRate() != null ? request.getRentRate() : 0.0);
        vehicle.setStatus(Vehicle.Status.AVAILABLE);
        vehicle.setDescription(request.getDescription());
        vehicle.setPicture(request.getPicture());

        // Save vehicle and link to listing
        Vehicle savedVehicle = vehicleRepository.save(vehicle);
        savedListing.setVehicleId(savedVehicle.getId());
        listingRepository.save(savedListing);

        return ResponseEntity.ok(savedListing);
    }

    // Update a listing (admin only) - Removed admin access check
    @PutMapping("/{id}")
    public ResponseEntity<String> updateListing(@PathVariable int id, @RequestBody Listing updatedListing) {
        Listing existingListing = listingRepository.findById(id).orElse(null);
        if (existingListing == null) {
            return ResponseEntity.badRequest().body("Listing not found");
        }

        // Update fields
        existingListing.setName(updatedListing.getName());
        existingListing.setYear(updatedListing.getYear());
        existingListing.setPrice(updatedListing.getPrice());
        existingListing.setRentRate(updatedListing.getRentRate());
        existingListing.setListingType(updatedListing.getListingType());
        existingListing.setStatus(updatedListing.getStatus());
        existingListing.setDescription(updatedListing.getDescription());
        existingListing.setPicture(updatedListing.getPicture());
        existingListing.setLocation(updatedListing.getLocation());
        existingListing.setMileage(updatedListing.getMileage());
        existingListing.setFuelType(updatedListing.getFuelType());
        existingListing.setTransmission(updatedListing.getTransmission());
        existingListing.setAvailableFrom(updatedListing.getAvailableFrom());
        existingListing.setAvailableUntil(updatedListing.getAvailableUntil());
        existingListing.setUpdatedAt(LocalDateTime.now());

        listingRepository.save(existingListing);

        // Log admin action
        AdminActionLog log = new AdminActionLog();
        log.setAdminId(1); // Simplified; using a default admin ID since token is removed
        log.setAction("UPDATE_LISTING");
        log.setEntityId(id);
        log.setEntityType("LISTING");
        log.setTimestamp(LocalDateTime.now());
        adminActionLogRepository.save(log);

        return ResponseEntity.ok("Listing updated successfully");
    }

    // Delete a listing (admin only) - Removed admin access check
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteListing(@PathVariable int id) {
        if (!listingRepository.existsById(id)) {
            return ResponseEntity.badRequest().body("Listing not found");
        }

        listingRepository.deleteById(id);

        // Log admin action
        AdminActionLog log = new AdminActionLog();
        log.setAdminId(1); // Simplified; using a default admin ID since token is removed
        log.setAction("DELETE_LISTING");
        log.setEntityId(id);
        log.setEntityType("LISTING");
        log.setTimestamp(LocalDateTime.now());
        adminActionLogRepository.save(log);

        return ResponseEntity.ok("Listing deleted successfully");
    }

    // Process a transaction (purchase or rental)
    @PostMapping("/transaction")
    public ResponseEntity<?> processTransaction(@RequestBody TransactionRequest request) {
        try {
            // Validate request
            if (request.getUserId() <= 0 || request.getListingId() <= 0) {
                return ResponseEntity.badRequest().body("Invalid user or listing ID");
            }

            // Get the listing
            Listing listing = listingRepository.findById(request.getListingId())
                .orElseThrow(() -> new RuntimeException("Listing not found"));

            // Check if listing is still active
            if (listing.getStatus() != Listing.ListingStatus.ACTIVE) {
                return ResponseEntity.badRequest().body("This listing is no longer available");
            }
            
            // Get the associated vehicle
            if (listing.getVehicleId() == null) {
                return ResponseEntity.badRequest().body("No vehicle associated with this listing");
            }
            
            Vehicle vehicle = vehicleRepository.findById(listing.getVehicleId())
                .orElseThrow(() -> new RuntimeException("Associated vehicle not found"));

            // Generate transaction ID
            String transactionId = generateTransactionId();
            
            // Record the transaction based on type
            if ("buy".equalsIgnoreCase(request.getTransactionType())) {
                // Create purchase record
                Purchase purchase = new Purchase();
                purchase.setUserId(request.getUserId());
                purchase.setVehicleId(vehicle.getId());
                purchase.setPurchaseDate(LocalDateTime.now());
                purchase.setPaymentMethod(Purchase.PaymentMethod.valueOf(request.getPaymentMethod()));
                purchase.setAmount(request.getAmount());
                purchase.setTransactionId(transactionId);
                purchase.setStatus(Purchase.Status.COMPLETED);
                
                purchaseRepository.save(purchase);
                
                // Update listing status
                listing.setStatus(Listing.ListingStatus.SOLD);
                
                // Update vehicle status
                vehicle.setStatus(Vehicle.Status.SOLD);
            } 
            else if ("rent".equalsIgnoreCase(request.getTransactionType())) {
                if (request.getRentalDays() == null || request.getRentalDays() <= 0) {
                    return ResponseEntity.badRequest().body("Invalid rental duration");
                }
                
                // Calculate rental end date
                LocalDateTime endDate = LocalDateTime.now().plusDays(request.getRentalDays());
                
                // Create rental record
                Rental rental = new Rental();
                rental.setUserId(request.getUserId());
                rental.setVehicleId(vehicle.getId());
                rental.setStartDate(LocalDateTime.now());
                rental.setEndDate(endDate);
                rental.setStatus(Rental.Status.RENTED);
                
                rentalRepository.save(rental);
                
                // Also create a purchase record for the payment
                Purchase purchase = new Purchase();
                purchase.setUserId(request.getUserId());
                purchase.setVehicleId(vehicle.getId());
                purchase.setPurchaseDate(LocalDateTime.now());
                purchase.setPaymentMethod(Purchase.PaymentMethod.valueOf(request.getPaymentMethod()));
                purchase.setAmount(request.getAmount());
                purchase.setTransactionId(transactionId);
                purchase.setStatus(Purchase.Status.COMPLETED);
                
                purchaseRepository.save(purchase);
                
                // Update listing status
                listing.setStatus(Listing.ListingStatus.RENTED);
                
                // Update vehicle status
                vehicle.setStatus(Vehicle.Status.RENTED);
            } 
            else {
                return ResponseEntity.badRequest().body("Invalid transaction type: " + request.getTransactionType());
            }

            // Save updated listing and vehicle
            listingRepository.save(listing);
            vehicleRepository.save(vehicle);

            // Create response
            TransactionResponse response = new TransactionResponse();
            response.setTransactionId(transactionId);
            response.setListingId(listing.getId());
            response.setUserId(request.getUserId());
            response.setAmount(request.getAmount());
            response.setTransactionType(request.getTransactionType());
            response.setTimestamp(LocalDateTime.now());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Transaction failed: " + e.getMessage());
        }
    }
    
    // Helper method to generate a transaction ID
    private String generateTransactionId() {
        return "TRX-" + System.currentTimeMillis() + "-" + (int)(Math.random() * 10000);
    }
}

class ListingRequest {
    private int userId;
    private String name;
    private int year;
    private Double price;
    private Double rentRate;
    private String listingType; // "SALE" or "RENT"
    private String description;
    private String picture;
    private String location;
    private Integer mileage;
    private String fuelType; // Optional: "PETROL", "DIESEL", etc.
    private String transmission; // Optional: "MANUAL", "AUTOMATIC"
    private String condition; // Optional: "NEW", "USED", "CERTIFIED_PRE_OWNED"
    private LocalDateTime availableFrom;
    private LocalDateTime availableUntil;

    // Getters and Setters
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }
    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }
    public Double getRentRate() { return rentRate; }
    public void setRentRate(Double rentRate) { this.rentRate = rentRate; }
    public String getListingType() { return listingType; }
    public void setListingType(String listingType) { this.listingType = listingType; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getPicture() { return picture; }
    public void setPicture(String picture) { this.picture = picture; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public Integer getMileage() { return mileage; }
    public void setMileage(Integer mileage) { this.mileage = mileage; }
    public String getFuelType() { return fuelType; }
    public void setFuelType(String fuelType) { this.fuelType = fuelType; }
    public String getTransmission() { return transmission; }
    public void setTransmission(String transmission) { this.transmission = transmission; }
    public String getCondition() { return condition; }
    public void setCondition(String condition) { this.condition = condition; }
    public LocalDateTime getAvailableFrom() { return availableFrom; }
    public void setAvailableFrom(LocalDateTime availableFrom) { this.availableFrom = availableFrom; }
    public LocalDateTime getAvailableUntil() { return availableUntil; }
    public void setAvailableUntil(LocalDateTime availableUntil) { this.availableUntil = availableUntil; }
}

// Request class for transactions
class TransactionRequest {
    private int userId;
    private int listingId;
    private String transactionType; // "BUY" or "RENT"
    private String paymentMethod;
    private double amount;
    private Integer rentalDays; // Optional, for rentals only

    // Getters and Setters
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public int getListingId() { return listingId; }
    public void setListingId(int listingId) { this.listingId = listingId; }
    public String getTransactionType() { return transactionType; }
    public void setTransactionType(String transactionType) { this.transactionType = transactionType; }
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
    public Integer getRentalDays() { return rentalDays; }
    public void setRentalDays(Integer rentalDays) { this.rentalDays = rentalDays; }
}

// Response class for successful transactions
class TransactionResponse {
    private String transactionId;
    private int listingId;
    private int userId;
    private double amount;
    private String transactionType;
    private LocalDateTime timestamp;

    // Getters and Setters
    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }
    public int getListingId() { return listingId; }
    public void setListingId(int listingId) { this.listingId = listingId; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
    public String getTransactionType() { return transactionType; }
    public void setTransactionType(String transactionType) { this.transactionType = transactionType; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}
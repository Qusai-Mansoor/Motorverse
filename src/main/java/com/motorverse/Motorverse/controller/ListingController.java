package com.motorverse.Motorverse.controller;

import com.motorverse.Motorverse.entity.Listing;
import com.motorverse.Motorverse.entity.Vehicle;
import com.motorverse.Motorverse.repository.ListingRepository;
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
        vehicle.setPrice(request.getPrice()!=null ? request.getPrice() : 0.0);
        vehicle.setRentRate(request.getRentRate()!= null ? request.getRentRate() : 0.0);
        vehicle.setStatus(Vehicle.Status.AVAILABLE);
        vehicle.setDescription(request.getDescription());
        vehicle.setPicture(request.getPicture());

        // Save vehicle and link to listing
        Vehicle savedVehicle = vehicleRepository.save(vehicle);
        savedListing.setVehicleId(savedVehicle.getId());
        listingRepository.save(savedListing);

        return ResponseEntity.ok(savedListing);
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
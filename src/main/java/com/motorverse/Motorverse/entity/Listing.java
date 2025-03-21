package com.motorverse.Motorverse.entity;


import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "listings")
public class Listing {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "user_id", nullable = false)
    private int userId;

    @Column(name = "vehicle_id")
    private Integer vehicleId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private int year;

    @Column
    private Double price;

    @Column(name = "rent_rate")
    private Double rentRate;

    @Enumerated(EnumType.STRING)
    @Column(name = "listing_type", nullable = false)
    private ListingType listingType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ListingStatus status = ListingStatus.ACTIVE;

    private String description;

    private String picture;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    private String location;

    private Integer mileage;

    @Enumerated(EnumType.STRING)
    @Column(name = "fuel_type")
    private FuelType fuelType;

    @Enumerated(EnumType.STRING)
    private Transmission transmission;

    @Column(name = "available_from")
    private LocalDateTime availableFrom;

    @Column(name = "available_until")
    private LocalDateTime availableUntil;

    // Enums
    public enum ListingType { SALE, RENT }
    public enum ListingStatus { ACTIVE, RENTED, SOLD, INACTIVE }
    public enum FuelType { PETROL, DIESEL, ELECTRIC, HYBRID }
    public enum Transmission { MANUAL, AUTOMATIC }
    public enum Condition { NEW, USED, CERTIFIED_PRE_OWNED }

    // Constructors
    public Listing() {}

    public Listing(int userId, String name, int year, Double price, Double rentRate, ListingType listingType,
                   String description, String picture, String location, Integer mileage, FuelType fuelType,
                   Transmission transmission, Condition condition, LocalDateTime availableFrom, LocalDateTime availableUntil) {
        this.userId = userId;
        this.name = name;
        this.year = year;
        this.price = price;
        this.rentRate = rentRate;
        this.listingType = listingType;
        this.description = description;
        this.picture = picture;
        this.location = location;
        this.mileage = mileage;
        this.fuelType = fuelType;
        this.transmission = transmission;
        this.availableFrom = availableFrom;
        this.availableUntil = availableUntil;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public Integer getVehicleId() { return vehicleId; }
    public void setVehicleId(Integer vehicleId) { this.vehicleId = vehicleId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }
    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }
    public Double getRentRate() { return rentRate; }
    public void setRentRate(Double rentRate) { this.rentRate = rentRate; }
    public ListingType getListingType() { return listingType; }
    public void setListingType(ListingType listingType) { this.listingType = listingType; }
    public ListingStatus getStatus() { return status; }
    public void setStatus(ListingStatus status) { this.status = status; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getPicture() { return picture; }
    public void setPicture(String picture) { this.picture = picture; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public Integer getMileage() { return mileage; }
    public void setMileage(Integer mileage) { this.mileage = mileage; }
    public FuelType getFuelType() { return fuelType; }
    public void setFuelType(FuelType fuelType) { this.fuelType = fuelType; }
    public Transmission getTransmission() { return transmission; }
    public void setTransmission(Transmission transmission) { this.transmission = transmission; }
    public LocalDateTime getAvailableFrom() { return availableFrom; }
    public void setAvailableFrom(LocalDateTime availableFrom) { this.availableFrom = availableFrom; }
    public LocalDateTime getAvailableUntil() { return availableUntil; }
    public void setAvailableUntil(LocalDateTime availableUntil) { this.availableUntil = availableUntil; }
}

package com.motorverse.Motorverse.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "rentals")
public class Rental {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "user_id", nullable = false)
    private int userId;

    @Column(name = "vehicle_id", nullable = false)
    private int vehicleId;

    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate = LocalDateTime.now();

    @Column(name = "end_date")
    private LocalDateTime endDate;
    
    @Column(name = "returned_date")
    private LocalDateTime returnedDate;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status = Status.RENTED;
    
    // Enum for rental status
    public enum Status {
        RENTED, RETURNED
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "insurance")
    private Insurance insurance = Insurance.NONE;

    public enum Insurance {
        NONE, BASIC, PREMIUM, COMPREHENSIVE
    }

    @Column(name="insurance_amount")
    private double insurance_amount;

    

    // Constructors
    public Rental() {}

    public Rental(int userId, int vehicleId, LocalDateTime endDate) {
        this.userId = userId;
        this.vehicleId = vehicleId;
        this.endDate = endDate;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public int getVehicleId() { return vehicleId; }
    public void setVehicleId(int vehicleId) { this.vehicleId = vehicleId; }
    public LocalDateTime getStartDate() { return startDate; }
    public void setStartDate(LocalDateTime startDate) { this.startDate = startDate; }
    public LocalDateTime getEndDate() { return endDate; }
    public void setEndDate(LocalDateTime endDate) { this.endDate = endDate; }
    public LocalDateTime getReturnedDate() { return returnedDate; }
    public void setReturnedDate(LocalDateTime returnedDate) { this.returnedDate = returnedDate; }
    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }
    public Insurance getInsurance() { return insurance; }
    public void setInsurance(Insurance insurance) { this.insurance = insurance; }
    public double getInsurance_amount() { return insurance_amount; }
    public void setInsurance_amount(double insurance_amount) { this.insurance_amount = insurance_amount; }

}
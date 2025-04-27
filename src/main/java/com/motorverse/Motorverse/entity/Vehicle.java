package com.motorverse.Motorverse.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "vehicles")
public class Vehicle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name")
    private String name;

    @Column(name = "year")
    private int year;

    @Column(name = "price")
    private double price;

    @Column(name = "rent_rate")
    private double rentRate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private Status status;

    @Column(name = "picture")  // Stores picture path
    private String picture;

    @Column(name = "description")  // New field
    private String description;

    // Enum for status
    public enum Status {
        AVAILABLE, SOLD, RENTED
    }

    // Getters and Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    public double getRentRate() { return rentRate; }
    public void setRentRate(double rentRate) { this.rentRate = rentRate; }
    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }
    public String getPicture() { return picture; }
    public void setPicture(String picture) { this.picture = picture; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
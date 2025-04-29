package com.motorverse.Motorverse.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
public class Payment {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(nullable = false)
    private BigDecimal amount;
    
    @Column(nullable = false)
    private LocalDateTime paymentDate;
    
    @Column(nullable = false)
    private String paymentMethod;
    
    @Column(nullable = false)
    private String paymentStatus;
    
    @Column(nullable = false)
    private String paymentType; // CAR, RENTAL, AUTOPART
    
    // For auto parts
    private Integer autoPartId;
    private Integer quantity;
    
    // For cars/rentals
    private Integer listingId;
    private Integer userId;
    
    // Reference number for the transaction
    private String referenceNumber;

    // Constructors
    public Payment() {
        this.paymentDate = LocalDateTime.now();
        this.amount = BigDecimal.ZERO; // Default value
        this.autoPartId = 0; // Default value
        this.quantity = 0; // Default value
        this.listingId = 0; // Default value

       
    }

    
    
    // Getters and setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public LocalDateTime getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDateTime paymentDate) {
        this.paymentDate = paymentDate;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public Integer getAutoPartId() {
        return autoPartId;
    }

    public void setAutoPartId(Integer autoPartId) {
        this.autoPartId = autoPartId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Integer getListingId() {
        return listingId;
    }

    public void setListingId(Integer listingId) {
        this.listingId = listingId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getReferenceNumber() {
        return referenceNumber;
    }

    public void setReferenceNumber(String referenceNumber) {
        this.referenceNumber = referenceNumber;
    }
}

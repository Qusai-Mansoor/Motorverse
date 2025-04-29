package com.motorverse.Motorverse.controller;

import com.motorverse.Motorverse.entity.Purchase;
import com.motorverse.Motorverse.entity.Rental;
import com.motorverse.Motorverse.entity.Vehicle;
import com.motorverse.Motorverse.repository.VehicleRepository;
import com.motorverse.Motorverse.repository.PurchaseRepository;
import com.motorverse.Motorverse.repository.RentalRepository;
import com.motorverse.Motorverse.repository.ListingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class VehicleControllerTest {

    @Mock
    private VehicleRepository vehicleRepository;
    
    @Mock
    private PurchaseRepository purchaseRepository;
    
    @Mock
    private RentalRepository rentalRepository;
    
    @Mock
    private ListingRepository listingRepository;
    
    @Mock
    private MultipartFile mockMultipartFile;
    
    @InjectMocks
    private VehicleController vehicleController;
    
    private Vehicle testVehicle;
    private Purchase testPurchase;
    private Rental testRental;
    
    @BeforeEach
    void setUp() {
        // Set up test vehicle
        testVehicle = new Vehicle();
        testVehicle.setId(1);
        testVehicle.setName("Test Vehicle");
        testVehicle.setYear(2023);
        testVehicle.setPrice(25000.0);
        testVehicle.setRentRate(100.0);
        testVehicle.setDescription("Test description");
        testVehicle.setStatus(Vehicle.Status.AVAILABLE);
        testVehicle.setPicture("test.jpg");
        
        // Set up test purchase
        testPurchase = new Purchase();
        testPurchase.setId(1);
        testPurchase.setUserId(1);
        testPurchase.setVehicleId(1);
        testPurchase.setAmount(25000.0);
        testPurchase.setPaymentMethod(Purchase.PaymentMethod.CREDIT_CARD);
        testPurchase.setTransactionId("TXN12345");
        testPurchase.setStatus(Purchase.Status.COMPLETED);
        testPurchase.setPurchaseDate(LocalDateTime.now());
        
        // Set up test rental
        testRental = new Rental();
        testRental.setId(1);
        testRental.setUserId(1);
        testRental.setVehicleId(1);
        testRental.setStartDate(LocalDateTime.now());
        testRental.setEndDate(LocalDateTime.now().plusDays(5));
        testRental.setStatus(Rental.Status.RENTED);
    }
    
    @Test
    void getVehiclesForSale_ShouldReturnAvailableVehicles() {
        // Arrange
        List<Vehicle> availableVehicles = new ArrayList<>();
        availableVehicles.add(testVehicle);
        when(vehicleRepository.findByStatus(Vehicle.Status.AVAILABLE)).thenReturn(availableVehicles);
        
        // Act
        List<Vehicle> result = vehicleController.getVehiclesForSale();
        
        // Assert
        assertEquals(1, result.size());
        assertEquals(testVehicle.getId(), result.get(0).getId());
        verify(vehicleRepository).findByStatus(Vehicle.Status.AVAILABLE);
    }
    
    @Test
    void getVehicleById_WithValidId_ShouldReturnVehicle() {
        // Arrange
        when(vehicleRepository.findById(1)).thenReturn(Optional.of(testVehicle));
        
        // Act
        Vehicle result = vehicleController.getVehicleById(1);
        
        // Assert
        assertNotNull(result);
        assertEquals(testVehicle.getId(), result.getId());
        verify(vehicleRepository).findById(1);
    }
    
    @Test
    void getVehicleById_WithInvalidId_ShouldThrowException() {
        // Arrange
        when(vehicleRepository.findById(999)).thenReturn(Optional.empty());
        
        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> {
            vehicleController.getVehicleById(999);
        });
        
        assertEquals("Vehicle not found", exception.getMessage());
        verify(vehicleRepository).findById(999);
    }
    
    @Test
    void purchaseVehicle_WithValidRequest_ShouldCompletePurchase() {
        // Arrange
        PurchaseRequest request = new PurchaseRequest();
        request.setUserId(1);
        request.setVehicleId(1);
        request.setPaymentMethod("CREDIT_CARD");
        
        when(vehicleRepository.findById(1)).thenReturn(Optional.of(testVehicle));
        when(vehicleRepository.save(any(Vehicle.class))).thenReturn(testVehicle);
        when(purchaseRepository.save(any(Purchase.class))).thenReturn(testPurchase);
        
        // Act
        Purchase result = vehicleController.purchaseVehicle(request);
        
        // Assert
        assertNotNull(result);
        assertEquals(testPurchase.getUserId(), result.getUserId());
        assertEquals(testPurchase.getVehicleId(), result.getVehicleId());
        assertEquals(testPurchase.getStatus(), result.getStatus());
        
        verify(vehicleRepository).findById(1);
        verify(vehicleRepository).save(any(Vehicle.class));
        verify(purchaseRepository).save(any(Purchase.class));
    }
    
    @Test
    void purchaseVehicle_WithInvalidUserId_ShouldThrowException() {
        // Arrange
        PurchaseRequest request = new PurchaseRequest();
        request.setUserId(0); // Invalid user ID
        request.setVehicleId(1);
        request.setPaymentMethod("CREDIT_CARD");
        
        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> {
            vehicleController.purchaseVehicle(request);
        });
        
        assertEquals("User must be logged in", exception.getMessage());
        verify(vehicleRepository, never()).findById(anyInt());
    }
    
    @Test
    void rentVehicle_WithValidRequest_ShouldCompleteRental() {
        // Arrange
        RentalRequest request = new RentalRequest();
        request.setUserId(1);
        request.setVehicleId(1);
        request.setDays(5);
        request.setPaymentMethod("CREDIT_CARD");
        
        when(vehicleRepository.findById(1)).thenReturn(Optional.of(testVehicle));
        when(vehicleRepository.save(any(Vehicle.class))).thenReturn(testVehicle);
        when(rentalRepository.save(any(Rental.class))).thenReturn(testRental);
        
        // Act
        Rental result = vehicleController.rentVehicle(request);
        
        // Assert
        assertNotNull(result);
        assertEquals(testRental.getUserId(), result.getUserId());
        assertEquals(testRental.getVehicleId(), result.getVehicleId());
        
        verify(vehicleRepository).findById(1);
        verify(vehicleRepository).save(any(Vehicle.class));
        verify(rentalRepository).save(any(Rental.class));
    }
    
    @Test
    void getUserRentals_ShouldReturnUserRentals() {
        // Arrange
        List<Rental> userRentals = new ArrayList<>();
        userRentals.add(testRental);
        
        when(rentalRepository.findByUserIdAndStatus(1, Rental.Status.RENTED)).thenReturn(userRentals);
        when(vehicleRepository.findById(1)).thenReturn(Optional.of(testVehicle));
        
        // Act
        List<Map<String, Object>> result = vehicleController.getUserRentals(1);
        
        // Assert
        assertEquals(1, result.size());
        Map<String, Object> rentalDetails = result.get(0);
        assertEquals(testRental, rentalDetails.get("rental"));
        assertEquals(testVehicle, rentalDetails.get("vehicle"));
        
        verify(rentalRepository).findByUserIdAndStatus(1, Rental.Status.RENTED);
        verify(vehicleRepository).findById(1);
    }
    
    @Test
    void returnVehicle_WithValidRequest_ShouldProcessReturn() {
        // Arrange
        ReturnVehicleRequest request = new ReturnVehicleRequest();
        request.setRentalId(1);
        request.setDamagePercentage(0.0);
        
        when(rentalRepository.findById(1)).thenReturn(Optional.of(testRental));
        when(vehicleRepository.findById(1)).thenReturn(Optional.of(testVehicle));
        
        // Act
        Map<String, Object> result = vehicleController.returnVehicle(request);
        
        // Assert
        assertTrue((Boolean) result.get("success"));
        assertEquals(0.0, (Double) result.get("additionalCharges"), 0.01);
        
        verify(rentalRepository).findById(1);
        verify(vehicleRepository).findById(1);
        verify(rentalRepository).save(any(Rental.class));
        verify(vehicleRepository).save(any(Vehicle.class));
        verify(listingRepository).findByVehicleId(1);
    }
}

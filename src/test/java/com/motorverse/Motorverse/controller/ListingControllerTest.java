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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ListingControllerTest {

    @Mock
    private ListingRepository listingRepository;
    
    @Mock
    private VehicleRepository vehicleRepository;
    
    @Mock
    private RentalRepository rentalRepository;
    
    @Mock
    private PurchaseRepository purchaseRepository;
    
    @Mock
    private AdminActionLogRepository adminActionLogRepository;
    
    @InjectMocks
    private ListingController listingController;
    
    private Listing testListing;
    private Vehicle testVehicle;
    
    @BeforeEach
    void setUp() {
        // Set up test listing
        testListing = new Listing();
        testListing.setId(1);
        testListing.setUserId(1);
        testListing.setName("Test Listing");
        testListing.setYear(2023);
        testListing.setPrice(25000.0);
        testListing.setRentRate(100.0);
        testListing.setListingType(Listing.ListingType.SALE);
        testListing.setStatus(Listing.ListingStatus.ACTIVE);
        testListing.setDescription("Test description");
        testListing.setPicture("test.jpg");
        testListing.setLocation("Test Location");
        testListing.setCreatedAt(LocalDateTime.now());
        testListing.setUpdatedAt(LocalDateTime.now());
        
        // Set up test vehicle
        testVehicle = new Vehicle();
        testVehicle.setId(1);
        testVehicle.setName("Test Vehicle");
        testVehicle.setYear(2023);
        testVehicle.setPrice(25000.0);
        testVehicle.setRentRate(100.0);
        testVehicle.setStatus(Vehicle.Status.AVAILABLE);
        testVehicle.setDescription("Test description");
        testVehicle.setPicture("test.jpg");
    }
    
    @Test
    void getListingsForSale_ShouldReturnActiveListings() {
        // Arrange
        List<Listing> activeListings = new ArrayList<>();
        activeListings.add(testListing);
        when(listingRepository.findByListingTypeAndStatus(Listing.ListingType.SALE, Listing.ListingStatus.ACTIVE))
            .thenReturn(activeListings);
        
        // Act
        List<Listing> result = listingController.getListingsForSale();
        
        // Assert
        assertEquals(1, result.size());
        assertEquals(testListing.getId(), result.get(0).getId());
        verify(listingRepository).findByListingTypeAndStatus(Listing.ListingType.SALE, Listing.ListingStatus.ACTIVE);
    }
    
    @Test
    void getListingsForRent_ShouldReturnActiveListings() {
        // Arrange
        List<Listing> activeListings = new ArrayList<>();
        testListing.setListingType(Listing.ListingType.RENT);
        activeListings.add(testListing);
        when(listingRepository.findByListingTypeAndStatus(Listing.ListingType.RENT, Listing.ListingStatus.ACTIVE))
            .thenReturn(activeListings);
        
        // Act
        List<Listing> result = listingController.getListingsForRent();
        
        // Assert
        assertEquals(1, result.size());
        assertEquals(testListing.getId(), result.get(0).getId());
        verify(listingRepository).findByListingTypeAndStatus(Listing.ListingType.RENT, Listing.ListingStatus.ACTIVE);
    }
    
    @Test
    void getListingById_WithValidId_ShouldReturnListing() {
        // Arrange
        when(listingRepository.findById(1)).thenReturn(Optional.of(testListing));
        
        // Act
        ResponseEntity<Listing> response = listingController.getListingById(1);
        
        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testListing, response.getBody());
        verify(listingRepository).findById(1);
    }
    
    @Test
    void getListingById_WithInvalidId_ShouldReturnNotFound() {
        // Arrange
        when(listingRepository.findById(999)).thenReturn(Optional.empty());
        
        // Act
        ResponseEntity<Listing> response = listingController.getListingById(999);
        
        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(listingRepository).findById(999);
    }
    
    @Test
    void getAllListings_ShouldReturnAllListings() {
        // Arrange
        List<Listing> allListings = new ArrayList<>();
        allListings.add(testListing);
        when(listingRepository.findAll()).thenReturn(allListings);
        
        // Act
        ResponseEntity<List<Listing>> response = listingController.getAllListings();
        
        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals(testListing.getId(), response.getBody().get(0).getId());
        verify(listingRepository).findAll();
    }
    
    @Test
    void createListing_WithValidRequest_ShouldCreateListing() {
        // Arrange
        ListingRequest request = new ListingRequest();
        request.setUserId(1);
        request.setName("New Listing");
        request.setYear(2023);
        request.setPrice(25000.0);
        request.setRentRate(100.0);
        request.setListingType("SALE");
        request.setDescription("New description");
        request.setPicture("new.jpg");
        request.setLocation("New Location");
        
        when(listingRepository.save(any(Listing.class))).thenAnswer(invocation -> {
            Listing savedListing = invocation.getArgument(0);
            savedListing.setId(2);
            return savedListing;
        });
        
        when(vehicleRepository.save(any(Vehicle.class))).thenAnswer(invocation -> {
            Vehicle savedVehicle = invocation.getArgument(0);
            savedVehicle.setId(2);
            return savedVehicle;
        });
        
        // Act
        ResponseEntity<Listing> response = listingController.createListing(request);
        
        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().getId());
        assertEquals(request.getName(), response.getBody().getName());
        
        verify(listingRepository, times(2)).save(any(Listing.class));
        verify(vehicleRepository).save(any(Vehicle.class));
    }
    
    @Test
    void updateListing_WithValidRequest_ShouldUpdateListing() {
        // Arrange
        when(listingRepository.findById(1)).thenReturn(Optional.of(testListing));
        when(listingRepository.save(any(Listing.class))).thenReturn(testListing);
        when(adminActionLogRepository.save(any(AdminActionLog.class))).thenReturn(new AdminActionLog());
        
        // Update listing with new values
        Listing updatedListing = new Listing();
        updatedListing.setName("Updated Listing");
        updatedListing.setYear(2024);
        updatedListing.setPrice(30000.0);
        
        // Act
        ResponseEntity<String> response = listingController.updateListing(1, updatedListing);
        
        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Listing updated successfully", response.getBody());
        
        verify(listingRepository).findById(1);
        verify(listingRepository).save(any(Listing.class));
        verify(adminActionLogRepository).save(any(AdminActionLog.class));
    }
    
    @Test
    void updateListing_WithInvalidId_ShouldReturnBadRequest() {
        // Arrange
        when(listingRepository.findById(999)).thenReturn(Optional.empty());
        
        // Act
        ResponseEntity<String> response = listingController.updateListing(999, new Listing());
        
        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Listing not found", response.getBody());
        
        verify(listingRepository).findById(999);
        verify(listingRepository, never()).save(any(Listing.class));
        verify(adminActionLogRepository, never()).save(any(AdminActionLog.class));
    }
    
    @Test
    void deleteListing_WithValidId_ShouldDeleteListing() {
        // Arrange
        when(listingRepository.existsById(1)).thenReturn(true);
        doNothing().when(listingRepository).deleteById(1);
        when(adminActionLogRepository.save(any(AdminActionLog.class))).thenReturn(new AdminActionLog());
        
        // Act
        ResponseEntity<String> response = listingController.deleteListing(1);
        
        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Listing deleted successfully", response.getBody());
        
        verify(listingRepository).existsById(1);
        verify(listingRepository).deleteById(1);
        verify(adminActionLogRepository).save(any(AdminActionLog.class));
    }
    
    @Test
    void deleteListing_WithInvalidId_ShouldReturnBadRequest() {
        // Arrange
        when(listingRepository.existsById(999)).thenReturn(false);
        
        // Act
        ResponseEntity<String> response = listingController.deleteListing(999);
        
        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Listing not found", response.getBody());
        
        verify(listingRepository).existsById(999);
        verify(listingRepository, never()).deleteById(anyInt());
        verify(adminActionLogRepository, never()).save(any(AdminActionLog.class));
    }
       
@Test
void processTransaction_ForRental_ShouldProcessSuccessfully() {
    // Arrange
    TransactionRequest request = new TransactionRequest();
    testListing.setListingType(Listing.ListingType.RENT);
    testListing.setVehicleId(1);

    request.setInsuranceAmount(75.0);
    request.setInsurance("BASIC");
    request.setRentalDays(5);
    request.setAmount(500.0);
    request.setPaymentMethod("CREDIT_CARD");
    request.setTransactionType("RENT");
    request.setListingId(1);
    request.setUserId(1);

    when(vehicleRepository.findById(1)).thenReturn(Optional.of(testVehicle));
    when(listingRepository.findById(1)).thenReturn(Optional.of(testListing));
    when(vehicleRepository.save(any(Vehicle.class))).thenReturn(testVehicle);
    when(listingRepository.save(any(Listing.class))).thenReturn(testListing);
    when(purchaseRepository.save(any(Purchase.class))).thenAnswer(invocation -> invocation.getArgument(0));
    when(rentalRepository.save(any(Rental.class))).thenAnswer(invocation -> invocation.getArgument(0));

    // Act
    ResponseEntity<?> response = listingController.processTransaction(request);

    // Assert
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(Vehicle.Status.RENTED, testVehicle.getStatus());
    assertEquals(Listing.ListingStatus.RENTED, testListing.getStatus());

    // Verify status changes
    verify(vehicleRepository).save(testVehicle);
    verify(listingRepository).save(testListing);
    verify(purchaseRepository).save(any(Purchase.class));
    verify(rentalRepository).save(any(Rental.class));
}

@Test
void processTransaction_ForPurchase_ShouldProcessSuccessfully() {
    // Arrange
    TransactionRequest request = new TransactionRequest();
    testListing.setVehicleId(1);

    request.setAmount(25000.0);
    request.setPaymentMethod("CREDIT_CARD");
    request.setTransactionType("BUY");
    request.setListingId(1);
    request.setUserId(1);

    when(vehicleRepository.findById(1)).thenReturn(Optional.of(testVehicle));
    when(listingRepository.findById(1)).thenReturn(Optional.of(testListing));
    when(vehicleRepository.save(any(Vehicle.class))).thenReturn(testVehicle);
    when(listingRepository.save(any(Listing.class))).thenReturn(testListing);
    when(purchaseRepository.save(any(Purchase.class))).thenAnswer(invocation -> invocation.getArgument(0));

    // Act
    ResponseEntity<?> response = listingController.processTransaction(request);

    // Assert
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(Vehicle.Status.SOLD, testVehicle.getStatus());
    assertEquals(Listing.ListingStatus.SOLD, testListing.getStatus());

    // Verify status changes
    verify(vehicleRepository).save(testVehicle);
    verify(listingRepository).save(testListing);
    verify(purchaseRepository).save(any(Purchase.class));
}

    @Test
    void updateListingStatusByVehicleId_WithNoListings_ShouldReturnNotFound() {
        // Arrange
        int vehicleId = 999;
        when(listingRepository.findByVehicleId(vehicleId)).thenReturn(new ArrayList<>());
        
        ListingController.StatusRequest statusRequest = new ListingController.StatusRequest();
        statusRequest.setStatus("ACTIVE");
        
        // Act
        ResponseEntity<?> response = listingController.updateListingStatusByVehicleId(vehicleId, statusRequest);
        
        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        
        // Verify repository interactions
        verify(listingRepository).findByVehicleId(vehicleId);
        verify(listingRepository, never()).save(any(Listing.class));
        verify(vehicleRepository, never()).save(any(Vehicle.class));
    }
}

package com.motorverse.Motorverse.controller;

import com.motorverse.Motorverse.entity.ReportPurchase;
import com.motorverse.Motorverse.entity.ReportRental;
import com.motorverse.Motorverse.repository.ReportPurchaseRepository;
import com.motorverse.Motorverse.repository.ReportRentalRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReportControllerTest {

    @Mock
    private ReportPurchaseRepository purchaseRepository;

    @Mock
    private ReportRentalRepository rentalRepository;

    @InjectMocks
    private ReportController reportController;

    private ReportPurchase testPurchase1;
    private ReportPurchase testPurchase2;
    private ReportRental testRental1;

    @BeforeEach
    void setUp() {
        testPurchase1 = new ReportPurchase();
        testPurchase1.setId(1);
        testPurchase1.setUserId(10);
        testPurchase1.setVehicleId(100);
        testPurchase1.setPurchaseDate(LocalDateTime.now().minusDays(1));
        testPurchase1.setAmount(20000.0);
        testPurchase1.setStatus("COMPLETED");
        testPurchase1.setPaymentMethod("CREDIT_CARD");
        testPurchase1.setTransactionId("TXN1");

        testPurchase2 = new ReportPurchase();
        testPurchase2.setId(2);
        testPurchase2.setUserId(11);
        testPurchase2.setVehicleId(101);
        testPurchase2.setPurchaseDate(LocalDateTime.now().minusHours(5));
        testPurchase2.setAmount(1500.0); // Test with null amount handling in controller
        testPurchase2.setStatus("PENDING");
        testPurchase2.setPaymentMethod("PAYPAL");
        testPurchase2.setTransactionId("TXN2");

        testRental1 = new ReportRental();
        testRental1.setId(1);
        testRental1.setUserId(20);
        testRental1.setVehicleId(200);
        testRental1.setStartDate(LocalDateTime.now().minusDays(10));
        testRental1.setEndDate(LocalDateTime.now().minusDays(3));
        testRental1.setReturnedDate(LocalDateTime.now().minusDays(2));
        testRental1.setStatus("RETURNED");
    }

    @Test
    void generatePurchaseReport_Success() {
        when(purchaseRepository.findAll()).thenReturn(Arrays.asList(testPurchase1, testPurchase2));

        ResponseEntity<PurchaseReportResponse> response = reportController.generatePurchaseReport();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNull(response.getBody().getError());
        assertEquals(2, response.getBody().getPurchases().size());

        PurchaseReportEntry entry1 = response.getBody().getPurchases().get(0);
        assertEquals(testPurchase1.getId(), entry1.getId());
        assertEquals(testPurchase1.getAmount(), entry1.getAmount());
        assertEquals(testPurchase1.getStatus(), entry1.getStatus());

        PurchaseReportEntry entry2 = response.getBody().getPurchases().get(1);
        assertEquals(testPurchase2.getId(), entry2.getId());
        assertEquals(testPurchase2.getAmount(), entry2.getAmount()); // Check amount handling
        assertEquals(testPurchase2.getStatus(), entry2.getStatus());

        verify(purchaseRepository, times(1)).findAll();
    }

    @Test
    void generatePurchaseReport_Empty() {
        when(purchaseRepository.findAll()).thenReturn(Collections.emptyList());

        ResponseEntity<PurchaseReportResponse> response = reportController.generatePurchaseReport();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNull(response.getBody().getError());
        assertTrue(response.getBody().getPurchases().isEmpty());

        verify(purchaseRepository, times(1)).findAll();
    }

    @Test
    void generatePurchaseReport_RepositoryError() {
        when(purchaseRepository.findAll()).thenThrow(new RuntimeException("Database connection failed"));

        ResponseEntity<PurchaseReportResponse> response = reportController.generatePurchaseReport();

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNull(response.getBody().getPurchases());
        assertTrue(response.getBody().getError().contains("Internal server error: Database connection failed"));

        verify(purchaseRepository, times(1)).findAll();
    }

    @Test
    void generateRentalReport_Success() {
        when(rentalRepository.findAll()).thenReturn(List.of(testRental1));

        ResponseEntity<RentalReportResponse> response = reportController.generateRentalReport();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNull(response.getBody().getError());
        assertEquals(1, response.getBody().getRentals().size());

        RentalReportEntry entry1 = response.getBody().getRentals().get(0);
        assertEquals(testRental1.getId(), entry1.getId());
        assertEquals(testRental1.getUserId(), entry1.getUserId());
        assertEquals(testRental1.getStatus(), entry1.getStatus());
        assertEquals(testRental1.getReturnedDate(), entry1.getReturnedDate());

        verify(rentalRepository, times(1)).findAll();
    }

    @Test
    void generateRentalReport_Empty() {
        when(rentalRepository.findAll()).thenReturn(Collections.emptyList());

        ResponseEntity<RentalReportResponse> response = reportController.generateRentalReport();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNull(response.getBody().getError());
        assertTrue(response.getBody().getRentals().isEmpty());

        verify(rentalRepository, times(1)).findAll();
    }

    @Test
    void generateRentalReport_RepositoryError() {
        when(rentalRepository.findAll()).thenThrow(new RuntimeException("DB error"));

        ResponseEntity<RentalReportResponse> response = reportController.generateRentalReport();

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNull(response.getBody().getRentals());
        assertTrue(response.getBody().getError().contains("Internal server error: DB error"));

        verify(rentalRepository, times(1)).findAll();
    }
}

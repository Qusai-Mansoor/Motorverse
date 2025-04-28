package com.motorverse.Motorverse.entity;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class ReportPurchaseTest {

    @Test
    void testReportPurchaseGettersAndSetters() {
        ReportPurchase reportPurchase = new ReportPurchase();
        LocalDateTime now = LocalDateTime.now();

        reportPurchase.setId(1);
        reportPurchase.setUserId(5);
        reportPurchase.setVehicleId(20);
        reportPurchase.setPurchaseDate(now);
        reportPurchase.setPaymentMethod("PAYPAL");
        reportPurchase.setAmount(25000.0);
        reportPurchase.setTransactionId("TXN-PURCH-456");
        reportPurchase.setStatus("PENDING");

        assertEquals(1, reportPurchase.getId());
        assertEquals(5, reportPurchase.getUserId());
        assertEquals(20, reportPurchase.getVehicleId());
        assertEquals(now, reportPurchase.getPurchaseDate());
        assertEquals("PAYPAL", reportPurchase.getPaymentMethod());
        assertEquals(25000.0, reportPurchase.getAmount());
        assertEquals("TXN-PURCH-456", reportPurchase.getTransactionId());
        assertEquals("PENDING", reportPurchase.getStatus());
    }

    @Test
    void testReportPurchaseDefaultConstructor() {
        ReportPurchase reportPurchase = new ReportPurchase();
        assertNotNull(reportPurchase);
        assertNull(reportPurchase.getId()); // Default Integer value
        assertNull(reportPurchase.getUserId());
        assertNull(reportPurchase.getVehicleId());
        assertNull(reportPurchase.getPurchaseDate());
        assertNull(reportPurchase.getPaymentMethod());
        assertNull(reportPurchase.getAmount()); // Default Double value
        assertNull(reportPurchase.getTransactionId());
        assertNull(reportPurchase.getStatus());
    }
}

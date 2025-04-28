package com.motorverse.Motorverse.entity;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class PurchaseTest {

    @Test
    void testPurchaseGettersAndSetters() {
        Purchase purchase = new Purchase();
        purchase.setId(1);
        purchase.setUserId(2);
        purchase.setVehicleId(3);
        purchase.setAmount(10000.0);
        purchase.setPaymentMethod(Purchase.PaymentMethod.CREDIT_CARD);
        purchase.setTransactionId("TXN123");
        purchase.setStatus(Purchase.Status.COMPLETED);
        LocalDateTime now = LocalDateTime.now();
        purchase.setPurchaseDate(now);

        assertEquals(1, purchase.getId());
        assertEquals(2, purchase.getUserId());
        assertEquals(3, purchase.getVehicleId());
        assertEquals(10000.0, purchase.getAmount());
        assertEquals(Purchase.PaymentMethod.CREDIT_CARD, purchase.getPaymentMethod());
        assertEquals("TXN123", purchase.getTransactionId());
        assertEquals(Purchase.Status.COMPLETED, purchase.getStatus());
        assertEquals(now, purchase.getPurchaseDate());
    }

    @Test
    void testPurchaseEnums() {
        //assertEquals("CASH", Purchase.PaymentMethod.CASH.name());
        assertEquals("CREDIT_CARD", Purchase.PaymentMethod.CREDIT_CARD.name());
        assertEquals("PAYPAL", Purchase.PaymentMethod.PAYPAL.name());
        assertEquals("COMPLETED", Purchase.Status.COMPLETED.name());
        assertEquals("FAILED", Purchase.Status.FAILED.name());
        assertEquals("PENDING", Purchase.Status.PENDING.name());
    }
}

package com.motorverse.Motorverse.entity;

import org.junit.jupiter.api.Test;
import org.springframework.cglib.core.Local;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class PaymentTest {

    @Test
    void testPaymentGettersAndSetters() {
        Payment payment = new Payment();
        LocalDateTime now = LocalDateTime.now();

        payment.setId(1);
        payment.setUserId(10);
        BigDecimal  amount = new BigDecimal("150.75");
        payment.setAmount(amount);
        payment.setPaymentDate(now);
        payment.setPaymentMethod("CREDIT_CARD");
        payment.setPaymentStatus("COMPLETED");
        payment.setReferenceNumber("TXN-PAY-123");

        assertEquals(1, payment.getId());
        assertEquals(10, payment.getUserId());
        BigDecimal expectedAmount = new BigDecimal("150.75");
        assertEquals(expectedAmount, payment.getAmount());
        assertEquals(now, payment.getPaymentDate());
        assertEquals("CREDIT_CARD", payment.getPaymentMethod());
        assertEquals("COMPLETED", payment.getPaymentStatus());
        assertEquals("TXN-PAY-123", payment.getReferenceNumber());
    }

    @Test
    void testPaymentDefaultConstructor() {
        Payment payment = new Payment();
       // assertNotNull(payment);
        assertNull( payment.getId()); 
        assertNull(payment.getUserId()); // Default Integer value
        BigDecimal expectedAmount = new BigDecimal("0");
        assertEquals(expectedAmount, payment.getAmount()); // Default double value
        assertEquals(LocalDateTime.now(),payment.getPaymentDate());
        assertNull(payment.getPaymentMethod());
        assertNull(payment.getPaymentStatus());
        assertNull(payment.getReferenceNumber());
    }
}

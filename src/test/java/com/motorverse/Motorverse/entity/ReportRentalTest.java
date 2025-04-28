package com.motorverse.Motorverse.entity;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class ReportRentalTest {

    @Test
    void testReportRentalGettersAndSetters() {
        ReportRental reportRental = new ReportRental();
        LocalDateTime startDate = LocalDateTime.now().minusDays(5);
        LocalDateTime endDate = LocalDateTime.now().plusDays(2);
        LocalDateTime returnedDate = LocalDateTime.now().plusDays(1);

        reportRental.setId(1);
        reportRental.setUserId(8);
        reportRental.setVehicleId(15);
        reportRental.setStartDate(startDate);
        reportRental.setEndDate(endDate);
        reportRental.setReturnedDate(returnedDate);
        reportRental.setStatus("RETURNED");

        assertEquals(1, reportRental.getId());
        assertEquals(8, reportRental.getUserId());
        assertEquals(15, reportRental.getVehicleId());
        assertEquals(startDate, reportRental.getStartDate());
        assertEquals(endDate, reportRental.getEndDate());
        assertEquals(returnedDate, reportRental.getReturnedDate());
        assertEquals("RETURNED", reportRental.getStatus());
    }

    @Test
    void testReportRentalDefaultConstructor() {
        ReportRental reportRental = new ReportRental();
        assertNotNull(reportRental);
        assertNull(reportRental.getId()); // Default Integer value
        assertNull(reportRental.getUserId());
        assertNull(reportRental.getVehicleId());
        assertNull(reportRental.getStartDate());
        assertNull(reportRental.getEndDate());
        assertNull(reportRental.getReturnedDate());
        assertNull(reportRental.getStatus());
    }
}

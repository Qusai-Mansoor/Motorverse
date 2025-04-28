package com.motorverse.Motorverse.controller;

import com.motorverse.Motorverse.entity.AutoPart;
import com.motorverse.Motorverse.repository.AutoPartRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AutoPartControllerTest {

    @Mock
    private AutoPartRepository autoPartRepository;

    @InjectMocks
    private AutoPartController autoPartController;

    private AutoPart testPart;

    @BeforeEach
    void setUp() {
        testPart = new AutoPart();
        testPart.setId(1);
        testPart.setName("Brake Pad");
        testPart.setDescription("High quality brake pad");
        BigDecimal price = new BigDecimal("50.00");
        testPart.setPrice(price);
        testPart.setQuantity(10);
    }

    @Test
    void getAllAutoParts_ShouldReturnList() {
        List<AutoPart> parts = new ArrayList<>();
        parts.add(testPart);
        when(autoPartRepository.findAll()).thenReturn(parts);

        List<AutoPart> result = autoPartController.getAllAutoParts();
        assertEquals(1, result.size());
        assertEquals(testPart.getName(), result.get(0).getName());
        verify(autoPartRepository).findAll();
    }

    @Test
    void getAutoPartById_ShouldReturnPart() {
        when(autoPartRepository.findById(1)).thenReturn(Optional.of(testPart));
        ResponseEntity<AutoPart> response = autoPartController.getAutoPartById(1);
        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertEquals(testPart, response.getBody());
        verify(autoPartRepository).findById(1);
    }

    @Test
    void getAutoPartById_NotFound_ShouldReturn404() {
        when(autoPartRepository.findById(2)).thenReturn(Optional.empty());
        ResponseEntity<AutoPart> response = autoPartController.getAutoPartById(2);
        assertTrue(response.getStatusCode().is4xxClientError());
        assertNull(response.getBody());
        verify(autoPartRepository).findById(2);
    }

    @Test
    void updateQuantityAfterPurchase_WithValidQuantity_ShouldDecreaseStock() {
        when(autoPartRepository.findById(1)).thenReturn(Optional.of(testPart));
        when(autoPartRepository.save(any(AutoPart.class))).thenReturn(testPart);

        ResponseEntity<AutoPart> response = autoPartController.updateQuantityAfterPurchase(1, 2);
        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertEquals(8, response.getBody().getQuantity());
        verify(autoPartRepository).findById(1);
        verify(autoPartRepository).save(any(AutoPart.class));
    }

    @Test
    void updateQuantityAfterPurchase_WithInvalidQuantity_ShouldReturnBadRequest() {
        when(autoPartRepository.findById(1)).thenReturn(Optional.of(testPart));
        ResponseEntity<AutoPart> response = autoPartController.updateQuantityAfterPurchase(1, 20);
        assertTrue(response.getStatusCode().is4xxClientError());
        verify(autoPartRepository).findById(1);
        verify(autoPartRepository, never()).save(any(AutoPart.class));
    }

    @Test
    void updateQuantityAfterPurchase_NotFound_ShouldReturn404() {
        when(autoPartRepository.findById(99)).thenReturn(Optional.empty());
        ResponseEntity<AutoPart> response = autoPartController.updateQuantityAfterPurchase(99, 1);
        assertTrue(response.getStatusCode().is4xxClientError());
        verify(autoPartRepository).findById(99);
    }

    @Test
    void createAutoPart_ShouldSaveAndReturnPart() {
        when(autoPartRepository.save(any(AutoPart.class))).thenReturn(testPart);
        AutoPart partToCreate = new AutoPart();
        partToCreate.setName("Rotor");
        partToCreate.setDescription("Rotor desc");
        BigDecimal price = new BigDecimal("100.00");
        partToCreate.setPrice(price);
        partToCreate.setQuantity(5);

        AutoPart result = autoPartController.createAutoPart(partToCreate);
        assertNotNull(result);
        assertEquals(testPart.getName(), result.getName());
        verify(autoPartRepository).save(any(AutoPart.class));
    }

    @Test
    void updateAutoPart_ShouldUpdateAndReturnPart() {
        when(autoPartRepository.findById(1)).thenReturn(Optional.of(testPart));
        when(autoPartRepository.save(any(AutoPart.class))).thenReturn(testPart);

        AutoPart updateDetails = new AutoPart();
        updateDetails.setName("Updated Name");
        updateDetails.setDescription("Updated Desc");
        BigDecimal price = new BigDecimal("60.00");
        updateDetails.setPrice(price);
        updateDetails.setQuantity(7);

        ResponseEntity<AutoPart> response = autoPartController.updateAutoPart(1, updateDetails);
        assertTrue(response.getStatusCode().is2xxSuccessful());
        verify(autoPartRepository).findById(1);
        verify(autoPartRepository).save(any(AutoPart.class));
    }

    @Test
    void updateAutoPart_NotFound_ShouldReturn404() {
        when(autoPartRepository.findById(2)).thenReturn(Optional.empty());
        AutoPart updateDetails = new AutoPart();
        ResponseEntity<AutoPart> response = autoPartController.updateAutoPart(2, updateDetails);
        assertTrue(response.getStatusCode().is4xxClientError());
        verify(autoPartRepository).findById(2);
    }

    @Test
    void deleteAutoPart_ShouldDeleteAndReturnOk() {
        when(autoPartRepository.findById(1)).thenReturn(Optional.of(testPart));
        ResponseEntity<Void> response = autoPartController.deleteAutoPart(1);
        assertTrue(response.getStatusCode().is2xxSuccessful());
        verify(autoPartRepository).findById(1);
        verify(autoPartRepository).delete(testPart);
    }

    @Test
    void deleteAutoPart_NotFound_ShouldReturn404() {
        when(autoPartRepository.findById(2)).thenReturn(Optional.empty());
        ResponseEntity<Void> response = autoPartController.deleteAutoPart(2);
        assertTrue(response.getStatusCode().is4xxClientError());
        verify(autoPartRepository).findById(2);
    }
}

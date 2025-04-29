package com.motorverse.Motorverse.controller;

import com.motorverse.Motorverse.entity.SupportTicket;
import com.motorverse.Motorverse.repository.SupportTicketRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SupportControllerTest {

    @Mock
    private SupportTicketRepository supportTicketRepository;

    @InjectMocks
    private SupportController supportController;

    private SupportTicket testTicket;

    @BeforeEach
    void setUp() {
        testTicket = new SupportTicket();
        testTicket.setId(1);
        testTicket.setUserId(2);
        testTicket.setIssueTitle("Login Issue");
        testTicket.setDescription("Cannot login to my account");
        testTicket.setCreatedAt(LocalDateTime.now().minusDays(1));
        testTicket.setUpdatedAt(LocalDateTime.now());
        testTicket.setStatus(SupportTicket.Status.OPEN);
        testTicket.setResolution(null);
    }

    @Test
    void createTicket_ShouldSaveAndReturnTicket() {
        TicketRequest req = new TicketRequest();
        req.setUserId(2);
        req.setIssueTitle("Login Issue");
        req.setDescription("Cannot login to my account");

        when(supportTicketRepository.save(any(SupportTicket.class))).thenReturn(testTicket);

        SupportTicket result = supportController.createTicket(req);
        assertNotNull(result);
        assertEquals(testTicket.getIssueTitle(), result.getIssueTitle());
        verify(supportTicketRepository).save(any(SupportTicket.class));
    }

    @Test
    void createTicket_WithInvalidUser_ShouldThrowException() {
        TicketRequest req = new TicketRequest();
        req.setUserId(0);
        req.setIssueTitle("Test");
        req.setDescription("Test desc");

        assertThrows(RuntimeException.class, () -> supportController.createTicket(req));
        verify(supportTicketRepository, never()).save(any(SupportTicket.class));
    }

    @Test
    void getUserTickets_ShouldReturnList() {
        List<SupportTicket> tickets = Collections.singletonList(testTicket);
        when(supportTicketRepository.findByUserIdOrderByCreatedAtDesc(2)).thenReturn(tickets);

        List<SupportTicket> result = supportController.getUserTickets(2);
        assertEquals(1, result.size());
        assertEquals(testTicket.getId(), result.get(0).getId());
        verify(supportTicketRepository).findByUserIdOrderByCreatedAtDesc(2);
    }

    @Test
    void getTicketDetails_Found_ShouldReturnTicket() {
        when(supportTicketRepository.findById(1)).thenReturn(Optional.of(testTicket));
        ResponseEntity<SupportTicket> response = supportController.getTicketDetails(1);
        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertNotNull(response.getBody());
        assertEquals(testTicket, response.getBody());
        verify(supportTicketRepository).findById(1);
    }

    @Test
    void getTicketDetails_NotFound_ShouldReturn404() {
        when(supportTicketRepository.findById(99)).thenReturn(Optional.empty());
        ResponseEntity<SupportTicket> response = supportController.getTicketDetails(99);
        assertTrue(response.getStatusCode().is4xxClientError());
        assertNull(response.getBody());
        verify(supportTicketRepository).findById(99);
    }

    @Test
    void getAllTickets_ShouldReturnList() {
        List<SupportTicket> tickets = Arrays.asList(testTicket);
        when(supportTicketRepository.findAll()).thenReturn(tickets);

        ResponseEntity<List<SupportTicket>> response = supportController.getAllTickets();
        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertEquals(1, response.getBody().size());
        verify(supportTicketRepository).findAll();
    }

    @Test
    void updateTicketStatus_ShouldUpdateAndReturnTicket() {
        when(supportTicketRepository.findById(1)).thenReturn(Optional.of(testTicket));
        when(supportTicketRepository.save(any(SupportTicket.class))).thenReturn(testTicket);

        TicketUpdateRequest req = new TicketUpdateRequest();
        req.setStatus("CLOSED");
        req.setResolution("Issue resolved");

        ResponseEntity<?> response = supportController.updateTicketStatus(1, req);
        assertTrue(response.getStatusCode().is2xxSuccessful());
        verify(supportTicketRepository).findById(1);
        verify(supportTicketRepository).save(any(SupportTicket.class));
    }

    @Test
    void updateTicketStatus_NotFound_ShouldThrowException() {
        when(supportTicketRepository.findById(99)).thenReturn(Optional.empty());
        TicketUpdateRequest req = new TicketUpdateRequest();
        req.setStatus("CLOSED");
        req.setResolution("Issue resolved");

        assertThrows(RuntimeException.class, () -> supportController.updateTicketStatus(99, req));
        verify(supportTicketRepository).findById(99);
        verify(supportTicketRepository, never()).save(any(SupportTicket.class));
    }

    @Test
    void deleteTicket_ShouldDeleteAndReturnOk() {
        when(supportTicketRepository.findById(1)).thenReturn(Optional.of(testTicket));
        ResponseEntity<?> response = supportController.deleteTicket(1);
        assertTrue(response.getStatusCode().is2xxSuccessful());
        verify(supportTicketRepository).findById(1);
        verify(supportTicketRepository).delete(testTicket);
    }

    @Test
    void deleteTicket_NotFound_ShouldThrowException() {
        when(supportTicketRepository.findById(99)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> supportController.deleteTicket(99));
        verify(supportTicketRepository).findById(99);
        verify(supportTicketRepository, never()).delete(any(SupportTicket.class));
    }
}

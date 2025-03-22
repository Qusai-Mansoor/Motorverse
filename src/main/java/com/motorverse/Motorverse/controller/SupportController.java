package com.motorverse.Motorverse.controller;

import com.motorverse.Motorverse.entity.SupportTicket;
import com.motorverse.Motorverse.repository.SupportTicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/support")
public class SupportController {

    @Autowired
    private SupportTicketRepository supportTicketRepository;

    @PostMapping("/tickets")
    public SupportTicket createTicket(@RequestBody TicketRequest request) {
        if (request.getUserId() <= 0) {
            throw new RuntimeException("User must be logged in");
        }

        SupportTicket ticket = new SupportTicket();
        ticket.setUserId(request.getUserId());
        ticket.setIssueTitle(request.getIssueTitle());
        ticket.setDescription(request.getDescription());
        ticket.setCreatedAt(LocalDateTime.now());
        ticket.setUpdatedAt(LocalDateTime.now());

        return supportTicketRepository.save(ticket);
    }

    @GetMapping("/tickets/user/{userId}")
    public List<SupportTicket> getUserTickets(@PathVariable int userId) {
        return supportTicketRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    @GetMapping("/tickets/{ticketId}")
    public ResponseEntity<SupportTicket> getTicketDetails(@PathVariable int ticketId) {
        return supportTicketRepository.findById(ticketId)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/tickets/all")
    public ResponseEntity<List<SupportTicket>> getAllTickets() {
        return ResponseEntity.ok(supportTicketRepository.findAll());
    }

    @PutMapping("/tickets/{ticketId}")
    public ResponseEntity<?> updateTicketStatus(@PathVariable int ticketId, @RequestBody TicketUpdateRequest request) {
        SupportTicket ticket = supportTicketRepository.findById(ticketId)
            .orElseThrow(() -> new RuntimeException("Ticket not found"));
            
        ticket.setStatus(SupportTicket.Status.valueOf(request.getStatus().toUpperCase()));
        ticket.setResolution(request.getResolution());
        ticket.setUpdatedAt(LocalDateTime.now());
        
        return ResponseEntity.ok(supportTicketRepository.save(ticket));
    }

    @DeleteMapping("/tickets/{ticketId}")
    public ResponseEntity<?> deleteTicket(@PathVariable int ticketId) {
        SupportTicket ticket = supportTicketRepository.findById(ticketId)
            .orElseThrow(() -> new RuntimeException("Ticket not found"));
        
        supportTicketRepository.delete(ticket);
        return ResponseEntity.ok().build();
    }
}

class TicketRequest {
    private int userId;
    private String issueTitle;
    private String description;

    // Getters and Setters
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    
    public String getIssueTitle() { return issueTitle; }
    public void setIssueTitle(String issueTitle) { this.issueTitle = issueTitle; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}

class TicketUpdateRequest {
    private String status;
    private String resolution;

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getResolution() { return resolution; }
    public void setResolution(String resolution) { this.resolution = resolution; }
}

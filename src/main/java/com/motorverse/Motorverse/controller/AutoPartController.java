package com.motorverse.Motorverse.controller;

import com.motorverse.Motorverse.entity.AutoPart;
import com.motorverse.Motorverse.repository.AutoPartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/autoparts")
public class AutoPartController {

    @Autowired
    private AutoPartRepository autoPartRepository;

    // Get all auto parts
    @GetMapping
    public List<AutoPart> getAllAutoParts() {
        return autoPartRepository.findAll();
    }

    // Get auto part by ID
    @GetMapping("/{id}")
    public ResponseEntity<AutoPart> getAutoPartById(@PathVariable Integer id) {
        Optional<AutoPart> autoPart = autoPartRepository.findById(id);
        return autoPart.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Create a new auto part
    @PostMapping
    public AutoPart createAutoPart(@RequestBody AutoPart autoPart) {
        return autoPartRepository.save(autoPart);
    }

    // Update quantity after purchase
    @PutMapping("/{id}/purchase")
    public ResponseEntity<AutoPart> updateQuantityAfterPurchase(
            @PathVariable Integer id, 
            @RequestParam Integer quantity) {
        
        Optional<AutoPart> optionalAutoPart = autoPartRepository.findById(id);
        
        if (optionalAutoPart.isPresent()) {
            AutoPart autoPart = optionalAutoPart.get();
            
            // Check if there's enough quantity available
            if (autoPart.getQuantity() < quantity) {
                return ResponseEntity.badRequest().build();
            }
            
            // Update the quantity
            autoPart.setQuantity(autoPart.getQuantity() - quantity);
            autoPartRepository.save(autoPart);
            
            return ResponseEntity.ok(autoPart);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Update an auto part
    @PutMapping("/{id}")
    public ResponseEntity<AutoPart> updateAutoPart(
            @PathVariable Integer id, 
            @RequestBody AutoPart autoPartDetails) {
        
        Optional<AutoPart> optionalAutoPart = autoPartRepository.findById(id);
        
        if (optionalAutoPart.isPresent()) {
            AutoPart autoPart = optionalAutoPart.get();
            autoPart.setName(autoPartDetails.getName());
            autoPart.setPrice(autoPartDetails.getPrice());
            autoPart.setQuantity(autoPartDetails.getQuantity());
            autoPart.setDescription(autoPartDetails.getDescription());
            
            return ResponseEntity.ok(autoPartRepository.save(autoPart));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Delete an auto part
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAutoPart(@PathVariable Integer id) {
        Optional<AutoPart> optionalAutoPart = autoPartRepository.findById(id);
        
        if (optionalAutoPart.isPresent()) {
            autoPartRepository.delete(optionalAutoPart.get());
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}

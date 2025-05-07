package com.motorverse.Motorverse.controller;

import com.motorverse.Motorverse.entity.User;
import com.motorverse.Motorverse.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/register")
public class RegisterController {
    private static final Logger logger = LoggerFactory.getLogger(RegisterController.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        logger.info("Received registration request for email: {}", user.getEmail());
        
        try {
            // Check if email already exists
            if (userRepository.findByEmail(user.getEmail()) != null) {
                logger.warn("Registration failed: Email already in use: {}", user.getEmail());
                return ResponseEntity.badRequest().body("Email already in use");
            }
            
            // Validate required fields
            if (user.getFirstName() == null || user.getFirstName().trim().isEmpty() ||
                user.getLastName() == null || user.getLastName().trim().isEmpty() ||
                user.getEmail() == null || user.getEmail().trim().isEmpty() ||
                user.getPassword() == null || user.getPassword().trim().isEmpty() ||
                user.getDateOfBirth() == null) {
                logger.warn("Registration failed: Missing required fields");
                return ResponseEntity.badRequest().body("All required fields must be filled");
            }
            
            // Encode password
            String encodedPassword = passwordEncoder.encode(user.getPassword());
            user.setPassword(encodedPassword);
            
            logger.info("Saving new user: {}", user.getEmail());
            
            // Save user
            User savedUser = userRepository.save(user);
            
            // Create a response without exposing the password
            Map<String, Object> response = new HashMap<>();
            response.put("id", savedUser.getId());
            response.put("email", savedUser.getEmail());
            response.put("firstName", savedUser.getFirstName());
            response.put("lastName", savedUser.getLastName());
            response.put("role", savedUser.getRole().toString());
            
            logger.info("User successfully registered: {}", savedUser.getEmail());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Registration error for {}: {}", user.getEmail(), e.getMessage(), e);
            return ResponseEntity.badRequest().body("Registration failed: " + e.getMessage());
        }
    }
    
    // Add a simple test endpoint to check if the controller is accessible
    @GetMapping("/test")
    public ResponseEntity<String> testEndpoint() {
        logger.info("Test endpoint called");
        return ResponseEntity.ok("Register controller is working");
    }
}

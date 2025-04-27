package com.motorverse.Motorverse.controller;

import com.motorverse.Motorverse.entity.User;
import com.motorverse.Motorverse.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private UserRepository userRepository;

    // Get all users (admin only)
    @GetMapping("/all")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userRepository.findAll());
    }

    // Get a user by ID
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable int id) {
        Optional<User> user = userRepository.findById(id);
        return user.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.status(404).body(null));
    }

    // Create a new user (signup)
    @PostMapping("/signup")
    public ResponseEntity<User> createUser(@RequestBody User user) {
        // In production, hash the password before saving!
        User savedUser = userRepository.save(user);
        return ResponseEntity.ok(savedUser);
    }

    // Login
    @PostMapping("/login")
    public ResponseEntity<User> login(@RequestBody LoginRequest loginRequest) {
        User user = userRepository.findByEmail(loginRequest.getEmail());
        if (user != null && user.getPassword().equals(loginRequest.getPassword())) {
            return ResponseEntity.ok(user);
        }
        return ResponseEntity.status(401).body(null);
    }

    // Update a user (admin only)
    @PutMapping("/{id}")
    public ResponseEntity<String> updateUser(@PathVariable int id, @RequestBody User updatedUser) {
        Optional<User> existingUser = userRepository.findById(id);
        if (!existingUser.isPresent()) {
            return ResponseEntity.status(404).body("User not found");
        }

        User user = existingUser.get();
        user.setEmail(updatedUser.getEmail());
        user.setFirstName(updatedUser.getFirstName());
        user.setLastName(updatedUser.getLastName());
        user.setDateOfBirth(updatedUser.getDateOfBirth());
        user.setPhoneNumber(updatedUser.getPhoneNumber());
        user.setRole(updatedUser.getRole());
        user.setStatus(updatedUser.getStatus());
        userRepository.save(user);

        return ResponseEntity.ok("User updated successfully");
    }

    // Delete a user (admin only)
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable int id) {
        if (!userRepository.existsById(id)) {
            return ResponseEntity.status(404).body("User not found");
        }

        userRepository.deleteById(id);
        return ResponseEntity.ok("User deleted successfully");
    }
}

class LoginRequest {
    private String email;
    private String password;

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
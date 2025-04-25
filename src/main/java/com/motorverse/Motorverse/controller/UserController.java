package com.motorverse.Motorverse.controller;

import com.motorverse.Motorverse.entity.User;
import com.motorverse.Motorverse.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.io.IOException;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            return ResponseEntity.badRequest().body("Email already registered");
        }
        User newUser = new User(
            user.getEmail(),
            user.getPassword(), // In production, hash this!
            user.getFirstName(),
            user.getLastName(),
            user.getDateOfBirth(),
            user.getPhoneNumber(),
            User.Role.USER
        );
        userRepository.save(newUser);
        return ResponseEntity.ok("User registered successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginRequest credentials) {
        User user = userRepository.findByEmail(credentials.getEmail());
        if (user == null || !user.getPassword().equals(credentials.getPassword())) {
            return ResponseEntity.badRequest().body("Invalid email or password");
        }
        String token = "mock-jwt-" + System.currentTimeMillis();
        LoginResponse response = new LoginResponse(
            token, 
            user.getId(), 
            user.getFirstName(), 
            user.getEmail(), 
            user.getPhoneNumber(), 
            user.getRole().toString().toUpperCase() // Ensure uppercase role
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/all")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userRepository.findAll();
        return ResponseEntity.ok(users);
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateUser(@PathVariable int id, @RequestBody User updatedUser) {
        User user = userRepository.findById(id).orElse(null);
        if (user == null) {
            return ResponseEntity.badRequest().body("User not found");
        }
        user.setFirstName(updatedUser.getFirstName());
        user.setLastName(updatedUser.getLastName());
        user.setEmail(updatedUser.getEmail());
        user.setPhoneNumber(updatedUser.getPhoneNumber());
        try {
            user.setStatus(User.Status.valueOf(updatedUser.getStatus().toString().toUpperCase()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid status value");
        }
        userRepository.save(user);
        return ResponseEntity.ok("User updated successfully");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable int id) {
        if (!userRepository.existsById(id)) {
            return ResponseEntity.badRequest().body("User not found");
        }
        userRepository.deleteById(id);
        return ResponseEntity.ok("User deleted successfully");
    }

    @PutMapping("/profile/{id}")
    public ResponseEntity<?> updateProfile(@PathVariable int id, @RequestBody ProfileUpdateRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setDateOfBirth(request.getDateOfBirth());
        user.setPhoneNumber(request.getPhoneNumber());
        
        if (request.getPicture() != null) {
            user.setPicture(request.getPicture());
        }

        userRepository.save(user);
        
        // Return updated user data
        ProfileResponse response = new ProfileResponse(
            user.getFirstName(),
            user.getLastName(),
            user.getEmail(),
            user.getDateOfBirth(),
            user.getPhoneNumber(),
            user.getStatus().toString(),
            user.getRole().toString(),
            user.getPicture()
        );
        return ResponseEntity.ok(response);
    }

    @PutMapping("/change-password/{id}")
    public ResponseEntity<?> changePassword(@PathVariable int id, @RequestBody PasswordChangeRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.getPassword().equals(request.getCurrentPassword())) {
            return ResponseEntity.badRequest().body("Current password is incorrect");
        }

        user.setPassword(request.getNewPassword());
        userRepository.save(user);
        return ResponseEntity.ok("Password changed successfully");
    }

    @PostMapping("/profile/{id}/picture")
    public ResponseEntity<?> updateProfilePicture(
            @PathVariable int id,
            @RequestParam("picture") MultipartFile picture) {
        try {
            User user = userRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Validate file type
            String contentType = picture.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                return ResponseEntity.badRequest().body("Only image files are allowed");
            }

            // Generate unique filename
            String originalFilename = picture.getOriginalFilename();
            if (originalFilename == null || originalFilename.isEmpty()) {
                return ResponseEntity.badRequest().body("Invalid file name");
            }
            
            String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String newFilename = "profile-" + user.getId() + "-" + System.currentTimeMillis() + fileExtension;

            // Save file to static resources
            Path uploadPath = Paths.get("src/main/resources/static/images");
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Save file
            Path destination = uploadPath.resolve(newFilename);
            Files.copy(picture.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);

            // Delete old profile picture if it exists and isn't the default
            if (!user.getPicture().equals("default-avatar.jpg")) {
                try {
                    Files.deleteIfExists(uploadPath.resolve(user.getPicture()));
                } catch (IOException e) {
                    System.err.println("Could not delete old profile picture: " + e.getMessage());
                }
            }

            // Update user profile picture in database
            user.setPicture(newFilename);
            userRepository.save(user);

            return ResponseEntity.ok(Map.of(
                "message", "Profile picture updated successfully",
                "picture", newFilename
            ));

        } catch (IOException e) {
            return ResponseEntity.badRequest().body("Failed to upload profile picture: " + e.getMessage());
        }
    }

    @GetMapping("/profile/{id}")
    public ResponseEntity<?> getProfile(@PathVariable int id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        ProfileResponse response = new ProfileResponse(
            user.getFirstName(),
            user.getLastName(),
            user.getEmail(),
            user.getDateOfBirth(),
            user.getPhoneNumber(),
            user.getStatus().toString(),
            user.getRole().toString(),
            user.getPicture()
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/status/{id}")
    public ResponseEntity<?> checkLoginStatus(@PathVariable int id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        LoginStatusResponse response = new LoginStatusResponse(
            user.getId(),
            user.getFirstName(),
            user.getEmail(),
            user.getRole().toString(),
            user.getStatus().toString()
        );
        return ResponseEntity.ok(response);
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

class LoginResponse {
    private String token;
    private int userId;
    private String firstName;
    private String email;
    private String phoneNumber;
    private String role;

    public LoginResponse(String token, int userId, String firstName, String email, String phoneNumber, String role) {
        this.token = token;
        this.userId = userId;
        this.firstName = firstName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.role = role;
    }

    public String getToken() { return token; }
    public int getUserId() { return userId; }
    public String getFirstName() { return firstName; }
    public String getEmail() { return email; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getRole() { return role; }
}

class ProfileUpdateRequest {
    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;
    private String phoneNumber;
    private String picture;

    // Getters and Setters
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public String getPicture() { return picture; }
    public void setPicture(String picture) { this.picture = picture; }
}

class PasswordChangeRequest {
    private String currentPassword;
    private String newPassword;

    public String getCurrentPassword() { return currentPassword; }
    public void setCurrentPassword(String currentPassword) { this.currentPassword = currentPassword; }
    public String getNewPassword() { return newPassword; }
    public void setNewPassword(String newPassword) { this.newPassword = newPassword; }
}

class ProfileResponse {
    private String firstName;
    private String lastName;
    private String email;
    private LocalDate dateOfBirth;
    private String phoneNumber;
    private String status;
    private String role;
    private String picture;

    public ProfileResponse(String firstName, String lastName, String email, 
                         LocalDate dateOfBirth, String phoneNumber, 
                         String status, String role, String picture) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.dateOfBirth = dateOfBirth;
        this.phoneNumber = phoneNumber;
        this.status = status;
        this.role = role;
        this.picture = picture;
    }

    // Getters
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getEmail() { return email; }
    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getStatus() { return status; }
    public String getRole() { return role; }
    public String getPicture() { return picture; }
}

class LoginStatusResponse {
    private int userId;
    private String firstName;
    private String email;
    private String role;
    private String status;

    public LoginStatusResponse(int userId, String firstName, String email, 
                             String role, String status) {
        this.userId = userId;
        this.firstName = firstName;
        this.email = email;
        this.role = role;
        this.status = status;
    }

    // Getters
    public int getUserId() { return userId; }
    public String getFirstName() { return firstName; }
    public String getEmail() { return email; }
    public String getRole() { return role; }
    public String getStatus() { return status; }
}
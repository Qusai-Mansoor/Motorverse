package com.motorverse.Motorverse.controller;

import com.motorverse.Motorverse.entity.User;
import com.motorverse.Motorverse.repository.UserRepository;
import com.motorverse.Motorverse.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.nio.file.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Map;
import java.io.IOException;

@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder encoder;
    
    @Autowired
    private AuthenticationManager authenticationManager;
    
    @Autowired
    private JwtUtil jwtUtil;

    // Get all users (admin only)
    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userRepository.findAll());
    }

    // Get a user by ID
    @GetMapping("/{id}")
    @PreAuthorize("authentication.principal.username == @userRepository.findById(#id).orElse(new com.motorverse.Motorverse.entity.User()).getEmail() or hasRole('ADMIN')")
    public ResponseEntity<User> getUserById(@PathVariable int id) {
        Optional<User> user = userRepository.findById(id);
        return user.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.status(404).body(null));
    }

    @PostMapping("/signup")
    public ResponseEntity<User> createUser(@RequestBody User user) {
        // Encode the password before saving
        String rawPassword = user.getPassword();
        user.setPassword(encoder.encode(rawPassword));
        
        User savedUser = userRepository.save(user);
        return ResponseEntity.ok(savedUser);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            // Authenticate the user
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
            );
            
            SecurityContextHolder.getContext().setAuthentication(authentication);
            
            // Get user details
            User user = userRepository.findByEmail(loginRequest.getEmail());
            if (user == null) {
                return ResponseEntity.status(402).body("User not found");
            }
            
            // Generate JWT token
            String token = jwtUtil.generateToken(user);
      
            LoginResponse response = new LoginResponse(
                token,
                user.getId(),
                user.getFirstName(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.getRole().toString()
            );
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(401).body("Authentication failed: " + e.getMessage());
        }
    }

    // Update a user (admin only)
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
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
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteUser(@PathVariable int id) {
        if (!userRepository.existsById(id)) {
            return ResponseEntity.status(404).body("User not found");
        }

        userRepository.deleteById(id);
        return ResponseEntity.ok("User deleted successfully");
    }

    @PutMapping("/profile/{id}")
    @PreAuthorize("authentication.principal.username == @userRepository.findById(#id).orElse(new com.motorverse.Motorverse.entity.User()).getEmail() or hasRole('ADMIN')")
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
    @PreAuthorize("authentication.principal.username == @userRepository.findById(#id).orElse(new com.motorverse.Motorverse.entity.User()).getEmail()")
    public ResponseEntity<?> changePassword(@PathVariable int id, @RequestBody PasswordChangeRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Check if current password matches
        if (!encoder.matches(request.getCurrentPassword(), user.getPassword())) {
            return ResponseEntity.badRequest().body("Current password is incorrect");
        }

        // Set new password (encode it here)
        user.setPassword(encoder.encode(request.getNewPassword()));
        userRepository.save(user);
        return ResponseEntity.ok("Password changed successfully");
    }

    @PostMapping("/profile/{id}/picture")
    @PreAuthorize("authentication.principal.username == @userRepository.findById(#id).orElse(new com.motorverse.Motorverse.entity.User()).getEmail() or hasRole('ADMIN')")
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
    @PreAuthorize("authentication.principal.username == @userRepository.findById(#id).orElse(new com.motorverse.Motorverse.entity.User()).getEmail() or hasRole('ADMIN')")
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
        // This should validate token in the request
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || 
            authentication.getName().equals("anonymousUser")) {
            return ResponseEntity.status(401).body("Not authenticated");
        }
        
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Verify the authenticated user has permission to check this user's status
        String authenticatedEmail = authentication.getName();
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        
        if (!user.getEmail().equals(authenticatedEmail) && !isAdmin) {
            return ResponseEntity.status(403).body("Access denied");
        }
        
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
package com.motorverse.Motorverse.test;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class BCryptDemo {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String password = "myPassword123";

        // Hash same password multiple times
        String hash1 = encoder.encode(password);
        String hash2 = encoder.encode(password);
        String hash3 = encoder.encode(password);

        System.out.println("Hash 1: " + hash1);
        System.out.println("Hash 2: " + hash2);
        System.out.println("Hash 3: " + hash3);

        // Verify all hashes work with original password
        System.out.println("\nVerifying all hashes match original password:");
        System.out.println(encoder.matches(password, hash1)); // true
        System.out.println(encoder.matches(password, hash2)); // true
        System.out.println(encoder.matches(password, hash3)); // true
    }
}

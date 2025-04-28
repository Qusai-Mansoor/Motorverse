package com.motorverse.Motorverse;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest // Loads the application context, which implicitly covers the constructor
class MotorverseApplicationTest {

    @Test
    void main() {
        // Call the main method to increase coverage.
        // Pass an empty string array as arguments.
        MotorverseApplication.main(new String[]{});
}
}

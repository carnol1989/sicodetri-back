package com.epsgrau.pe.sicodetri;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.boot.SpringApplication;

class SicodetriApplicationTest {

    @Test
    void testMain() {
        // Arrange
        String[] args = new String[]{};

        // Mock SpringApplication to avoid starting the actual Spring context
        try (MockedStatic<SpringApplication> mockedStatic = Mockito.mockStatic(SpringApplication.class)) {
            // Act
            SicodetriApplication.main(args);

            // Assert
            mockedStatic.verify(() -> SpringApplication.run(SicodetriApplication.class, args));
        }
    }

}
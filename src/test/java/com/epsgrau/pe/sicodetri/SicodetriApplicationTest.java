package com.epsgrau.pe.sicodetri;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.boot.SpringApplication;

/**
 * Test class for SicodetriApplication. <br/>
 * <b>Class</b>: {@link SicodetriApplicationTest}<br/>
 * <b>Copyright</b>: &Copy; 2024 EPS Grau. <br/>
 * <b>Company</b>: EPS Grau. <br/>
 *
 * @author EPS Grau <br/>
 *     <u>Service Provider</u>: Consultor TI <br/>
 *     <u>Developed by</u>: <br/>
 *     <ul>
 *     <li>Carlos Augusto Nole Machaca</li>
 *     </ul>
 *     <u>Changes</u>:<br/>
 *     <ul>
 *     <li>Ago 28, 2024 (JAR) Creation class.</li>
 *     </ul>
 * @version 1.0
 */
class SicodetriApplicationTest {

    @Test
    @DisplayName("Invoke SpringApplication.run when main is executed")
    void invokeSpringApplicationRunWhenMainIsExecuted() {
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
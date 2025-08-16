package com.epsgrau.pe.sicodetri.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link ResourceNotFoundException}.
 *
 * <p>
 * Verifies correct instantiation of the exception with:
 * <ul>
 *   <li>Message only</li>
 *   <li>Message and cause</li>
 * </ul>
 * </p>
 *
 * <b>Class</b>: {@link ResourceNotFoundExceptionTest}<br/>
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
 *     <li>Jun 06, 2025 (JAR) Creation class.</li>
 *     </ul>
 * @version 1.0
 */
class ResourceNotFoundExceptionTest {

    /**
     * Tests that creating a {@link ResourceNotFoundException} with only a message
     * sets the exception message correctly.
     */
    @Test
    @DisplayName("Create ResourceNotFoundException with message only")
    void createResourceNotFoundExceptionWithMessage() {
        // Arrange
        String message = "Resource not found";

        // Act
        ResourceNotFoundException exception = new ResourceNotFoundException(message);

        // Assert
        assertNotNull(exception);
        assertNotNull(exception, "Exception should not be null");
        assertEquals(message, exception.getMessage(), "Exception message should match");
    }

    /**
     * Tests that creating a {@link ResourceNotFoundException} with both a message
     * and a cause sets the exception message and cause correctly.
     */
    @Test
    @DisplayName("Create ResourceNotFoundException with message and cause")
    void createResourceNotFoundExceptionWithMessageAndCause() {
        // Arrange
        String message = "Resource not found";
        Throwable cause = new Throwable("Cause of the exception");

        // Act
        ResourceNotFoundException exception = new ResourceNotFoundException(message, cause);

        // Assert
        assertNotNull(exception, "Exception should not be null");
        assertEquals(message, exception.getMessage(), "Exception message should match");
        assertEquals(cause, exception.getCause(), "Exception cause should match");
    }

}
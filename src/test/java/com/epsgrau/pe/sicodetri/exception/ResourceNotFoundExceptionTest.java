package com.epsgrau.pe.sicodetri.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * This class test for ResourceNotFoundException. <br/>
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
 *     <li>Ago 28, 2024 (JAR) Creation class.</li>
 *     </ul>
 * @version 1.0
 */
class ResourceNotFoundExceptionTest {

    @Test
    void testConstructorWithMessage() {
        // Arrange
        String message = "Resource not found";

        // Act
        ResourceNotFoundException exception = new ResourceNotFoundException(message);

        // Assert
        assertNotNull(exception, "Exception should not be null");
        assertEquals(message, exception.getMessage(), "Exception message should match");
    }

    @Test
    void testConstructorWithMessageAndCause() {
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
package com.epsgrau.pe.sicodetri.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
package com.epsgrau.pe.sicodetri.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.context.request.WebRequest;

/**
 * This class test for GlobalExceptionHandler. <br/>
 * <b>Class</b>: {@link GlobalExceptionHandlerTest}<br/>
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
class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler globalExceptionHandler;

    @BeforeEach
    void setUp() {
        globalExceptionHandler = new GlobalExceptionHandler();
    }

    @Test
    @DisplayName("Handle ResourceNotFoundException when resource is missing")
    void handleResourceNotFoundWhenResourceIsMissing() {
        // Arrange
        ResourceNotFoundException ex = new ResourceNotFoundException("Resource not found");
        WebRequest request = mock(WebRequest.class);
        when(request.getDescription(false)).thenReturn("Resource details");

        // Act
        ResponseEntity<ExceptionResponse> response = globalExceptionHandler.handlerResourceNotFound(ex, request);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        ExceptionResponse body = response.getBody();
        assertNotNull(body);
        assertEquals("Resource not found", body.getMessage());
        assertEquals("Resource details", body.getDetails());
    }

    @Test
    @DisplayName("Handle MethodArgumentNotValidException when validation fails")
    void handleValidationExceptionsWhenInputIsInvalid() {
        // Arrange
        FieldError fieldError = mock(FieldError.class);
        when(fieldError.getDefaultMessage()).thenReturn("Validation error");

        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.getAllErrors()).thenReturn(Collections.singletonList(fieldError));

        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        when(ex.getBindingResult()).thenReturn(bindingResult);

        WebRequest request = mock(WebRequest.class);
        when(request.getDescription(false)).thenReturn("Validation details");

        // Act
        ResponseEntity<ExceptionResponse> response = globalExceptionHandler.handleValidationExceptions(ex, request);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        ExceptionResponse body = response.getBody();
        assertNotNull(body);
        assertEquals("Validation error; ", body.getMessage());
        assertEquals("Validation details", body.getDetails());
    }

    @Test
    @DisplayName("Handle MaxUploadSizeExceededException when file size limit is exceeded")
    void handleMaxSizeExceptionWhenFileSizeLimitExceeded() {
        // Arrange
        MaxUploadSizeExceededException ex = new MaxUploadSizeExceededException(1000L);
        WebRequest request = mock(WebRequest.class);
        when(request.getDescription(false)).thenReturn("Max size exceeded");

        // Act
        ResponseEntity<ExceptionResponse> response = globalExceptionHandler.handleMaxSizeException(ex, request);
        System.out.println(response);

        // Assert
        assertEquals(HttpStatus.EXPECTATION_FAILED, response.getStatusCode());
        ExceptionResponse body = response.getBody();
        assertNotNull(body);
        assertEquals("Maximum upload size of 1000 bytes exceeded", body.getMessage());
        assertEquals("Max size exceeded", body.getDetails());
    }

}
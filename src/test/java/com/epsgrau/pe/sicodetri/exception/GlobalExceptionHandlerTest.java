package com.epsgrau.pe.sicodetri.exception;

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

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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
    @DisplayName("testHandlerResourceNotFound")
    void testHandlerResourceNotFound() {
        // Arrange
        ResourceNotFoundException ex = new ResourceNotFoundException("Resource not found");
        WebRequest request = mock(WebRequest.class);
        when(request.getDescription(false)).thenReturn("Resource details");

        // Act
        ResponseEntity<ExceptionResponse> response = globalExceptionHandler.handlerResourceNotFound(ex, request);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Resource not found", response.getBody().getMessage());
        assertEquals("Resource details", response.getBody().getDetails());
    }

    @Test
    @DisplayName("testHandleValidationExceptions")
    void testHandleValidationExceptions() {
        // Arrange
        // Crear un mock de BindingResult
        BindingResult bindingResult = mock(BindingResult.class);

        // Configurar el mock de MethodArgumentNotValidException para devolver el mock de BindingResult
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        when(ex.getBindingResult()).thenReturn(bindingResult);

        // Configurar el mock de BindingResult para devolver una lista de errores
        WebRequest request = mock(WebRequest.class);
        when(request.getDescription(false)).thenReturn("Validation details");

        FieldError fieldError = mock(FieldError.class);
        when(fieldError.getDefaultMessage()).thenReturn("Validation error");
        when(bindingResult.getAllErrors()).thenReturn(Collections.singletonList(fieldError));

        // Act
        ResponseEntity<ExceptionResponse> response = globalExceptionHandler.handleValidationExceptions(ex, request);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Validation error; ", response.getBody().getMessage());
        assertEquals("Validation details", response.getBody().getDetails());
    }

    @Test
    @DisplayName("testHandleMaxSizeException")
    void testHandleMaxSizeException() {
        // Arrange
        MaxUploadSizeExceededException ex = new MaxUploadSizeExceededException(1000L);
        WebRequest request = mock(WebRequest.class);
        when(request.getDescription(false)).thenReturn("Max size exceeded");

        // Act
        ResponseEntity<ExceptionResponse> response = globalExceptionHandler.handleMaxSizeException(ex, request);
        System.out.println(response);

        // Assert
        assertEquals(HttpStatus.EXPECTATION_FAILED, response.getStatusCode());
        assertEquals("Maximum upload size of 1000 bytes exceeded", response.getBody().getMessage());
        assertEquals("Max size exceeded", response.getBody().getDetails());
    }

}
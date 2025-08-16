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
 * Unit tests for {@link GlobalExceptionHandler}.
 *
 * <p>
 * Verifies that the global exception handler methods produce the correct
 * HTTP status codes and {@link ExceptionResponse} bodies for various exceptions:
 * <ul>
 *   <li>{@link ResourceNotFoundException}</li>
 *   <li>{@link MethodArgumentNotValidException}</li>
 *   <li>{@link MaxUploadSizeExceededException}</li>
 * </ul>
 * </p>
 *
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
 *     <li>Jun 06, 2025 (JAR) Creation class.</li>
 *     </ul>
 * @version 1.0
 */
class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler globalExceptionHandler;

    /**
     * Initializes a fresh instance of the {@link GlobalExceptionHandler}
     * before each test.
     */
    @BeforeEach
    void setUp() {
        globalExceptionHandler = new GlobalExceptionHandler();
    }

    /**
     * Tests that handling a {@link ResourceNotFoundException} returns
     * HTTP 404 Not Found and a body containing the exception message
     * and request details.
     */
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

    /**
     * Tests that handling a {@link MethodArgumentNotValidException} returns
     * HTTP 400 Bad Request and a body containing all validation error messages
     * concatenated, along with request details.
     */
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

    /**
     * Tests that handling a {@link MaxUploadSizeExceededException} returns
     * HTTP 417 Expectation Failed and a body containing the maximum upload
     * size message and request details.
     */
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
package com.epsgrau.pe.sicodetri.exception;

import java.time.LocalDateTime;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

/**
 * Centralized exception handler for the application.
 * Uses {@link ControllerAdvice} to intercept exceptions thrown by controllers
 * and map them to {@link ExceptionResponse} payloads with appropriate HTTP status codes.
 * Handles:
 * <ul>
 *   <li>{@link ResourceNotFoundException} → 404 NOT_FOUND</li>
 *   <li>{@link MethodArgumentNotValidException} → 400 BAD_REQUEST</li>
 *   <li>{@link MaxUploadSizeExceededException} → 417 EXPECTATION_FAILED</li>
 * </ul>
 * Each handler method constructs an {@link ExceptionResponse} containing
 * a timestamp, a human-readable message, and request details.
 * <b>Class</b>: {@link GlobalExceptionHandler}<br/>
 * <b>Copyright</b>: &copy; 2024 EPS Grau. <br/>
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
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handler resource not found.
     *
     * @param ex Object exception
     * @param request Object WebRequest
     * @return ReponseEntity of ExceptionResponse
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ExceptionResponse> handlerResourceNotFound(ResourceNotFoundException ex,
                                                                     WebRequest request) {
        ExceptionResponse er = new ExceptionResponse(LocalDateTime.now(), ex.getMessage(),
            request.getDescription(false));
        return new ResponseEntity<ExceptionResponse>(er, HttpStatus.NOT_FOUND);
    }

    /**
     * Handle validation exceptions.
     *
     * @param ex Object exception
     * @param request Object WebRequest
     * @return ReponseEntity of ExceptionResponse
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ExceptionResponse> handleValidationExceptions(
        MethodArgumentNotValidException ex, WebRequest request) {
        StringBuilder errors = new StringBuilder();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            errors.append(error.getDefaultMessage()).append("; ");
        });
        ExceptionResponse er = new ExceptionResponse(LocalDateTime.now(), errors.toString(),
            request.getDescription(false));
        return new ResponseEntity<ExceptionResponse>(er, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handle max size exception.
     *
     * @param ex Object exception
     * @param request Object WebRequest
     * @return ReponseEntity of ExceptionResponse
     */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ExceptionResponse> handleMaxSizeException(
        MaxUploadSizeExceededException ex, WebRequest request) {
        ExceptionResponse er = new ExceptionResponse(LocalDateTime.now(), ex.getMessage(),
            request.getDescription(false));
        return new ResponseEntity<ExceptionResponse>(er, HttpStatus.EXPECTATION_FAILED);
    }

}

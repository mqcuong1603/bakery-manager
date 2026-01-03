package com.bakery.common.exception;

import com.bakery.user.exception.UserNotFoundException;
import com.bakery.user.exception.UsernameAlreadyExistsException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler for REST API.
 * Converts exceptions to proper HTTP responses.
 *
 * TODO: Study how @RestControllerAdvice and @ExceptionHandler work together
 * - @RestControllerAdvice: Applies to all controllers
 * - @ExceptionHandler: Catches specific exception types
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handle UserNotFoundException -> 404 Not Found
     *
     * TODO: This is complete. Study the pattern.
     */
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleUserNotFound(UserNotFoundException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.NOT_FOUND.value());
        body.put("error", "Not Found");
        body.put("message", ex.getMessage());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    /**
     * Handle UsernameAlreadyExistsException -> 409 Conflict
     *
     * TODO: Implement this method similar to handleUserNotFound
     * - Status should be HttpStatus.CONFLICT (409)
     * - Error should be "Conflict"
     */
    @ExceptionHandler(UsernameAlreadyExistsException.class)
    public ResponseEntity<Map<String, Object>> handleUsernameExists(UsernameAlreadyExistsException ex) {
        // TODO: Implement this
        return null;
    }

    /**
     * Handle validation errors -> 400 Bad Request
     *
     * TODO: Implement this method
     * - Extract field errors from ex.getBindingResult().getFieldErrors()
     * - Return map of field -> error message
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationErrors(MethodArgumentNotValidException ex) {
        // TODO: Implement this
        // Hint: Loop through ex.getBindingResult().getFieldErrors()
        // and collect field name -> default message
        return null;
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleResourceNotFound(ResourceNotFoundException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.NOT_FOUND.value());
        body.put("error", "Not Found");
        body.put("message", ex.getMessage());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }
}

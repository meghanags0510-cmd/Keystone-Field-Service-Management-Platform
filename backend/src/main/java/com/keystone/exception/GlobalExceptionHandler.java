package com.keystone.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import java.time.Instant;
import java.util.*;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ApiException.class)
    ResponseEntity<?> api(ApiException ex, HttpServletRequest req) {
        return body(ex.getStatus(), ex.getMessage(), req.getRequestURI(), List.of());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<?> validation(MethodArgumentNotValidException ex, HttpServletRequest req) {
        List<Map<String,String>> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> Map.of("field", e.getField(), "message", Objects.toString(e.getDefaultMessage(), "Invalid value")))
                .toList();
        return body(HttpStatus.BAD_REQUEST, "Validation failed", req.getRequestURI(), errors);
    }

    @ExceptionHandler(Exception.class)
    ResponseEntity<?> other(Exception ex, HttpServletRequest req) {
        return body(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error", req.getRequestURI(), List.of());
    }

    private ResponseEntity<?> body(HttpStatus status, String message, String path, List<?> errors) {
        return ResponseEntity.status(status).body(Map.of(
                "timestamp", Instant.now(), "status", status.value(),
                "message", message, "path", path, "errors", errors
        ));
    }
}

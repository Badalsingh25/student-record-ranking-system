package com.college.studentranking.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(NoSuchElementException.class)
    ResponseEntity<Map<String, String>> notFound(NoSuchElementException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", ex.getMessage()));
    }
    
    @ExceptionHandler(IllegalArgumentException.class)
    ResponseEntity<Map<String, String>> badRequest(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(Map.of("message", ex.getMessage()));
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<Map<String, Object>> validation(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(e -> errors.putIfAbsent(e.getField(), e.getDefaultMessage()));
        return ResponseEntity.badRequest().body(Map.of("message",
                "Please check the submitted fields.",
                "errors",
                errors));
    }
    
    @ExceptionHandler(DataIntegrityViolationException.class)
    ResponseEntity<Map<String, String>> duplicate(DataIntegrityViolationException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message",
                "Roll number or email already exists."));
    }
}

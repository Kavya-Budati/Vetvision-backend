package com.vetvision.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(ResponseStatusException.class)
    ResponseEntity<Map<String, Object>> status(ResponseStatusException exception) {
        return response(exception.getStatusCode().value(), exception.getReason());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<Map<String, Object>> validation(MethodArgumentNotValidException exception) {
        String message = exception.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage()).findFirst().orElse("Invalid request.");
        return response(HttpStatus.BAD_REQUEST.value(), message);
    }

    @ExceptionHandler({BadCredentialsException.class, HttpMessageNotReadableException.class})
    ResponseEntity<Map<String, Object>> badRequest(Exception exception) {
        int status = exception instanceof BadCredentialsException ? HttpStatus.UNAUTHORIZED.value() : HttpStatus.BAD_REQUEST.value();
        return response(status, exception instanceof BadCredentialsException ? "Email or password is incorrect." : "Request body is invalid.");
    }

    private ResponseEntity<Map<String, Object>> response(int status, String message) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", Instant.now().toString());
        body.put("status", status);
        body.put("message", message == null ? "Request failed." : message);
        return ResponseEntity.status(status).body(body);
    }
}
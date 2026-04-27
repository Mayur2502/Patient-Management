package com.pm.patientservice.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;


@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String,String>>handleValidationException(MethodArgumentNotValidException ex){
        Map<String,String>errors=new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error->errors.put(error.getField(), error.getDefaultMessage()));

        return ResponseEntity.badRequest().body(errors);
    }
    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<Map<String,String>>handleEmailAlreadyExistsException(EmailAlreadyExistsException ex){
        log.warn("Email already exits {}",ex.getMessage());
        Map<String,String>errors=new HashMap<>();
        errors.put("message","Email address already exists");
        return ResponseEntity.badRequest().body(errors);

    }
    @ExceptionHandler(PatientNotFoundException.class)
    public ResponseEntity<Map<String,String>>handlePatientNotFound(PatientNotFoundException ex){
        log.warn("Patient not found {}",ex.getMessage());
        Map<String,String>errors=new HashMap<>();
        errors.put("message","Patient not found");
        return ResponseEntity.badRequest().body(errors);
    }
    // Handles → billing service down or unavailable
    @ExceptionHandler(BillingServiceException.class)
    public ResponseEntity<Map<String, Object>> handleBillingServiceException(
            BillingServiceException ex) {
        return buildResponse(
                HttpStatus.SERVICE_UNAVAILABLE,
                "Billing Service Unavailable",
                ex.getMessage()
        );
    }

    // Handles → validation errors (@NotNull, @Email etc)


    // Handles → any other unexpected error
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {
        return buildResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Internal Server Error",
                "Something went wrong. Please try again later."
        );
    }

    // Reusable response builder
    private ResponseEntity<Map<String, Object>> buildResponse(
            HttpStatus status, String error, String message) {

        Map<String, Object> response = new HashMap<>();
        response.put("status", status.value());
        response.put("error", error);
        response.put("message", message);
        response.put("timestamp", LocalDateTime.now().toString());

        return new ResponseEntity<>(response, status);
    }

}

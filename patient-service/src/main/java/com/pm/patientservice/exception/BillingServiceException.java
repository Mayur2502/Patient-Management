package com.pm.patientservice.exception;

public class BillingServiceException extends RuntimeException {

    public BillingServiceException(String message) {
        super(message);
    }

    public BillingServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
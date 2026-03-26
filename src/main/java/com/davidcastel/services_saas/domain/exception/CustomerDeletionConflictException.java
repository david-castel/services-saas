package com.davidcastel.services_saas.domain.exception;

public class CustomerDeletionConflictException extends RuntimeException {
    public CustomerDeletionConflictException(String message) {
        super(message);
    }
}
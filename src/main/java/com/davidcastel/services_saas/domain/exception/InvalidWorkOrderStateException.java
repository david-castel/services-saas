package com.davidcastel.services_saas.domain.exception;

public class InvalidWorkOrderStateException extends RuntimeException {

    public InvalidWorkOrderStateException(String message) {
        super(message);
    }

}
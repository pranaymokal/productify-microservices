package com.microservices.auth.auth_service.exception;

/** Author: Pranay Sanjay Mokal. Date: 11-05-2025 Project: Microservices */

public class UserRegistrationException extends RuntimeException {
    public UserRegistrationException(String message) {
        super(message);
    }
}

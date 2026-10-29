package com.microservices.user.user_service.exception;

/** Author: Pranay Sanjay Mokal Date: 02-05-2025 Project: Microservices */

public class ResourceNotFoundException extends RuntimeException {

  public ResourceNotFoundException(String message) {
    super(message);
  }
}

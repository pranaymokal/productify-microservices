package com.microservices.cart.cart_service.exception;

import java.util.Map;
import lombok.Data;

/** Author: Pranay Sanjay Mokal Date: 02-05-2025 Project: Microservices */
@Data
public class ErrorResponse {

  private String message;
  private String traceId;
  private Map<String, String> errors;

  public ErrorResponse(String message, String traceId) {
    this.message = message;
    this.traceId = traceId;
  }

  public ErrorResponse(String message, Map<String, String> errors, String traceId) {
    this.message = message;
    this.errors = errors;
    this.traceId = traceId;
  }
}

package com.microservices.product.product_service.exception;

import io.micrometer.tracing.Tracer;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/** Author: Pranay Sanjay Mokal Date: 02-05-2025 Project: Microservices */

@RestControllerAdvice
public class GlobalExceptionHandler {

  private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  @Autowired private Tracer tracer;

  private String getTraceId() {
    return tracer.currentSpan() != null ? tracer.currentSpan().context().traceId() : "N/A";
  }

  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleResourceNotFound(ResourceNotFoundException ex) {
    String traceId = getTraceId();
    logger.warn("Resource not found. Trace ID: {}", traceId, ex);
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(new ErrorResponse(ex.getMessage(), traceId));
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleValidationErrors(MethodArgumentNotValidException ex) {
    String traceId = getTraceId();
    Map<String, String> errors = new HashMap<>();
    for (FieldError error : ex.getBindingResult().getFieldErrors()) {
      errors.put(error.getField(), error.getDefaultMessage());
    }

    logger.info("Validation failed. Trace ID: {}, Errors: {}", traceId, errors);
    return ResponseEntity.badRequest()
        .body(new ErrorResponse("Validation failed", errors, traceId));
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
    String traceId = getTraceId();
    logger.error("Unexpected error occurred. Trace ID: {}", traceId, ex);
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(new ErrorResponse("An unexpected error occurred.", traceId));
  }
}

package com.microservices.auth.auth_service.controller;

import com.microservices.auth.auth_service.request.AuthRequest;
import com.microservices.auth.auth_service.request.RegisterRequest;
import com.microservices.auth.auth_service.response.AuthResponse;
import com.microservices.auth.auth_service.service.AuthService;
import com.microservices.auth.auth_service.utility.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Map;

/** Author: Pranay Sanjay Mokal. Date: 02-05-2025 Project: Microservices */
@RestController
@RequestMapping("auth-service/v1/auth")
public class AuthController {

  @Autowired
  private AuthService authService;

  @Autowired
  private JwtService jwtService;

  private final AuthenticationManager authenticationManager;

  public AuthController(AuthenticationManager authenticationManager) {
    this.authenticationManager = authenticationManager;
  }

  @PostMapping("/login")
  public ResponseEntity<?> login(@RequestBody AuthRequest request) {
    String token = authService.login(request.getUsername(), request.getPassword());
    return ResponseEntity.ok(Map.of("token", token));
  }

  @PostMapping("/refresh-token")
  public ResponseEntity<AuthResponse> refreshToken(@RequestHeader("Authorization") String refreshToken) {
    String newToken = authService.refreshToken(refreshToken);
    return new ResponseEntity<>(new AuthResponse(newToken), HttpStatus.OK);
  }

  // Endpoint for user registration (optional)
  @PostMapping("/register")
  public ResponseEntity<String> register(@RequestBody RegisterRequest authRequest) {
    String response = authService.register(authRequest);
    return new ResponseEntity<>(response, HttpStatus.CREATED);
  }
}

package com.microservices.auth.auth_service.implementation;

import com.microservices.auth.auth_service.exception.UserRegistrationException;
import com.microservices.auth.auth_service.request.RegisterRequest;
import com.microservices.auth.auth_service.request.UserRequest;
import com.microservices.auth.auth_service.service.AuthService;
import com.microservices.auth.auth_service.utility.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

/** Author: Pranay Sanjay Mokal. Date: 02-05-2025 Project: Microservices */

@Service
public class AuthServiceImplementation implements AuthService {

  private final AuthenticationManager authenticationManager;
  private final UserDetailsService userDetailsService;
  private final PasswordEncoder passwordEncoder;

  @Autowired private RestClient.Builder restClientBuilder;

  @Autowired private JwtService jwtService;

  public AuthServiceImplementation(
      AuthenticationManager authenticationManager,
      UserDetailsService userDetailsService,
      PasswordEncoder passwordEncoder) {
    this.authenticationManager = authenticationManager;
    this.userDetailsService = userDetailsService;
    this.passwordEncoder = passwordEncoder;
  }

  public String login(String username, String password) {
    authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
    UserDetails userDetails = userDetailsService.loadUserByUsername(username);
    return jwtService.generateToken(userDetails);
  }

  public String refreshToken(String refreshToken) {
    // Add your logic to validate and refresh the token here (if needed)
    return "new-jwt-token"; // Placeholder for the new JWT token
  }

  public String register(RegisterRequest registerRequest) {

    UserRequest userRequest = new UserRequest();
    userRequest.setFirstName(registerRequest.getFirstName());
    userRequest.setLastName(registerRequest.getLastName());
    userRequest.setUsername(registerRequest.getUsername());
    userRequest.setEmail(registerRequest.getEmail());
    userRequest.setMobile(registerRequest.getMobile());
    userRequest.setPassword(registerRequest.getPassword());
    userRequest.setRole("ROLE_USER");

    restClientBuilder
        .baseUrl("http://USER-SERVICE")
        .build()
        .post()
        .uri("/user-service/v1/users/signup")
        .body(userRequest)
        .retrieve()
        .onStatus(
            HttpStatusCode::isError,
            (req, res) -> {
              throw new UserRegistrationException("User Registration Failed");
            })
        .toBodilessEntity();

    return "User Registered Successfully";
  }
}

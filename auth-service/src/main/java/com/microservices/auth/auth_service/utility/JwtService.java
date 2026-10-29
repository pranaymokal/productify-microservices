package com.microservices.auth.auth_service.utility;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/** Author: Pranay Sanjay Mokal. Date: 02-05-2025 Project: Microservices */

@Component
public class JwtService {

  @Value("${jwt.secret.key}")
  private String secretKey;

  public String generateToken(UserDetails userDetails) {
    SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));

    List<String> roles = userDetails.getAuthorities().stream()
        .map(GrantedAuthority::getAuthority)
        .collect(Collectors.toList());

    JwtBuilder jwtBuilder = Jwts.builder()
        .subject(userDetails.getUsername()) // Set username as subject
        .claim("roles", roles) // Add roles as claim
        .issuedAt(new Date()) // Set issue time
        .expiration(
            new Date(System.currentTimeMillis() + 1000 * 60 * 60)); // Set expiration time

    // Create and sign the token
    return jwtBuilder
        .signWith(key) // Sign with the HMAC key
        .compact();
  }

  public String extractUsername(String token) {
    SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    Claims claims = Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
    return claims.getSubject();
  }

  public boolean isTokenValid(String token, UserDetails userDetails) {
    return extractUsername(token).equals(userDetails.getUsername());
  }
}

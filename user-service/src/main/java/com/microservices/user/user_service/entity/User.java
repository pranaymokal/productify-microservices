package com.microservices.user.user_service.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "users")
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long userId;

  @Column(unique = true)
  private String username;

  private String password;

  @Column(unique = true)
  private String email;

  private String role;

  @Column(unique = true)
  private String mobile;

  private String firstName;

  private String lastName;
}

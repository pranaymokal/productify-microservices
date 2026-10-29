package com.microservices.auth.auth_service.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserRequest {

    private String username;

    private String password;

    private String email;

    private String role;

    private String mobile;

    private String firstName;

    private String lastName;
}

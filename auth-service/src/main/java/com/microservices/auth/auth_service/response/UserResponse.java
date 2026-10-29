package com.microservices.auth.auth_service.response;

import lombok.Data;

import java.util.List;

/** Author: Pranay Sanjay Mokal. Date: 03-05-2025 Project: Microservices */

@Data
public class UserResponse {

    private Long userId;

    private String username;

    private String password;

    private String email;

    private String role;

    private List<String> roles;

    private String mobile;

    public List<String> getRoles() {
        if (roles != null && !roles.isEmpty()) {
            return roles;
        }
        if (role != null && !role.isBlank()) {
            return List.of(role);
        }
        return List.of("ROLE_USER");
    }
}

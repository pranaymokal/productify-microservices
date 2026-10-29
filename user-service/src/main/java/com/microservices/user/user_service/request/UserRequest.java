package com.microservices.user.user_service.request;

import lombok.Data;

/** Author: Pranay Sanjay Mokal. Date: 03-05-2025 Project: Microservices */

@Data
public class UserRequest {

    private Long userId;

    private String username;

    private String password;

    private String email;

    private String role;

    private String mobile;

    private String firstName;

    private String lastName;
}

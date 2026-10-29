package com.microservices.auth.auth_service.service;

import com.microservices.auth.auth_service.request.RegisterRequest;

/** Author: Pranay Sanjay Mokal. Date: 02-05-2025 Project: Microservices */
public interface AuthService {


    public String login(String username, String password);

    public String refreshToken(String refreshToken);

    public String register(RegisterRequest registerRequest);
}

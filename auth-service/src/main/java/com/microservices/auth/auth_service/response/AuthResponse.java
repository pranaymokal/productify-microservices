package com.microservices.auth.auth_service.response;

import lombok.Data;

/** Author: Pranay Sanjay Mokal. Date: 02-05-2025 Project: Microservices */
@Data
public class AuthResponse {
    private String token;

    public AuthResponse(){

    }
    public AuthResponse(String token){
        this.token=token;
    }
}

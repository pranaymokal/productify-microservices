package com.microservices.user.user_service.service;

import com.microservices.user.user_service.entity.User;
import com.microservices.user.user_service.request.UserRequest;

import java.util.List;

/** Author: Pranay Sanjay Mokal. Date: 03-05-2025 Project: Microservices */

public interface UserService {

    public User signUp(UserRequest userRequest);

    public String signOut(String username);

    public User getByUsername(String username);

    List<User> getAllUsers();
}
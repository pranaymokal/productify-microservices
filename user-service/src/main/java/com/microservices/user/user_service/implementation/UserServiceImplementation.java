package com.microservices.user.user_service.implementation;

import com.microservices.user.user_service.entity.User;
import com.microservices.user.user_service.exception.ResourceNotFoundException;
import com.microservices.user.user_service.repository.UserRepository;
import com.microservices.user.user_service.request.UserRequest;
import com.microservices.user.user_service.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

/** Author: Pranay Sanjay Mokal. Date: 03-05-2025 Project: Microservices */
@Service
public class UserServiceImplementation implements UserService  {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User signUp(UserRequest userRequest) {
        if (userRepository.findByUsername(userRequest.getUsername()).isPresent()) {
            throw new RuntimeException("Username already exists");
        }

        User user = new User();
        user.setUsername(userRequest.getUsername());
        user.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        user.setEmail(userRequest.getEmail());
        String role = userRequest.getRole();
        if (role == null || role.isBlank()) {
            role = "ROLE_USER";
        }
        user.setRole(role);
        user.setMobile(userRequest.getMobile());
        user.setFirstName(userRequest.getFirstName());
        user.setLastName(userRequest.getLastName());
        return userRepository.save(user);
    }

    public String signOut(String username) {
        // Invalidate session or JWT if applicable (handled on client or token store)
        return "User signed out successfully";
    }

    public User getByUsername(String username) {
    return userRepository
        .findByUsername(username)
        .orElseThrow(() -> new ResourceNotFoundException("User not found with name:"+username));
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}

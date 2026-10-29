package com.microservices.user.user_service.controller;

import com.microservices.user.user_service.entity.User;
import com.microservices.user.user_service.request.UserRequest;
import com.microservices.user.user_service.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** Author: Pranay Sanjay Mokal. Date: 03-05-2025 Project: Microservices */

@RestController
@RequestMapping("user-service/v1/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<User> signUp(@RequestBody UserRequest userRequest) {
        return ResponseEntity.ok(userService.signUp(userRequest));
    }

    @PostMapping("/signout")
    public ResponseEntity<String> signOut(@RequestParam String username) {
        return ResponseEntity.ok(userService.signOut(username));
    }

    @GetMapping("/{username}")
    public ResponseEntity<User> getUser(@PathVariable String username) {
        return ResponseEntity.ok(userService.getByUsername(username));
    }

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }
}

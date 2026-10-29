package com.microservices.user.user_service.controller;

import com.microservices.user.user_service.entity.User;
import com.microservices.user.user_service.request.UserRequest;
import com.microservices.user.user_service.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import org.junit.jupiter.api.extension.ExtendWith;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private User user;
    private UserRequest userRequest;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUsername("testuser");
        userRequest = new UserRequest();
        userRequest.setUsername("testuser");
    }

    @Test
    void signUp() {
        when(userService.signUp(userRequest)).thenReturn(user);
        ResponseEntity<User> response = userController.signUp(userRequest);
        assertEquals(user, response.getBody());
        verify(userService, times(1)).signUp(userRequest);
    }

    @Test
    void getAllUsers() {
        when(userService.getAllUsers()).thenReturn(Collections.singletonList(user));
        ResponseEntity<List<User>> response = userController.getAllUsers();
        assertEquals(1, response.getBody().size());
        verify(userService, times(1)).getAllUsers();
    }
}
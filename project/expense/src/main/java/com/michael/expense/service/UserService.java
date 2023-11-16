package com.michael.expense.service;

import com.michael.expense.entity.User;
import com.michael.expense.payload.request.LoginRequest;
import com.michael.expense.payload.request.UserRequest;
import com.michael.expense.payload.response.JwtAuthResponse;
import com.michael.expense.payload.response.MessageResponse;
import com.michael.expense.payload.response.UserResponse;

import java.util.List;

public interface UserService {
    UserResponse createUser(UserRequest userRequest);

    JwtAuthResponse login(LoginRequest loginRequest);

    User getLoggedInUser();

    UserResponse getMyProfile();

    UserResponse getUserById(Long userId);

    UserResponse getUserByEmail(String email);

    UserResponse getUserByUsername(String username);

    List<UserResponse> getAllUsers();

    UserResponse updateUser(UserRequest userRequest);

    MessageResponse deleteUserProfile();
}

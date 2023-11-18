package com.michael.expense.service;

import com.michael.expense.entity.User;
import com.michael.expense.payload.request.ChangePasswordRequest;
import com.michael.expense.payload.request.LoginRequest;
import com.michael.expense.payload.request.UserRequest;
import com.michael.expense.payload.response.JwtAuthResponse;
import com.michael.expense.payload.response.MessageResponse;
import com.michael.expense.payload.response.UserResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;

public interface UserService {
    String createUser(UserRequest userRequest);

    User getLoggedInUser();

    UserResponse getMyProfile();

    UserResponse getUserById(Long userId);

    UserResponse getUserByEmail(String email);

    UserResponse getUserByUsername(String username);

    List<UserResponse> getAllUsers();

    UserResponse updateUser(UserRequest userRequest);

    MessageResponse deleteUserProfile();



}

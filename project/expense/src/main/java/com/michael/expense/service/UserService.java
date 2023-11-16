package com.michael.expense.service;

import com.michael.expense.payload.request.UserRequest;
import com.michael.expense.payload.response.MessageResponse;
import com.michael.expense.payload.response.UserResponse;

import java.util.List;

public interface UserService {
    UserResponse createUser(UserRequest userRequest);

    UserResponse getUserById(Long userId);

    UserResponse getUserByEmail(String email);

    UserResponse getUserByUsername(String username);

    //
    List<UserResponse> getAllUsers();

    UserResponse updateUser(Long userId, UserRequest userRequest);

    MessageResponse deleteUserById(Long userId);
}

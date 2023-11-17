package com.michael.expense.service;

import com.michael.expense.payload.request.LoginRequest;
import com.michael.expense.payload.response.JwtAuthResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface AuthService {

    JwtAuthResponse login(LoginRequest loginRequest);

    JwtAuthResponse refreshToken(HttpServletRequest request, HttpServletResponse response);
}

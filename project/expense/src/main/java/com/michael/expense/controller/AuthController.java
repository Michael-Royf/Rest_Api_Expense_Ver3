package com.michael.expense.controller;

import com.michael.expense.payload.request.ChangePasswordRequest;
import com.michael.expense.payload.request.EmailRequest;
import com.michael.expense.payload.request.LoginRequest;
import com.michael.expense.payload.response.JwtAuthResponse;
import com.michael.expense.payload.response.MessageResponse;
import com.michael.expense.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<JwtAuthResponse> login(@RequestBody @Valid LoginRequest loginRequest) {
        return ResponseEntity.ok(authService.login(loginRequest));
    }

    @PostMapping("/refresh_token")
    public JwtAuthResponse refreshToken(HttpServletRequest request,
                                        HttpServletResponse response) {
        return authService.refreshToken(request, response);
    }

    @PatchMapping("/change-password")
    public ResponseEntity<MessageResponse> changePassword(@RequestBody @Valid ChangePasswordRequest changePasswordRequest) {
        return new ResponseEntity<>(authService.changePassword(changePasswordRequest), HttpStatus.OK);
    }

    @PatchMapping("/reset-password")
    public ResponseEntity<MessageResponse> resetPassword(@RequestBody @Valid EmailRequest emailRequest) {
        return new ResponseEntity<>(authService.resetPassword(emailRequest.getEmail()), HttpStatus.OK);
    }

}

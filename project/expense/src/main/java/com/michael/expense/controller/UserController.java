package com.michael.expense.controller;

import com.michael.expense.payload.request.ChangePasswordRequest;
import com.michael.expense.payload.request.LoginRequest;
import com.michael.expense.payload.request.UserRequest;
import com.michael.expense.payload.response.JwtAuthResponse;
import com.michael.expense.payload.response.MessageResponse;
import com.michael.expense.payload.response.UserResponse;
import com.michael.expense.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;


    @PostMapping("/user/register")
    public ResponseEntity<String> registerUser(@RequestBody @Valid UserRequest userRequest) {
        return new ResponseEntity<>(userService.createUser(userRequest), CREATED);
    }



    @GetMapping("/user/get/my_profile")
    public ResponseEntity<UserResponse> getUserProfile() {
        return new ResponseEntity<>(userService.getMyProfile(), OK);
    }


    @GetMapping("/user/get/id/{userId}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable("userId") Long userId) {
        return new ResponseEntity<>(userService.getUserById(userId), OK);
    }


    @GetMapping("/user/get/email/{email}")
    public ResponseEntity<UserResponse> getUserByEmail(@PathVariable("email") String email) {
        return new ResponseEntity<>(userService.getUserByEmail(email), OK);
    }

    @GetMapping("/user/get/username/{username}")
    public ResponseEntity<UserResponse> getUserByUsername(@PathVariable("username") String username) {
        return new ResponseEntity<>(userService.getUserByUsername(username), OK);
    }

    @GetMapping("/user/get/allUsers")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return new ResponseEntity<>(userService.getAllUsers(), OK);
    }


    @PutMapping("/user/update_profile")
    public ResponseEntity<UserResponse> updateUser(@RequestBody @Valid UserRequest userRequest) {
        return new ResponseEntity<>(userService.updateUser(userRequest), OK);
    }

    @DeleteMapping("/user/remove_profile")
    public ResponseEntity<MessageResponse> removeUser() {
        return new ResponseEntity<>(userService.deleteUserProfile(), OK);
    }




}

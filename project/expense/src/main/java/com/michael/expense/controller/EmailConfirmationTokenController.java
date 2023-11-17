package com.michael.expense.controller;

import com.michael.expense.service.EmailConfirmationTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/api/v1/")
@RequiredArgsConstructor
public class EmailConfirmationTokenController {

    private final EmailConfirmationTokenService emailConfirmationTokenService;

    @GetMapping(path = "/confirm_email")
    public ResponseEntity<String> confirm(@RequestParam("token") String token) {
        return new ResponseEntity<>(emailConfirmationTokenService.confirmToken(token), OK);
    }
}

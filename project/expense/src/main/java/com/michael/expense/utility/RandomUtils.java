package com.michael.expense.utility;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class RandomUtils {

    public String generateTokenForEmailVerification() {
        return UUID.randomUUID().toString();
    }
}

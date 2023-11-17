package com.michael.expense.utility;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class RandomUtils {

    public String generateTokenForEmailVerification() {
        return UUID.randomUUID().toString();
    }

    public String generateUserId() {
        return RandomStringUtils.randomNumeric(10);
    }
}

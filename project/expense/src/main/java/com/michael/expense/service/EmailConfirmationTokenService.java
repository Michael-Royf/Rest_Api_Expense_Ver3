package com.michael.expense.service;

import com.michael.expense.entity.EmailConfirmationToken;

public interface EmailConfirmationTokenService {
    void saveConfirmationToken(EmailConfirmationToken token);

    String confirmToken(String token);
}

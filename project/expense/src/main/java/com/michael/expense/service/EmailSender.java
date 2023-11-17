package com.michael.expense.service;

public interface EmailSender {
    void sendEmailForVerification(String to, String email);

    void sendNewPassword( String email, String fullName, String password);
}

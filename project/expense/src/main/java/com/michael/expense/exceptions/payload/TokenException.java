package com.michael.expense.exceptions.payload;

public class TokenException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public TokenException(String message) {
        super(message);
    }
}

package com.michael.expense.exceptions.payload;

public class MonthNotFoundException extends RuntimeException{
    private static final long serialVersionUID = 1L;

    public MonthNotFoundException(String message) {
        super(message);
    }
}

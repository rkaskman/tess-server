package com.ttu.roman.service.exception;

public class InvalidExpenseException extends Exception {
    public InvalidExpenseException(String message) {
        super(message);
    }
}

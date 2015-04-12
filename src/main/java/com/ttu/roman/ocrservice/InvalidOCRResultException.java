package com.ttu.roman.ocrservice;

public class InvalidOCRResultException extends Exception {
    public InvalidOCRResultException(String message) {
        super(message);
    }

    public InvalidOCRResultException() {
        super();
    }
}

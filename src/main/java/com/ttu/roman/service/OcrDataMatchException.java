package com.ttu.roman.service;

public class OcrDataMatchException extends Exception{
    Integer errorCode;

    public OcrDataMatchException(String message, Integer errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public OcrDataMatchException(String message) {
        super(message);
    }
}

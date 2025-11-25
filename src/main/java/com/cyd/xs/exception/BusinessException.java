package com.cyd.xs.exception;


public class BusinessException extends RuntimeException {
    public BusinessException(String message) {
        super(message);
    }
}
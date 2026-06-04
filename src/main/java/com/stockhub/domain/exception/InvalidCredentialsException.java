package com.stockhub.domain.exception;

public class InvalidCredentialsException extends RuntimeException {

    public InvalidCredentialsException() {
        super("Credenciales invalidas");
    }

    public InvalidCredentialsException(String message) {
        super(message);
    }
}

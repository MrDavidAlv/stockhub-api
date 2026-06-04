package com.stockhub.infrastructure.mail;

public class MailDeliveryException extends RuntimeException {

    public MailDeliveryException(String message, Throwable cause) {
        super(message, cause);
    }
}

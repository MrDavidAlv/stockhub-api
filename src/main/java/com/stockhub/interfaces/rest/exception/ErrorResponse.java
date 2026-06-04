package com.stockhub.interfaces.rest.exception;

import java.time.OffsetDateTime;
import java.util.Map;

public record ErrorResponse(
        String code,
        String message,
        OffsetDateTime timestamp,
        Map<String, String> fieldErrors
) {

    public static ErrorResponse of(String code, String message) {
        return new ErrorResponse(code, message, OffsetDateTime.now(), null);
    }

    public static ErrorResponse of(String code, String message, Map<String, String> fieldErrors) {
        return new ErrorResponse(code, message, OffsetDateTime.now(), fieldErrors);
    }
}

package com.stockhub.application.dto;

public record TokenPairResponse(
        String accessToken,
        String refreshToken,
        String rol,
        String nombre,
        String email
) {
}

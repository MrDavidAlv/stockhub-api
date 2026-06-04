package com.stockhub.application.dto;

import java.time.LocalDateTime;

public record EmpresaResponse(
        String nit,
        String nombre,
        String direccion,
        String telefono,
        LocalDateTime createdAt
) {
}

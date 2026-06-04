package com.stockhub.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record EmpresaRequest(
        @NotBlank
        @Size(max = 20)
        @Pattern(regexp = "^[0-9A-Za-z-]+$", message = "NIT solo admite letras, numeros y guiones")
        String nit,

        @NotBlank
        @Size(max = 255)
        String nombre,

        @Size(max = 500)
        String direccion,

        @Size(max = 50)
        String telefono
) {
}

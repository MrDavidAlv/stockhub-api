package com.stockhub.application.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.Set;

public record ProductoRequest(
        @NotBlank
        @Size(max = 50)
        String codigo,

        @NotBlank
        @Size(max = 255)
        String nombre,

        String caracteristicas,

        @NotBlank
        @Size(max = 20)
        String empresaNit,

        Set<Long> categoriaIds,

        @Valid
        List<PrecioMonedaRequest> precios
) {
}

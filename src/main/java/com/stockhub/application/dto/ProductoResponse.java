package com.stockhub.application.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public record ProductoResponse(
        Long id,
        String codigo,
        String nombre,
        String caracteristicas,
        String empresaNit,
        String empresaNombre,
        Set<CategoriaResponse> categorias,
        List<PrecioMonedaResponse> precios,
        LocalDateTime createdAt
) {
}

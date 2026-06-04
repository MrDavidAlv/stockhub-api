package com.stockhub.application.dto;

import com.stockhub.domain.model.Moneda;

import java.math.BigDecimal;

public record PrecioMonedaResponse(
        Moneda moneda,
        BigDecimal precio
) {
}

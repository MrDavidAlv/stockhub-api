package com.stockhub.application.dto;

import com.stockhub.domain.model.Moneda;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public record PrecioMonedaRequest(
        @NotNull
        Moneda moneda,

        @NotNull
        @PositiveOrZero
        @Digits(integer = 16, fraction = 2)
        BigDecimal precio
) {
}

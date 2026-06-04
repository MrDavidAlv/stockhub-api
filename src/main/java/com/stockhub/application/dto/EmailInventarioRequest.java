package com.stockhub.application.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record EmailInventarioRequest(
        @NotBlank @Email String email,
        @Size(max = 20) String empresaNit
) {
}

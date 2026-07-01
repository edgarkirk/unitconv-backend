package com.example.unitconv.api.request;

import java.math.BigDecimal;
import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ConversionRequest(
    @NotNull UUID id,
    @NotNull BigDecimal value,
    @NotBlank String sourceUnit,
    @NotBlank String targetUnit
) {
}

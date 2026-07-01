package com.edgarkirk.unitconv.api.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ConversionRequest(
        @NotNull BigDecimal value,
        @NotBlank String sourceUnit,
        @NotBlank String targetUnit) {
}

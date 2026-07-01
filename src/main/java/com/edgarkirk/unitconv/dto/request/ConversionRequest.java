package com.edgarkirk.unitconv.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record ConversionRequest(
        @NotNull BigDecimal value,
        @NotBlank String sourceUnit,
        @NotBlank String targetUnit) {
}

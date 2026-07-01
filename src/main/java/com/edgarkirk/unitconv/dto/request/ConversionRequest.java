package com.edgarkirk.unitconv.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record ConversionRequest(
        @NotNull(message = "value is required.") BigDecimal value,
        @NotBlank(message = "sourceUnit is required.") String sourceUnit,
        @NotBlank(message = "targetUnit is required.") String targetUnit) {
}

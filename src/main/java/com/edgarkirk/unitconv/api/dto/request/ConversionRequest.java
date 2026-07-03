package com.edgarkirk.unitconv.api.dto.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ConversionRequest(
        @NotNull(message = "value is required") BigDecimal value,
        @NotBlank(message = "sourceUnit is required") String sourceUnit,
        @NotBlank(message = "targetUnit is required") String targetUnit) {
}

package com.edgarkirk.unitconv.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record ConversionRequest(
        @NotNull(message = "Missing required field: value") BigDecimal value,
        @NotBlank(message = "Missing required field: sourceUnit") String sourceUnit,
        @NotBlank(message = "Missing required field: targetUnit") String targetUnit) {
}

package com.edgarkirk.unitconv.api.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record ConversionRequest(
        @NotNull(message = "Missing required field: value")
        @Schema(description = "Numeric value to convert", requiredMode = Schema.RequiredMode.REQUIRED)
        BigDecimal value,
        @NotBlank(message = "Missing required field: sourceUnit")
        @Schema(description = "Source unit name", requiredMode = Schema.RequiredMode.REQUIRED)
        String sourceUnit,
        @NotBlank(message = "Missing required field: targetUnit")
        @Schema(description = "Target unit name", requiredMode = Schema.RequiredMode.REQUIRED)
        String targetUnit) {
}

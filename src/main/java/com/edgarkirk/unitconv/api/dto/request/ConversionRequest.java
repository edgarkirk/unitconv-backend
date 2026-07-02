package com.edgarkirk.unitconv.api.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record ConversionRequest(
        @Schema(description = "Numeric value to convert")
        @NotNull BigDecimal value,
        @Schema(description = "Source unit name")
        @NotBlank String sourceUnit,
        @Schema(description = "Target unit name")
        @NotBlank String targetUnit) {
}

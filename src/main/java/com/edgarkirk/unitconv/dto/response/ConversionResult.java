package com.edgarkirk.unitconv.dto.response;

import java.math.BigDecimal;
import java.util.UUID;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ConversionResult(
        @NotNull UUID id,
        @NotNull BigDecimal inputValue,
        @NotBlank String sourceUnit,
        @NotBlank String targetUnit,
        @NotNull BigDecimal result) {
}

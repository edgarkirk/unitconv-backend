package com.edgarkirk.unitconv.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.UUID;

public record ConversionRequest(
        @NotNull(message = "Field 'id' is required.") UUID id,
        @NotNull(message = "Field 'value' is required.") BigDecimal value,
        @NotBlank(message = "Field 'sourceUnit' is required.") String sourceUnit,
        @NotBlank(message = "Field 'targetUnit' is required.") String targetUnit) {
}

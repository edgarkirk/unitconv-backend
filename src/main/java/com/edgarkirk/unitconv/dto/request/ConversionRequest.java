package com.edgarkirk.unitconv.dto.request;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record ConversionRequest(
        @NotNull
        @JsonDeserialize(using = StrictBigDecimalDeserializer.class)
        @Schema(description = "Numeric value to convert", requiredMode = Schema.RequiredMode.REQUIRED)
        BigDecimal value,
        @NotBlank
        @Schema(description = "Source unit name", requiredMode = Schema.RequiredMode.REQUIRED)
        String sourceUnit,
        @NotBlank
        @Schema(description = "Target unit name", requiredMode = Schema.RequiredMode.REQUIRED)
        String targetUnit) {
}

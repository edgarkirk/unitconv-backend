package com.edgarkirk.unitconv.api.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.util.UUID;

public record ConversionResultResponse(
        @Schema(description = "Server-generated unique identifier", requiredMode = Schema.RequiredMode.REQUIRED)
        UUID id,
        @Schema(description = "Original input value", requiredMode = Schema.RequiredMode.REQUIRED)
        BigDecimal inputValue,
        @Schema(description = "Source unit name", requiredMode = Schema.RequiredMode.REQUIRED)
        String sourceUnit,
        @Schema(description = "Target unit name", requiredMode = Schema.RequiredMode.REQUIRED)
        String targetUnit,
        @Schema(description = "Converted numeric result", requiredMode = Schema.RequiredMode.REQUIRED)
        BigDecimal result) {
}

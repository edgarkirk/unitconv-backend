package com.edgarkirk.unitconv.api.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.util.UUID;

public record ConversionResultResponse(
        @Schema(description = "Server-generated unique identifier") UUID id,
        @Schema(description = "Original input value") BigDecimal inputValue,
        @Schema(description = "Source unit name") String sourceUnit,
        @Schema(description = "Target unit name") String targetUnit,
        @Schema(description = "Converted value") BigDecimal result) {
}

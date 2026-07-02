package com.edgarkirk.unitconv.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;

public record UnitResponse(
        @Schema(description = "Unique identifier", requiredMode = Schema.RequiredMode.REQUIRED)
        UUID id,
        @Schema(description = "Unit name", requiredMode = Schema.RequiredMode.REQUIRED)
        String name,
        @Schema(description = "Measurement system", requiredMode = Schema.RequiredMode.REQUIRED, allowableValues = {"metric", "imperial"})
        String system) {
}

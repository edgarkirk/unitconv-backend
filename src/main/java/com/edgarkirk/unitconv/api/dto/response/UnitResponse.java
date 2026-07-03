package com.edgarkirk.unitconv.api.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

public record UnitResponse(
        @Schema(description = "Server-generated unique identifier", requiredMode = Schema.RequiredMode.REQUIRED)
        UUID id,
        @Schema(description = "Unit name", requiredMode = Schema.RequiredMode.REQUIRED)
        String name,
        @Schema(description = "Measurement system", requiredMode = Schema.RequiredMode.REQUIRED)
        String system) {
}

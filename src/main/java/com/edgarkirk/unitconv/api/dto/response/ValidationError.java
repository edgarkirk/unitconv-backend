package com.edgarkirk.unitconv.api.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

public record ValidationError(
        @Schema(description = "Unique error identifier", requiredMode = Schema.RequiredMode.REQUIRED)
        UUID id,
        @Schema(description = "Human-readable error message", requiredMode = Schema.RequiredMode.REQUIRED)
        String message,
        @Schema(description = "Invalid field name, when applicable", nullable = true)
        String field) {
}

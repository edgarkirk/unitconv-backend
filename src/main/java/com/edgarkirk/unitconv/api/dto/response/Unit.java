package com.edgarkirk.unitconv.api.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

public record Unit(
        @Schema(description = "Unique identifier") UUID id,
        @Schema(description = "Unit name") String name,
        @Schema(description = "Measurement system") String system) {
}

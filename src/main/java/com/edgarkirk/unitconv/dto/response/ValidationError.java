package com.edgarkirk.unitconv.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ValidationError(
        @Schema(description = "Unique error identifier", format = "uuid", requiredMode = Schema.RequiredMode.REQUIRED)
        UUID id,
        @Schema(description = "Human-readable error message", requiredMode = Schema.RequiredMode.REQUIRED)
        String message,
        @Schema(description = "Name of the invalid field, or null if not field-specific", nullable = true)
        String field) {
}

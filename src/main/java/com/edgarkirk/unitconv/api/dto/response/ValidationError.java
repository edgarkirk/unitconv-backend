package com.edgarkirk.unitconv.api.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ValidationError(
        @Schema(description = "Unique error identifier") UUID id,
        @Schema(description = "Human-readable message") String message,
        @Schema(description = "Field name, when applicable") String field) {
}

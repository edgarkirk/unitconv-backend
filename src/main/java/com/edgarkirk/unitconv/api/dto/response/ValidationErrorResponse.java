package com.edgarkirk.unitconv.api.dto.response;

import java.util.UUID;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ValidationErrorResponse(UUID id, String message, String field) {
}

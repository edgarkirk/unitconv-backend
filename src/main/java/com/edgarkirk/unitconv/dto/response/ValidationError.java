package com.edgarkirk.unitconv.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ValidationError(UUID id, String message, String field) {
}

package com.example.unitconv.api.response;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(
    UUID id,
    String message,
    String field
) {
}

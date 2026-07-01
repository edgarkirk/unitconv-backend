package com.example.unitconv.api.response;

import java.util.UUID;

public record ErrorResponse(
    UUID id,
    String message,
    String field
) {
}

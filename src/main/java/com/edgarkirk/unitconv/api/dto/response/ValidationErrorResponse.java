package com.edgarkirk.unitconv.api.dto.response;

import java.util.UUID;

public record ValidationErrorResponse(UUID id, String message, String field) {
}

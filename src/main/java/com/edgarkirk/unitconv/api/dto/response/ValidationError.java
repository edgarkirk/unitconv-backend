package com.edgarkirk.unitconv.api.dto.response;

import java.util.UUID;

public record ValidationError(UUID id, String message, String field) {
}

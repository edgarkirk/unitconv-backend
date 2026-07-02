package com.edgarkirk.unitconv.dto.response;

import java.util.UUID;

public record ValidationError(UUID id, String message, String field) {
}

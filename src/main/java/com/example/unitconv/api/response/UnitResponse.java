package com.example.unitconv.api.response;

import java.util.UUID;

public record UnitResponse(
    UUID id,
    String name,
    String system
) {
}

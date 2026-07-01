package com.edgarkirk.unitconv.api.response;

import java.util.UUID;

public record Unit(
        UUID id,
        String name,
        String system) {
}

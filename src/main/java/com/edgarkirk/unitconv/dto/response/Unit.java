package com.edgarkirk.unitconv.dto.response;

import java.util.UUID;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record Unit(
        @NotNull UUID id,
        @NotBlank String name,
        @NotBlank String system) {
}
